package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.interfaces.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.interfaces.ItemRequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ItemRequestStorageTest {

    @Autowired
    private UserStorage userStorage;

    @Autowired
    private ItemStorage itemStorage;

    @Autowired
    private ItemRequestStorage itemRequestStorage;

    private User requester;

    private ItemRequest itemRequest;

    @BeforeEach
    void startingBeforeEachTest() {
        User owner = new User(null,
                "FirstUsername",
                "FirstUsername@example.com");
        requester = new User(null,
                "SecondUsername",
                "SecondUsername@example.com");

        itemRequest = new ItemRequest(
                null,
                "Description",
                requester,
                LocalDateTime.of(2023, 6, 25, 12, 0));

        Item firstItem = new Item(null,
                owner, "Name",
                "Description",
                true,
                itemRequest);

        Item secondItem = new Item(null,
                owner, "Name",
                "Description",
                true,
                null);

        userStorage.save(requester);
        userStorage.save(owner);
        itemRequestStorage.save(itemRequest);

        itemStorage.save(firstItem);
        itemStorage.save(secondItem);

    }

    @Test
    void shouldSearchAllByRequesterIdWorkCorrectly() {
        List<ItemRequest> actualRequests =
                itemRequestStorage
                        .findAllByRequesterIdNot(requester.getId(),
                                PageRequest.of(0, 4));

        assertTrue(actualRequests.isEmpty());
    }

    @Test
    void shouldSearchAllByRequestIdAndSortingByCreatedD() {
        List<ItemRequest> actualRequests =
                itemRequestStorage
                        .findAllByRequesterIdOrderByCreatedDesc(requester.getId());

        assertEquals(List.of(itemRequest), actualRequests);
    }
}
