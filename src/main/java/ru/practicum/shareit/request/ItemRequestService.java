package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    List<ItemRequestDto> getAllItemRequests(Integer from, Integer size, Long userId);

    ItemRequestDto getItemRequestById(Long requestId, Long userId);

    List<ItemRequestDto> getItemRequestByOwnerId(Long userId);

    ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto);

}