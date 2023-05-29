package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemSufficiencyDto;

import java.util.List;

public interface ItemService {

    ItemDto addNewItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);


    ItemSufficiencyDto getItemById(Long itemId, Long ownerId);

    List<ItemSufficiencyDto> getItemByUserId(Long userId);

    List<ItemDto> getItemBySearch(String text);

    CommentDto addNewComment(Long bookerId, Long itemId, CommentDto commentDto);


}

