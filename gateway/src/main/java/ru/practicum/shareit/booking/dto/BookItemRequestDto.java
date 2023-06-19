package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {

    private long itemId;

    @NotNull(message = "start date cannot be null")
    @FutureOrPresent(message = "the start time must be future or present")
    private LocalDateTime start;

    @NotNull(message = "The end date cannot be null")
    @Future(message = "The end date of the booking cannot be in the past.")
    private LocalDateTime end;
}
