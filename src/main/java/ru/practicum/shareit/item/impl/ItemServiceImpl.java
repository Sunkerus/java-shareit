package ru.practicum.shareit.item.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.IncorrectDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.interfaces.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.interfaces.UserService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

    private final UserService userService;

    @Override
    public ItemDto addNewItem(Integer userId, ItemDto itemDto) throws IncorrectDataException {
        checkId(userId);
        userService.getUserById(userId);

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userId);

        return ItemMapper.toDto(itemStorage.save(item));
    }

    @Override
    public ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto) throws IncorrectDataException {
        checkId(userId);
        userService.getUserById(userId);

        Item item = itemStorage.getById(itemId);

        if (!item.getOwner().equals(userId)) {
            throw new NotFoundException("Item with that id coudn't found");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toDto(itemStorage.update(item));

    }

    @Override
    public ItemDto getItemById(Integer id) {
        return ItemMapper.toDto(itemStorage.getById(id));
    }

    @Override
    public List<ItemDto> getItemByUserId(Integer userId) {
        return itemStorage.getAll()
                .stream()
                .filter(item -> Objects.equals(item.getOwner(), userId))
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemBySearch(String text) {
        if (text.isBlank()) {
            return List.of();
        }

        return itemStorage.getAll()
                .stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());

    }

    private void checkId(Integer userId) {
        if (userId == null) {
            throw new IncorrectDataException("id of user is null");
        }
    }
}

