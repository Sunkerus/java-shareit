package ru.practicum.shareit.item.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.interfaces.BookingStorage;
import ru.practicum.shareit.booking.models.Booking;
import ru.practicum.shareit.exceptions.IncorrectDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.CommentStorage;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemSufficiencyDto;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.interfaces.ItemStorage;
import ru.practicum.shareit.item.mappers.CommentMapper;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.interfaces.ItemRequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

    private final UserStorage userStorage;

    private final BookingStorage bookingStorage;

    private final CommentStorage commentStorage;

    private final ItemRequestStorage itemRequestStorage;

    @Override
    @Transactional
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("User with this id didn't found"));
        ItemRequest itemRequest = itemDto.getRequestId() != null ?
                itemRequestStorage.findById(itemDto.getRequestId()).orElse(null) : null;

        item.setOwner(user);
        item.setRequest(itemRequest);

        return ItemMapper.toDto(itemStorage.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id=%d not found", itemId)));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException(
                    String.format("Item with id: %d does not belong with id=%d", itemId, userId));
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

        return ItemMapper.toDto(itemStorage.save(item));
    }

    @Override
    public ItemSufficiencyDto getItemById(Long itemId, Long ownerId) {
        Item item = itemStorage.findById(itemId).orElseThrow(() -> new NotFoundException(
                String.format("Item with ID=%d was not found.", itemId)));

        List<Booking> bookings = bookingStorage
                .findByItemIdAndItemOwnerIdAndStatusNotOrderByStartDesc(itemId, ownerId, BookingStatus.REJECTED);
        List<CommentDto> comments = CommentMapper.mapToCommentDto(commentStorage.findAllByItemIdOrderByCreated(itemId));

        return ItemMapper.toSufficiencyDto(item, getNextBooking(bookings), getLastBooking(bookings), comments);
    }

    @Override
    public List<ItemSufficiencyDto> getItemByUserId(Long userId, Pageable pageable) {
        List<Item> items = itemStorage.findAllByOwnerIdOrderById(userId, pageable);
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        Map<Long, List<Comment>> comments = commentStorage.findByItemIdInOrderByItemId(itemIds)
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        Map<Long, List<Booking>> bookings = bookingStorage
                .findAllByItemOwnerIdAndStatusNotOrderByStartDesc(userId, BookingStatus.REJECTED)
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        return items
                .stream()
                .map(item -> ItemMapper.toSufficiencyDto(
                        item,
                        getNextBooking(bookings.getOrDefault(item.getId(), List.of())),
                        getLastBooking(bookings.getOrDefault(item.getId(), List.of())),
                        CommentMapper.mapToCommentDto(comments.get(item.getId()))
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemBySearch(String text, Pageable pageable) {
        if (text.isBlank()) {
            return List.of();
        }

        return itemStorage.searchByText(text.toLowerCase(), pageable)
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long bookerId, Long itemId, CommentDto commentDto) {
        Booking booking = bookingStorage.findDistinctBookingByBookerIdAndItemId(bookerId, itemId)
                .stream()
                .filter(u -> u.getStatus().name().equals("APPROVED"))
                .findAny().orElseThrow(() -> new IncorrectDataException(
                        String.format(
                                "A user with ID=%d cannot leave a comment because he/she didn't rent this item.", bookerId)));


        if (booking.getEnd().isAfter(LocalDateTime.now())) {
            throw new IncorrectDataException("A comment can be left only after the end of the rent period.");
        }

        Comment comment = CommentMapper.mapToComment(commentDto);
        comment.setAuthor(booking.getBooker());
        comment.setItem(booking.getItem());

        return CommentMapper.mapToCommentDto(commentStorage.save(comment));
    }

    private Booking getLastBooking(List<Booking> bookings) {
        return bookings.stream()
                .filter(u -> u.getStart().isBefore(LocalDateTime.now()))
                .findFirst()
                .orElse(null);
    }

    private Booking getNextBooking(List<Booking> bookings) {
        return bookings
                .stream()
                .filter(u -> u.getStart().isAfter(LocalDateTime.now()))
                .min(comparing(Booking::getEnd))
                .orElse(null);
    }

    @Override
    public Item getItemByIdAllField(Long id) {
        return itemStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Item with " + id + " was not found!"));
    }
}