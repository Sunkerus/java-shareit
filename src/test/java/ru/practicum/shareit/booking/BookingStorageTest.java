package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.interfaces.BookingStorage;
import ru.practicum.shareit.booking.models.Booking;
import ru.practicum.shareit.item.interfaces.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.interfaces.ItemRequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserStorage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.booking.enums.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.enums.BookingStatus.REJECTED;

@DataJpaTest
class BookingStorageTest {
    private final PageRequest page = PageRequest.of(0, 4);
    @Autowired
    BookingStorage bookingStorage;
    @Autowired

    UserStorage userStorage;
    @Autowired

    ItemRequestStorage requestStorage;
    @Autowired

    ItemStorage itemStorage;
    private User owner;
    private User booker;
    private Item item;
    private Booking bookingFuture;
    private Booking bookingPast;
    private Booking bookingCurrent;

    @BeforeEach
    void startingBeforeEachTest() {
        owner = new User(null, "name", "usernameFirst.doe@example.org");
        booker = new User(null, "Name", "usernameSecond.doe@example.org");

        ItemRequest request = new ItemRequest();
        request.setCreated(LocalDate.of(1970, 1, 1).atStartOfDay());
        request.setDescription("Description");
        request.setRequester(booker);

        item = new Item();
        item.setAvailable(true);
        item.setDescription("Description");
        item.setName("Name");
        item.setOwner(owner);
        item.setRequest(request);

        bookingPast = new Booking(null,
                item,
                booker,
                REJECTED,
                LocalDateTime.of(2023, 5, 5, 12, 0),
                LocalDateTime.of(2023, 5, 10, 12, 0));

        bookingFuture = new Booking(null,
                item,
                booker,
                APPROVED,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(10));

        bookingCurrent = new Booking(null,
                item,
                booker,
                APPROVED,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().plusDays(10));

        userStorage.save(owner);
        userStorage.save(booker);

        requestStorage.save(request);
        itemStorage.save(item);

        bookingStorage.save(bookingPast);
        bookingStorage.save(bookingCurrent);
        bookingStorage.save(bookingFuture);
    }

    @Test
    void findItemIdAndItemOwnerIditIsNotOrderByStartDesc() {
        List<Booking> actualBookings = bookingStorage
                .findByItemIdAndItemOwnerIdAndStatusNotOrderByStartDesc(
                        item.getId(),
                        owner.getId(),
                        REJECTED);

        assertEquals(List.of(bookingFuture, bookingCurrent), actualBookings);
    }

    @Test
    void findAllByStartDescByBookerIdAndStartIsAfterAndEndIsAfterOrder() {
        List<Booking> actualBookings = bookingStorage
                .findAllByBookerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc(
                        booker.getId(),
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        page);

        assertEquals(List.of(bookingFuture), actualBookings);
    }

    @Test
    void findByStartDescByItemOwnerIdAndStatusOrder() {
        List<Booking> actualBookings = bookingStorage
                .findByItemOwnerIdAndStatusOrderByStartDesc(
                        owner.getId(),
                        APPROVED,
                        page);

        assertEquals(List.of(bookingFuture, bookingCurrent), actualBookings);
    }

    @Test
    void findByStartDescByItemOwnerIdAndStartIsAfterAndEndIsAfterOrder() {
        List<Booking> actualBookings = bookingStorage
                .findByItemOwnerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc(
                        owner.getId(),
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        page);

        assertEquals(List.of(bookingFuture), actualBookings);
    }


    @Test
    void findByStartDescByItemOwnerIdAndEndIsBeforeOrder() {
        List<Booking> actualBookings = bookingStorage
                .findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(owner.getId(),
                        LocalDateTime.now(),
                        page);

        assertEquals(List.of(bookingPast), actualBookings);
    }

    @Test
    void findByStartDescByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrder() {
        List<Booking> actualBookings = bookingStorage
                .findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                        owner.getId(),
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        page);

        assertEquals(List.of(bookingCurrent), actualBookings);
    }

    @Test
    void findAllByStartDescByItemOwnerIdCheckCorrectOfOrder() {
        List<Booking> actualBookings = bookingStorage
                .findAllByItemOwnerIdOrderByStartDesc(owner.getId(),
                        page);

        assertEquals(List.of(bookingFuture, bookingCurrent, bookingPast), actualBookings);
    }


    @Test
    void findAllByStartDescByItemOwnerIdCheckStatusNotOrder() {
        List<Booking> actualBookings = bookingStorage
                .findAllByItemOwnerIdAndStatusNotOrderByStartDesc(
                        owner.getId(),
                        REJECTED);

        assertEquals(List.of(bookingFuture, bookingCurrent), actualBookings);
    }


    @Test
    void findDByBookerIdAndItemIdistinctBooking() {
        List<Booking> actualBookings = bookingStorage
                .findDistinctBookingByBookerIdAndItemId(
                        booker.getId(),
                        item.getId());

        assertEquals(List.of(bookingCurrent, bookingPast), actualBookings);
    }

    @Test
    void findByBookerIdOrderBookingsByStartDesc() {
        List<Booking> actualBookings = bookingStorage
                .findByBookerIdOrderByStartDesc(
                        booker.getId(),
                        page);

        assertEquals(List.of(bookingFuture, bookingCurrent, bookingPast), actualBookings);
    }


    @Test
    void findBeforeOrderByStartDescByBookerIdAndEnd() {
        PageRequest page = PageRequest.of(0, 4);
        List<Booking> actualBookings = bookingStorage
                .findByBookerIdAndEndIsBeforeOrderByStartDesc(
                        booker.getId(),
                        LocalDateTime.now(),
                        page);

        assertEquals(List.of(bookingPast), actualBookings);
    }


    @Test
    void findByStartDescByBookerIdAndStartIsBeforeAndEndIsAfterOrder() {
        List<Booking> actualBookings = bookingStorage
                .findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                        booker.getId(),
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        page);

        assertEquals(List.of(bookingCurrent), actualBookings);
    }


    @Test
    void findByStartDescByBookerIdAndStatusOrder() {
        List<Booking> actualBookings = bookingStorage
                .findByBookerIdAndStatusOrderByStartDesc(
                        booker.getId(),
                        APPROVED,
                        page);

        assertEquals(List.of(bookingFuture, bookingCurrent), actualBookings);
    }


}