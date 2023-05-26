package ru.practicum.shareit.item.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.interfaces.ItemStorage;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemStorageImpl implements ItemStorage {

    private Long id = 1L;

    private final Map<Long, Item> storage = new HashMap<>();

    @Override
    public Item save(Item item) {
        item.setId(id++);
        storage.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        Long id = item.getId();
        storage.put(id, item);
        return storage.get(id);
    }

    @Override
    public Item getById(Long id) {
        return storage.get(id);
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<ItemDto> getItemByUser(Long userId) {
        return storage
                .values()
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }
}
