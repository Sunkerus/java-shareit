package ru.practicum.shareit.item.interfaces;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemSufficiencyDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto addNewItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);


    ItemSufficiencyDto getItemById(Long itemId, Long ownerId);

    List<ItemSufficiencyDto> getItemByUserId(Long userId, Pageable pageable);

    List<ItemDto> getItemBySearch(String text, Pageable pageable);

    CommentDto addComment(Long bookerId, Long itemId, CommentDto commentDto);

}

