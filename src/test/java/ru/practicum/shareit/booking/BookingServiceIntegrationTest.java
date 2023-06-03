package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSufficiencyDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.booking.models.Booking;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.OverriddenPageRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.interfaces.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceIntegrationTest {

    private final ItemService itemService;
    private final UserService userService;

    private final BookingService bookingSerivce;


    @Test
    void shouldBookingServiceWorkCorrectly() {
        User user = new User(1L, "name", "username@example.com");
        User userSecond = new User(2L, "nameSecond", "userSecondname@example.com");

        ItemRequest request = new ItemRequest();
        request.setCreated(LocalDate.of(2001, 7, 1).atStartOfDay());
        request.setDescription("Description");
        request.setRequester(user);

        Item item = new Item();
        item.setAvailable(true);
        item.setDescription("Description");
        item.setId(1L);
        item.setName("Name");
        item.setRequest(request);

        Booking booking = new Booking(
                1L,
                item,
                userSecond,
                BookingStatus.WAITING,
                LocalDateTime.of(2023, 6, 25, 15, 0),
                LocalDateTime.of(2023, 6, 30, 10, 0));

        BookingDto bookingDto = new BookingDto(
                1L,
                item.getId(),
                user.getId(),
                null,
                LocalDateTime.of(2023, 6, 25, 15, 0),
                LocalDateTime.of(2023, 6, 30, 10, 0));


        userService.createUser(UserMapper.toDto(user));
        userService.createUser(UserMapper.toDto(userSecond));
        itemService.addNewItem(item.getId(), ItemMapper.toDto(item));


        BookingSufficiencyDto savingBooking = bookingSerivce.save(userSecond.getId(), bookingDto);


        assertEquals(savingBooking, BookingMapper.mapToBookingSufficiencyDto(booking));
        assertEquals(bookingSerivce.getById(1L, 1L), BookingMapper.mapToBookingSufficiencyDto(booking));
        assertEquals(bookingSerivce.getByOwner(user.getId(), "ALL", new OverriddenPageRequest(0, 4)),
                List.of(BookingMapper.mapToBookingSufficiencyDto(booking)));

        booking.setStatus(BookingStatus.APPROVED);
        assertEquals(bookingSerivce.approve(user.getId(), booking.getId(), true), BookingMapper.mapToBookingSufficiencyDto(booking));
    }


}
