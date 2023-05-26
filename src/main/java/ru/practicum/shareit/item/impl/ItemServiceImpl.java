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
import ru.practicum.shareit.user.interfaces.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

    private final UserStorage userStorage;

    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) throws IncorrectDataException {

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userStorage.getByIdUser(userId).orElseThrow(
                        () -> new NotFoundException("User with this id didn't find ")
                )
        );

        return ItemMapper.toDto(itemStorage.save(item));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) throws IncorrectDataException {

        Item item = itemStorage.getById(itemId);

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Owner with that id does not meet the requirements");
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
    public ItemDto getItemById(Long id) {
        return ItemMapper.toDto(itemStorage.getById(id));
    }

    @Override
    public List<ItemDto> getItemByUserId(Long userId) {
        return itemStorage.getItemByUser(userId);
    }

    @Override
    public List<ItemDto> getItemBySearch(String text) {
        if (text.isBlank()) {
            return List.of();
        }

        String textC = text.toLowerCase();

        return itemStorage.getAll()
                .stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(textC)
                        || item.getDescription().toLowerCase().contains(textC))
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());

    }


}

