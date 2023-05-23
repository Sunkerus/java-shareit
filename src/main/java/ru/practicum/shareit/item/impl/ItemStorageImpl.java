package ru.practicum.shareit.item.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.interfaces.ItemStorage;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemStorageImpl implements ItemStorage {

    private Integer id = 1;

    private final Map<Integer, Item> storage = new HashMap<>();

    @Override
    public Item save(Item item) {
        item.setId(id++);
        storage.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        Integer id = item.getId();
        storage.put(id, item);
        return storage.get(id);
    }

    @Override
    public Item getById(Integer id) {
        return storage.get(id);
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(storage.values());
    }
}
