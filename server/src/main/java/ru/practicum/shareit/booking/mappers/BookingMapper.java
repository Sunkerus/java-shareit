package ru.practicum.shareit.booking.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSufficiencyDto;
import ru.practicum.shareit.booking.models.Booking;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static Booking mapToBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd((bookingDto.getEnd()));
        return booking;
    }

    public static BookingSufficiencyDto mapToBookingSufficiencyDto(Booking booking) {
        return BookingSufficiencyDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus().name())
                .booker(UserMapper.toDto(booking.getBooker()))
                .item(ItemMapper.toDto(booking.getItem()))
                .build();
    }

    //convert booking to list of dto
    public static List<BookingSufficiencyDto> mapToBookingSufficiencyDto(Iterable<Booking> bookings) {
        List<BookingSufficiencyDto> bookingsDto = new ArrayList<>();

        for (Booking booking : bookings) {
            bookingsDto.add(mapToBookingSufficiencyDto(booking));
        }
        return bookingsDto;
    }
}