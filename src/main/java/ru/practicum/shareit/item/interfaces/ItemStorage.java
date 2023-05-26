package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item save(Item item);

    Item update(Item item);

    Item getById(Long id);

    List<Item> getAll();

    List<ItemDto> getItemByUser(Long userId);
}
