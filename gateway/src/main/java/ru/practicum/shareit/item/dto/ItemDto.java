package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemDto {

    @NotBlank(message = "ThatName cannot be Blank")
    private String name;

    @NotBlank(message = "The description cannot be Blank")
    private String description;

    @NotNull(message = "Available cannot be Null")
    private Boolean available;

    private Long requestId;
}
