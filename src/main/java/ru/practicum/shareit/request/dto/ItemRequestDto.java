package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ItemRequestDto {

    private Long id;

    @NotBlank(message = "Name couldn't be blank")
    private String name;

    @NotBlank(message = "description couldn't be blank")
    private String description;

    @NotNull(message = "Aviable status couldn't be null")
    private Boolean available;

    private User requester;

    private LocalDateTime created;
}
