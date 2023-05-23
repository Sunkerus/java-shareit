package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item save(Item item);

    Item update(Item item);

    Item getById(Integer id);

    List<Item> getAll();


}
