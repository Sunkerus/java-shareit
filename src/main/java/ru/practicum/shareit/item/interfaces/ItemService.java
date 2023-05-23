package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addNewItem(Integer userId, ItemDto itemDto);

    ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto);

    ItemDto getItemById(Integer id);

    List<ItemDto> getItemByUserId(Integer userId);

    List<ItemDto> getItemBySearch(String text);


}
