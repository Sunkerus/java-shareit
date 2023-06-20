package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;


@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestClient requestClient;

    @GetMapping
    public ResponseEntity<Object> getItemRequestByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get itemRequest by ownerId={}", userId);
        return requestClient.getItemRequestByOwner(userId);
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getItemRequestById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable(value = "id") Long requestId) {
        log.info("Get itemRequest by requestId={}, userId={}", requestId, userId);
        return requestClient.getItemRequestById(requestId, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer size) {
        log.info("Get all itemRequest, userId={}, from={}, size={}", userId, from, size);
        return requestClient.getAllItemRequests(from, size, userId);
    }


    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Creating itemRequest={}, userId={}", itemRequestDto, userId);
        return requestClient.createItemRequest(userId, itemRequestDto);
    }
}
