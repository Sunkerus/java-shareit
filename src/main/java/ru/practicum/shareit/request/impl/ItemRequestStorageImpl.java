package ru.practicum.shareit.request.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.interfaces.ItemRequestStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemRequestStorageImpl implements ItemRequestStorage {

    private final Map<Long, ItemRequest> storage = new HashMap<>();

    private Long id = 1L;

    @Override
    public ItemRequest save(ItemRequest itemRequest) {
        itemRequest.setId(id++);
        storage.put(itemRequest.getId(), itemRequest);
        return itemRequest;
    }

    @Override
    public ItemRequest update(ItemRequest itemrequest) {
        Long id = itemrequest.getId();
        storage.put(id, itemrequest);
        return storage.get(id);
    }

    @Override
    public ItemRequest getById(Long id) {
        return storage.get(id);
    }

    @Override
    public List<ItemRequest> getAll() {
        return new ArrayList<>(storage.values());
    }
}
