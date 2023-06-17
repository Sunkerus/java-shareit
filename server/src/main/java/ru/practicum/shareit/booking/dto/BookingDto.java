package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {

    private Long id;

    private Long itemId;

    private Long bookerId;

    private String status;

    @NotNull(message = "start date cannot be null")
    @FutureOrPresent(message = "the start time must be future or present")
    private LocalDateTime start;

    @NotNull(message = "The end date cannot be null")
    @Future(message = "The end date of the booking cannot be in the past.")
    private LocalDateTime end;

}