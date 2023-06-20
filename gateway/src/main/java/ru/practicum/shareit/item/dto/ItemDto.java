package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemDto {

    @NotBlank(message = "Name couldn't be blank")
    private String name;

    @NotBlank(message = "description couldn't be blank")
    private String description;

    @NotNull(message = "Aviable status couldn't be null")
    private Boolean available;

    private Long requestId;
}
