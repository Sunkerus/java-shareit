package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.interfaces.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.interfaces.ItemRequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemStorageTest {

    @Autowired
    private ItemStorage itemStorage;

    @Autowired
    private UserStorage userStorage;

    @Autowired
    private ItemRequestStorage requestStorage;

    private User user;

    private ItemRequest itemRequest;

    private Item firstItem;

    private Item secondItem;

    @BeforeEach
    void setUp() {
        user = new User(null, "Name", "Username@example.com");
        userStorage.save(user);

        itemRequest = new ItemRequest(
                null,
                "Description",
                user,
                LocalDateTime.of(2023, 5, 5, 17, 15, 30));
        requestStorage.save(itemRequest);

        firstItem = new Item(null,
                user, "Name",
                "Description",
                true,
                itemRequest);

        secondItem = new Item(null,
                user, "Name",
                "SecondDescription",
                true,
                null);
        itemStorage.save(firstItem);
        itemStorage.save(secondItem);
    }


    @Test
    void shouldFindAllByOwnerIdCorrectlyOrderById() {
        List<Item> actualItems = itemStorage.findAllByOwnerIdOrderById(user.getId(), PageRequest.of(0, 2));

        assertEquals(List.of(firstItem, secondItem), actualItems);
    }


    @Test
    void shouldfindAllByRequestIdBeCorrectly() {
        List<Item> actualItems = itemStorage.findAllByRequestIdIn(List.of(itemRequest.getId()));

        assertEquals(List.of(firstItem), actualItems);
    }


    @Test
    void shouldFindAllByRequestIdBeCorrectly() {
        List<Item> actualItems = itemStorage.findAllByRequestId(itemRequest.getId());

        assertEquals(List.of(firstItem), actualItems);
    }
}
