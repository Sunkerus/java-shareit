package ru.practicum.shareit.item.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.models.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemSufficiencyDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null);
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public static ItemSufficiencyDto toSufficiencyDto(Item item,
                                                      Booking next,
                                                      Booking last,
                                                      List<CommentDto> comments) {
        return new ItemSufficiencyDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                last != null ? new ItemSufficiencyDto.BookingDto(last.getId(), last.getBooker().getId()) : null,
                next != null ? new ItemSufficiencyDto.BookingDto(next.getId(), next.getBooker().getId()) : null,
                comments != null ? comments : List.of());
    }

    public static List<ItemDto> mapToDto(Iterable<Item> items) {
        List<ItemDto> listDtoOfItem = new ArrayList<>();
        if (items == null) {
            return List.of();
        }

        for (Item item : items) {
            listDtoOfItem.add(toDto(item));
        }
        return listDtoOfItem;
    }


}
