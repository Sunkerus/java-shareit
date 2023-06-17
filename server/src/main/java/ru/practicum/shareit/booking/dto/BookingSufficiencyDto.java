package ru.practicum.shareit.booking.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookingSufficiencyDto {

    private Long id;

    private String status;

    private UserDto booker;

    private ItemDto item;

    private LocalDateTime start;

    private LocalDateTime end;
}
