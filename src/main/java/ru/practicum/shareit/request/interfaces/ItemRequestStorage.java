package ru.practicum.shareit.request.interfaces;

import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public interface ItemRequestStorage {

    ItemRequest save(ItemRequest itemRequest);

    ItemRequest update(ItemRequest itemRequest);

    ItemRequest getById(Long id);

    List<ItemRequest> getAll();


}
