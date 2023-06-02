package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemSufficiencyDto;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.request.OverriddenPageRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.interfaces.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceIntegrationalTest {

    private final ItemService itemService;
    private final UserService userService;
    @Test
    @SneakyThrows
    void shouldAllItemsAddAndReturnsCorrectly() {

        UserDto user = new UserDto(1L,"name","userName@mail.ru");
        Long userId = userService.createUser(user).getId();

        UserDto userTakeDto = userService.getById(userId);

        ItemDto item = new ItemDto(1L,"name","description",true,null);
        itemService.addNewItem(userTakeDto.getId(), item);

        ItemSufficiencyDto itemOut = itemService.getItemById(1L, 1L);

        assertThat(itemOut.getId(), notNullValue());
        assertThat(itemOut.getName(), equalTo(item.getName()));
        assertThat(itemOut.getDescription(), equalTo(item.getDescription()));

        List<ItemSufficiencyDto> items = itemService.getItemByUserId(userTakeDto.getId(), new OverriddenPageRequest(0,4));

        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getName(), equalTo(user.getName()));
        assertThat(itemOut.getDescription(), equalTo(item.getDescription()));
        assertThat(itemOut.getName(), equalTo(userTakeDto.getName()));

        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getName(), equalTo(user.getName()));
    }

    @Test
    @SneakyThrows
    void shouldItemUpdateCorrectly() {
        UserDto user = new UserDto(1L,"name","userName@mail.ru");
        Long userId = userService.createUser(user).getId();

        UserDto userTakeDto = userService.getById(userId);

    }



}
