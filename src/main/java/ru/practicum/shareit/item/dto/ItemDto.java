package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ItemDto {

    private Integer id;

    @NotBlank(message = "Name couldn't be blank")
    private String name;

    @NotBlank(message = "description couldn't be blank")
    private String description;

    @NotNull(message = "Aviable status couldn't be null")
    private Boolean available;

    private Integer requestId;
}