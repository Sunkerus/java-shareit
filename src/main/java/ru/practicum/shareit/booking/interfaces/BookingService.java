package ru.practicum.shareit.booking.interfaces;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSufficiencyDto;

import java.util.List;

public interface BookingService {
    BookingSufficiencyDto approve(Long ownerId, Long bookingId, boolean isApproved);

    BookingSufficiencyDto save(Long userId, BookingDto bookingDto);

    BookingSufficiencyDto getById(Long userID, Long bookingId);

    List<BookingSufficiencyDto> getByOwner(Long ownerId, String state);

    List<BookingSufficiencyDto> getByBookerId(Long bookerId, String state);
}