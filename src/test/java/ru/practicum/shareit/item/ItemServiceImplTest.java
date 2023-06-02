package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.interfaces.BookingStorage;
import ru.practicum.shareit.booking.models.Booking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemSufficiencyDto;
import ru.practicum.shareit.item.impl.ItemServiceImpl;
import ru.practicum.shareit.item.interfaces.ItemStorage;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.OverriddenPageRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.enums.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.enums.BookingStatus.REJECTED;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {


    private final Long userId = 1L;
    private final Long itemId = 1L;
    private final User user = new User(1L, "Name", "Username@gmail.com");
    private final ItemRequest itemRequest = new ItemRequest(
            2L,
            "name + description",
            user,
            LocalDateTime.of(2023, 6, 30, 12, 0));
    private final Item item = new Item(
            1L,
            user,
            "name",
            "name + description2",
            true,
            itemRequest);
    private final Comment comment = new Comment(
            1L,
            "Add comment from user1",
            item,
            user,
            LocalDateTime.of(2023, 5, 31, 13, 0));
    private final Booking booking = new Booking(
            1L,
            item,
            user,
            APPROVED,
            LocalDateTime.of(2023, 5, 25, 12, 0),
            LocalDateTime.of(2023, 5, 30, 12, 0));
    private final CommentDto commentDto = new CommentDto(
            1L,
            "Add comment from user1",
            "Name",
            LocalDateTime.of(2023, 5, 31, 13, 0));
    @Captor
    ArgumentCaptor<Item> argCap;
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemStorage itemStorage;
    @Mock
    private UserStorage userStorage;
    @Mock
    private BookingStorage bookingStorage;
    @Mock
    private CommentStorage commentStorage;

    @Test
    void shouldGetItemByIdWhenItemNotFoundThenThrowNotFoundException() {
        when(itemStorage.findById(anyLong())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () ->
                itemService.getItemById(itemId, userId));
        verify(bookingStorage, never())
                .findByItemIdAndItemOwnerIdAndStatusNotOrderByStartDesc(itemId, userId, REJECTED);
        verify(commentStorage, never()).findAllByItemIdOrderByCreated(itemId);
    }


    @Test
    void shouldGetItemByUserId() {
        when(itemStorage.findAllByOwnerIdOrderById(anyLong(), any(Pageable.class))).thenReturn(List.of(item));
        when(commentStorage.findByItemIdInOrderByItemId(anyList())).thenReturn(List.of(comment));
        when(bookingStorage
                .findAllByItemOwnerIdAndStatusNotOrderByStartDesc(
                        anyLong(), any(BookingStatus.class)))
                .thenReturn(List.of(booking));

        List<ItemSufficiencyDto> actualItems = itemService.getItemByUserId(userId, new OverriddenPageRequest(0, 2));


        assertEquals(1, actualItems.size());
        assertEquals(item.getId(), actualItems.get(0).getId());
        verify(commentStorage, times(1)).findByItemIdInOrderByItemId(List.of(itemId));
        verify(bookingStorage, times(1))
                .findAllByItemOwnerIdAndStatusNotOrderByStartDesc(userId, BookingStatus.REJECTED);

    }

    @Test
    void shouldGetItemById() {
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));

        ItemSufficiencyDto actualItem = itemService.getItemById(itemId, userId);

        assertEquals(item.getId(), actualItem.getId());
        verify(bookingStorage, times(1))
                .findByItemIdAndItemOwnerIdAndStatusNotOrderByStartDesc(itemId, userId, REJECTED);
        verify(commentStorage, times(1)).findAllByItemIdOrderByCreated(itemId);
    }

    @Test
    void shouldGetItemBySearch() {
        when(itemStorage.searchByText(anyString(), any(Pageable.class))).thenReturn(List.of(item));

        List<ItemDto> actualItems = itemService.getItemBySearch("дрель", new OverriddenPageRequest(0, 2));


        assertEquals(item.getId(), actualItems.get(0).getId());
        assertEquals(1, actualItems.size());
        assertEquals(item.getName(), actualItems.get(0).getName());
        verify(itemStorage, times(1)).searchByText(anyString(), any(Pageable.class));
    }


    @Test
    void shouldAddComment() {
        when(bookingStorage.findDistinctBookingByBookerIdAndItemId(anyLong(), anyLong())).thenReturn(List.of(booking));
        when(commentStorage.save(any())).thenReturn(comment);

        CommentDto actualComment = itemService.addComment(userId, itemId, commentDto);

        assertEquals(comment.getId(), actualComment.getId());
        assertEquals(comment.getText(), actualComment.getText());

        verify(bookingStorage, times(1)).findDistinctBookingByBookerIdAndItemId(anyLong(), anyLong());
        verify(commentStorage, times(1)).save(any());
    }

    @Test
    void shouldTestThrowNotFoundExceptionWhenItemisntAppersToOwner() {
        ItemDto newItem = new ItemDto(
                1L,
                "New name",
                "newDesctiption",
                true,
                2L);
        String message = "Item with id: 1 does not belong with id=2";
        Long wrongUserId = 2L;
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(wrongUserId, itemId,
                        newItem));
        assertEquals(message, exception.getMessage());
        verify(itemStorage, never()).save(argCap.capture());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemUserNotFound() {

        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));

        ItemDto newItem = new ItemDto(
                33L,
                "Name",
                "Description",
                true,
                2L);

        NullPointerException ex = assertThrows(NullPointerException.class, () ->
                itemService.addNewItem(userId, newItem));

        verify(itemStorage, never()).save(item);
    }


}
