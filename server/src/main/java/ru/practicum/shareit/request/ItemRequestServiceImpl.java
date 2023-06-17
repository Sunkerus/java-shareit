package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.interfaces.ItemStorage;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.interfaces.ItemRequestStorage;
import ru.practicum.shareit.user.interfaces.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserStorage userStorage;

    private final ItemStorage itemStorage;

    private final ItemRequestStorage itemRequestStorage;


    private void validPermit(Long userId) {
        userStorage.findById(userId).orElseThrow(
                () -> new NotFoundException("You do not have permission"));
    }

    @Override
    public ItemRequestDto getItemRequestById(Long requestId, Long userId) {
        validPermit(userId);

        ItemRequest itemRequest = itemRequestStorage.findById(requestId).orElseThrow(() -> new NotFoundException(
                String.format("Request with ID=%d was not found.", requestId)));

        List<ItemDto> items = ItemMapper.mapToDto(itemStorage.findAllByRequestId(itemRequest.getId()));
        return ItemRequestMapper.toDto(itemRequest, items);
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Integer from, Integer size, Long userId) {
        validPermit(userId);
        List<ItemRequest> itemRequests = itemRequestStorage
                .findAllByRequesterIdNot(userId, new OverriddenPageRequest(from, size, Sort.by("created")
                        .descending()));

        List<Long> requestIds = itemRequests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        Map<Long, List<ItemDto>> items = ItemMapper.mapToDto(itemStorage.findAllByRequestIdIn(requestIds))
                .stream()
                .collect(Collectors.groupingBy(ItemDto::getRequestId));

        return itemRequests
                .stream()
                .map(itemRequest -> ItemRequestMapper.toDto(
                        itemRequest,
                        items.getOrDefault(itemRequest.getId(), List.of())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getItemRequestByOwnerId(Long userId) {
        validPermit(userId);
        List<ItemRequest> itemRequests = itemRequestStorage.findAllByRequesterIdOrderByCreatedDesc(userId);
        List<Long> requestIds = itemRequests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        Map<Long, List<Item>> items = itemStorage.findAllByRequestIdIn(requestIds)
                .stream().collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return itemRequests
                .stream()
                .map(itemRequest -> ItemRequestMapper.toDto(
                        itemRequest, ItemMapper.mapToDto(items.get(itemRequest.getId()))))
                .collect(Collectors.toList());
    }


    @Override
    public ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        validPermit(userId);

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, userStorage.findById(userId).get());
        return ItemRequestMapper.toDto(itemRequestStorage.save(itemRequest), List.of());
    }


}
