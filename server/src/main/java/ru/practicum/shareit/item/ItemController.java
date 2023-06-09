package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemSufficiencyDto;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.request.OverriddenPageRequest;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addNewItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) {
        return itemService.addNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemSufficiencyDto getItemById(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long itemId) {
        return itemService.getItemById(itemId, ownerId);
    }


    @GetMapping("/search")
    public List<ItemDto> getItemsBySearch(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return itemService.getItemBySearch(text, new OverriddenPageRequest(from, size));
    }


    @GetMapping
    public List<ItemSufficiencyDto> getItemByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(defaultValue = "0") Integer from,
                                                    @RequestParam(defaultValue = "10") Integer size) {
        return itemService.getItemByUserId(userId, new OverriddenPageRequest(from, size));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addNewComment(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @PathVariable Long itemId,
            @RequestBody CommentDto commentDto) {
        return itemService.addComment(bookerId, itemId, commentDto);
    }

}
