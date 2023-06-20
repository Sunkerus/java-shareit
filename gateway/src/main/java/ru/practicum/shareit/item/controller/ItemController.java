package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;


    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long itemId) {
        log.info("Get item by itemId={}, ownerId={}", itemId, ownerId);
        return itemClient.getItemById(itemId, ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsBySearch(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam String text,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer size) {
        log.info("Searching item={}, from{}, size{}, userId={}", text, from, size, userId);
        return itemClient.getItemBySearch(text, userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto) {
        log.info("Update item={}, itemId{}, userId={}", itemDto, itemId, userId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemByUserId(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) @Max(100) Integer size) {
        log.info("Get item by userId={}, from={}, size={}", userId, from, size);
        return itemClient.getItemByUserId(userId, from, size);
    }


    @PostMapping
    public ResponseEntity<Object> addNewItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody @Valid ItemDto itemDto) {
        log.info("Creating item={}, userid={}", itemDto, userId);
        return itemClient.addNewItem(userId, itemDto);
    }


    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addNewComment(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @PathVariable Long itemId,
            @RequestBody @Valid CommentDto commentDto) {
        log.info("Creating comment={}, bookerId={}, itemId={}", commentDto, bookerId, itemId);
        return itemClient.addNewComment(bookerId, itemId, commentDto);
    }


}
