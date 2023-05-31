package ru.practicum.shareit.booking.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSufficiencyDto;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.request.OverriddenPageRequest;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;


@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;


    @PostMapping
    public BookingSufficiencyDto save(
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
    public BookingSufficiencyDto getById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping("/owner")
    public List<BookingSufficiencyDto> getByOwner(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        return bookingService.getByOwner(ownerId, state, new OverriddenPageRequest(from, size));
    }

    @GetMapping
    public List<BookingSufficiencyDto> getBookingsByUBooker(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        return bookingService.getByBookerId(bookerId, state, new OverriddenPageRequest(from, size));
    }


}