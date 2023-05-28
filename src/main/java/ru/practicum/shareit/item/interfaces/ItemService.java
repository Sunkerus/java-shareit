package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemSufficiencyDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto addNewItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);


    ItemSufficiencyDto getItemById(Long itemId, Long ownerId);

    Item getItemByIdAllField(Long id);

    List<ItemSufficiencyDto> getItemByUserId(Long userId);

    List<ItemDto> getItemBySearch(String text);

    CommentDto addNewComment(Long bookerId, Long itemId, CommentDto commentDto);


}

