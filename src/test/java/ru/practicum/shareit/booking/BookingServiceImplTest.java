package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSufficiencyDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.interfaces.BookingStorage;
import ru.practicum.shareit.booking.models.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.IncorrectDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.interfaces.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.OverriddenPageRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.interfaces.UserStorage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    private static User user;
    private static ItemRequest request;
    private static Item item;
    private static BookingDto bookingDto;
    private static BookingSufficiencyDto bookingSufficiencyDto;
    private static Booking booking;
    private final Long ownerId = 1L;
    private final Long userId = 2L;
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingStorage bookingStorage;
    @Mock
    private ItemStorage itemStorage;
    @Mock
    private UserStorage userStorage;

    @BeforeAll
    static void startingBeforeTest() {
        UserDto userDto = new UserDto(1L, "name", "username@example.com");

        user = new User(1L, "name", "username@example.com");

        request = new ItemRequest();
        request.setCreated(LocalDate.of(2001, 7, 1).atStartOfDay());
        request.setDescription("Description");
        request.setId(1L);
        request.setRequester(user);

        item = new Item();
        item.setAvailable(true);
        item.setDescription("Description");
        item.setId(1L);
        item.setName("Name");
        item.setOwner(user);
        item.setRequest(request);


        ItemDto itemDto = new ItemDto(
                1L,
                "Name",
                "Description",
                true,
                request.getId());

        bookingDto = new BookingDto(
                1L,
                item.getId(),
                user.getId(),
                null,
                LocalDateTime.of(2023, 6, 25, 15, 0),
                LocalDateTime.of(2023, 6, 30, 10, 0));

        bookingSufficiencyDto = new BookingSufficiencyDto(
                1L,
                "APPROVED",
                userDto,
                itemDto,
                LocalDateTime.of(2023, 6, 25, 15, 0),
                LocalDateTime.of(2023, 6, 30, 10, 0));

        booking = new Booking(
                1L,
                item,
                user,
                BookingStatus.APPROVED,
                LocalDateTime.of(2023, 6, 25, 15, 0),
                LocalDateTime.of(2023, 6, 30, 10, 0));
    }


    @Test
    void whenWhenEndTimeBeforeThenStartThenThrowIncorrectDataException() {
        BookingDto invalidDateBooking = new BookingDto(
                null,
                item.getId(),
                user.getId(),
                null,
                LocalDateTime.of(2023, 6, 25, 15, 0),
                LocalDateTime.of(2023, 6, 20, 10, 0));

        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(userStorage.findById(userId)).thenReturn(Optional.of(user));

        IncorrectDataException exception = assertThrows(IncorrectDataException.class,
                () -> bookingService.save(userId, invalidDateBooking));

        assertEquals("Booking date end cannot be before start date", exception.getMessage());
        verify(bookingStorage, never()).save(booking);
    }


    @Test
    void saveWhenEndStartDateIsSameEndDateThenThrowIncorrectDataException() {

        BookingDto invalidDateBooking = new BookingDto(
                null,
                item.getId(),
                user.getId(),
                null,
                LocalDateTime.of(2023, 6, 25, 15, 0),
                LocalDateTime.of(2023, 6, 25, 15, 0));

        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(userStorage.findById(userId)).thenReturn(Optional.of(user));

        IncorrectDataException exception = assertThrows(IncorrectDataException.class,
                () -> bookingService.save(userId, invalidDateBooking));

        assertEquals("Booking date end cannot be ident start date", exception.getMessage());
        verify(bookingStorage, never()).save(booking);
    }


    @Test
    void saveWhenItemIsNotFoundThenThrowNotFoundException() {
        when(itemStorage.findById(anyLong())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
                () -> bookingService.save(userId, bookingDto));
        verify(bookingStorage, never()).save(booking);
    }

    @Test
    void saveWhenAllParametersIsCorrect() {
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(userStorage.findById(userId)).thenReturn(Optional.of(user));
        when(bookingStorage.save(any())).thenReturn(booking);


        BookingSufficiencyDto actualBooking = bookingService.save(userId, bookingDto);

        assertEquals(bookingSufficiencyDto, actualBooking);

    }


    @Test
    void saveWhenItemWasUnavailableThenThrowIncorrectDataException() {

        Item unavailableItem = new Item();
        unavailableItem.setAvailable(false);
        unavailableItem.setDescription("Description");
        unavailableItem.setId(1L);
        unavailableItem.setName("Name");
        unavailableItem.setOwner(user);
        unavailableItem.setRequest(request);

        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(unavailableItem));
        when(userStorage.findById(userId)).thenReturn(Optional.of(user));

        IncorrectDataException exception = assertThrows(IncorrectDataException.class,
                () -> bookingService.save(userId, bookingDto));

        assertEquals("That item: 1 cannot be booked", exception.getMessage());
        verify(bookingStorage, never()).save(booking);
    }


    @Test
    void approveWhenDataForBookingServiceIsCorrect() {

        booking.setStatus(BookingStatus.WAITING);

        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingStorage.save(any())).thenReturn(booking);

        BookingSufficiencyDto actualBooking = bookingService.approve(ownerId, 1L, true);

        assertEquals(bookingSufficiencyDto, actualBooking);
    }

    @Test
    void getByIdWhenDataIsCorrect() {
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingSufficiencyDto actualBooking = bookingService.getById(ownerId, 1L);

        assertEquals(bookingSufficiencyDto, actualBooking);
    }


    @Test
    void getByIdWhenWrongItemOwnerAndBookerThenThrowNotFoundException() {
        Long wrongUserId = 2L;
        String message = "Item must be owned by the owner of the item or the author of the booking";
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getById(wrongUserId, 1L));
        assertEquals(message, exception.getMessage());
    }


    @Test
    void getByBookerIdIfBookingStateIsAllThenReturnBookingByState() {
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));

        when(bookingStorage.findByBookerIdOrderByStartDesc(anyLong(), any(OverriddenPageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingSufficiencyDto> actualBookings = bookingService
                .getByBookerId(ownerId, "ALL", new OverriddenPageRequest(0, 4));

        assertEquals(List.of(bookingSufficiencyDto), actualBookings);
    }

    @Test
    void approveWhenBookingNotFoundThenThrowNotFoundException() {

        when(bookingStorage.findById(anyLong())).thenReturn(Optional.empty());

        String message = "Booking with id = 1 not found";

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.approve(ownerId, 1L, true));
        assertEquals(message, exception.getMessage());
        verify(bookingStorage, never()).save(booking);
    }


    @Test
    void approveWhenOtherItemOwnerThenThrowNotFoundException() {
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));

        Long wrongUserId = 2L;
        String message = "Only the owner of an item can approve or deny a rental request.";

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.approve(wrongUserId, 1L, true));
        assertEquals(message, exception.getMessage());
        verify(bookingStorage, never()).save(booking);
    }


    @Test
    void getByBookerIdIfBookingStateIsFUTUREThenReturnBookingByState() {
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingStorage.findAllByBookerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc(
                anyLong(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(OverriddenPageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingSufficiencyDto> actualBookings = bookingService
                .getByBookerId(ownerId, "FUTURE", new OverriddenPageRequest(0, 2));

        assertEquals(List.of(bookingSufficiencyDto), actualBookings);
    }


    @Test
    void getByBookerIdIfBookingStateCURRENTThenReturnBookingByState() {
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingStorage.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                anyLong(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(OverriddenPageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingSufficiencyDto> actualBookings = bookingService
                .getByBookerId(ownerId, "CURRENT", new OverriddenPageRequest(0, 2));

        assertEquals(List.of(bookingSufficiencyDto), actualBookings);
    }


    @Test
    void getByBookerIdWhenBookingStateIsBeUnsupportedThenReturnIncorrectDataException() {

        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        IncorrectDataException exception = assertThrows(IncorrectDataException.class,
                () -> bookingService.getByBookerId(ownerId, "UNSUPPORTED", new OverriddenPageRequest(0, 2)));
        assertEquals("Unknown state: UNSUPPORTED", exception.getMessage());

    }


    @Test
    void getByOwnerWhenBookingStateIsBeALLReturnBookingByState() {

        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingStorage.findAllByItemOwnerIdOrderByStartDesc(anyLong(), any(OverriddenPageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingSufficiencyDto> actualBooking = bookingService
                .getByOwner(ownerId, "ALL", new OverriddenPageRequest(0, 2));

        assertEquals(List.of(bookingSufficiencyDto), actualBooking);
    }


    @Test
    void getByBookerIdIfBookingStatePASTThenReturnBookingByState() {
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingStorage.findByBookerIdAndEndIsBeforeOrderByStartDesc(
                anyLong(),
                any(LocalDateTime.class),
                any(OverriddenPageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingSufficiencyDto> actualBookings = bookingService
                .getByBookerId(ownerId, "PAST", new OverriddenPageRequest(0, 2));

        assertEquals(List.of(bookingSufficiencyDto), actualBookings);
    }


    @Test
    void getByBookerIdIfBookingHadStatusThenReturnBookingSortByStatus() {
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingStorage
                .findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class), any(OverriddenPageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingSufficiencyDto> actualBookings = bookingService
                .getByBookerId(ownerId, "APPROVED", new OverriddenPageRequest(0, 2));

        assertEquals(List.of(bookingSufficiencyDto), actualBookings);
    }


    @Test
    void getByOwnerWhenBookingBeWithStatusReturnBookingByStatus() {

        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingStorage
                .findByItemOwnerIdAndStatusOrderByStartDesc(
                        anyLong(),
                        any(BookingStatus.class),
                        any(OverriddenPageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingSufficiencyDto> actualBooking = bookingService
                .getByOwner(ownerId, "APPROVED", new OverriddenPageRequest(0, 2));

        assertEquals(List.of(bookingSufficiencyDto), actualBooking);
    }


    @Test
    void getByOwnerWhenBookingStateUnsupportedThenReturnNotFoundException() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));

        IncorrectDataException exception = assertThrows(IncorrectDataException.class,
                () -> bookingService.getByOwner(ownerId, "UNSUPPORTED", new OverriddenPageRequest(0, 2)));
        assertEquals("Unknown state: UNSUPPORTED", exception.getMessage());

    }

    @Test
    void getByOwnerIDWhenBookingStateIsBeFUTUREReturnBookingByState() {

        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingStorage
                .findByItemOwnerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc(
                        anyLong(),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class),
                        any(OverriddenPageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingSufficiencyDto> actualBooking = bookingService
                .getByOwner(ownerId, "FUTURE", new OverriddenPageRequest(0, 2));

        assertEquals(List.of(bookingSufficiencyDto), actualBooking);
    }


    @Test
    void getByOwnerIdWhenBookingStateIsPASTReturnBookingByState() {

        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingStorage
                .findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(
                        anyLong(),
                        any(LocalDateTime.class),
                        any(OverriddenPageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingSufficiencyDto> actualBooking = bookingService
                .getByOwner(ownerId, "PAST", new OverriddenPageRequest(0, 2));

        assertEquals(List.of(bookingSufficiencyDto), actualBooking);
    }


    @Test
    void getByOwnerWhenBookingStateIsCURRENTThenReturnBookingByState() {

        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingStorage
                .findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                        anyLong(),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class),
                        any(OverriddenPageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingSufficiencyDto> actualBooking = bookingService
                .getByOwner(ownerId, "CURRENT", new OverriddenPageRequest(0, 2));


        assertEquals(List.of(bookingSufficiencyDto), actualBooking);
    }


}