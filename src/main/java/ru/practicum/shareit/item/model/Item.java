package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    private Integer id;

    @NotBlank(message = "Name couldn't be blank")
    private String name;

    @NotBlank(message = "Description couldn't be blank")
    private String description;

    @NotNull(message = "Available status couldn't be null")
    private Boolean available;

    private Integer owner;

    private ItemRequest request;
}
