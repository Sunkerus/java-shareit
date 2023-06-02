package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.interfaces.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.interfaces.ItemRequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    private final Long userId = 1L;
    @InjectMocks
    private ItemRequestServiceImpl requestService;
    @Mock
    private ItemRequestStorage itemRequestStorage;
    @Mock
    private UserStorage userStorage;
    @Mock
    private ItemStorage itemStorage;
    private ItemRequestDto requestDto;
    private ItemRequest request;
    private ItemDto itemDto;
    private Item item;
    private User user;

    @BeforeEach
    void setUp() {

        user = new User(1L, "Name", "Username@example.com");

        itemDto = new ItemDto(
                1L,
                "Name",
                "Description",
                true,
                2L);

        requestDto = new ItemRequestDto(
                2L,
                "Description",
                List.of(),
                LocalDateTime.of(2023, 10, 10, 10, 10));

        request = new ItemRequest(
                2L,
                "Description",
                user,
                LocalDateTime.of(2023, 10, 10, 10, 10));

        item = new Item(
                1L,
                user,
                "Name",
                "Description",
                true,
                request);
    }

    @Test
    void shouldGetCorrectlyItemRequestById() {
        requestDto.setItems(List.of(itemDto));
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestStorage.findById(anyLong())).thenReturn(Optional.of(request));
        when(itemStorage.findAllByRequestId(anyLong())).thenReturn(List.of(item));

        ItemRequestDto actualResult = requestService.getItemRequestById(request.getId(), userId);

        Assertions.assertEquals(requestDto, actualResult);

        verify(userStorage, times(1)).findById(anyLong());
        verify(itemRequestStorage, times(1)).findById(anyLong());
        verify(itemStorage, times(1)).findAllByRequestId(anyLong());
    }

    @Test
    void shouldCorrectlyCreateItemRequest() {
        requestDto.setCreated(null);
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestStorage.save(any())).thenReturn(request);

        ItemRequestDto actualRequest = requestService.createItemRequest(userId, requestDto);

        Assertions.assertNotNull(actualRequest.getCreated());
        Assertions.assertEquals(requestDto.getId(), actualRequest.getId());


        verify(userStorage, times(2)).findById(any());
        verify(itemRequestStorage, times(1)).save(any());
    }


    @Test
    void createItemRequestWhenUserIsNotFoundThenThrowNotFoundException() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> requestService.createItemRequest(userId, requestDto));

        Assertions.assertEquals("You do not have permission", exception.getMessage());
        verify(itemRequestStorage, never()).save(any(ItemRequest.class));
        verify(userStorage, times(1)).findById(anyLong());
    }


    @Test
    void shouldGetCorrectlyAllItemRequests() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestStorage.findAllByRequesterIdNot(anyLong(), any(OverriddenPageRequest.class)))
                .thenReturn(List.of(request));
        when(itemStorage.findAllByRequestIdIn(anyList())).thenReturn(List.of());

        List<ItemRequestDto> actualRequests = requestService.getAllItemRequests(0, 2, userId);

        Assertions.assertEquals(List.of(requestDto), actualRequests);

        verify(userStorage, times(1)).findById(anyLong());
        verify(itemRequestStorage, times(1)).findAllByRequesterIdNot(anyLong(), any(OverriddenPageRequest.class));
    }

    @Test
    void shouldGetCorrectlyItemRequestByOwnerId() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestStorage.findAllByRequesterIdOrderByCreatedDesc(anyLong())).thenReturn(List.of(request));
        when(itemStorage.findAllByRequestIdIn(anyList())).thenReturn(List.of());

        List<ItemRequestDto> actualRequests = requestService.getItemRequestByOwnerId(userId);

        Assertions.assertEquals(List.of(requestDto), actualRequests);

        verify(userStorage, times(1)).findById(anyLong());
        verify(itemRequestStorage,times(1)).findAllByRequesterIdOrderByCreatedDesc(anyLong());
        verify(itemStorage,times(1)).findAllByRequestIdIn(anyList());
    }

    @Test
    void getItemRequestByIdIfNotFoundThenReturnNotFoundException() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestStorage.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> requestService.getItemRequestById(request.getId(), userId));
        Assertions.assertEquals("Request with ID=2 was not found.", exception.getMessage());

        verify(itemStorage, never()).findAllByRequestId(anyLong());
        verify(itemRequestStorage, times(1)).findById(anyLong());
    }
}