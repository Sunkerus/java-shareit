package ru.practicum.shareit.booking.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSufficiencyDto;
import ru.practicum.shareit.booking.interfaces.BookingService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;


    @PostMapping
    public BookingSufficiencyDto saveBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody @Valid BookingDto bookingDto) {
        return bookingService.save(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingSufficiencyDto approve(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved) {
        return bookingService.approve(ownerId, bookingId, approved);
    }


    @GetMapping("/{bookingId}")
    public BookingSufficiencyDto getBookingById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId) {
        return bookingService.getById(userId, bookingId);
    }


    @GetMapping
    public List<BookingSufficiencyDto> getBookingsByUBooker(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getByBookerId(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingSufficiencyDto> getBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getByOwner(ownerId, state);
    }
}