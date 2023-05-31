package ru.practicum.shareit.user;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.impl.UserServiceImpl;
import ru.practicum.shareit.user.interfaces.UserStorage;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

    private final UserDto userDto = new UserDto(1L, "Name", "Username@example.com");
    private final User user = new User(1L, "Name", "Username@example.com");
    private final Long userId = 1L;
    @Mock
    private UserStorage userStorage;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void checkUserIsCreateCorrectly() {
        when(userStorage.save(any())).thenReturn(user);

        UserDto validUser = userService.createUser(userDto);

        Assertions.assertEquals(userDto, validUser);
    }

    @Test
    void checkCorrectlyDeleteAllUsers() {
        userService.deleteUser(userId);
        verify(userStorage, times(1)).deleteById(userId);
    }

    @Test
    void updateUserIfUserNotFoundThrowNotFoundException() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> userService.updateUser(userDto, userId));
        Assertions.assertEquals("User with this id not found", exception.getMessage());
        verify(userStorage, never()).save(user);
    }

    @Test
    void checkUserIsUpdateCorrectly() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(userStorage.save(any())).thenReturn(user);

        UserDto validUser = userService.updateUser(userDto, userId);

        Assertions.assertEquals(userDto, validUser);
    }


    @Test
    void checkCorrectlyGetAllUsers() {
        when(userStorage.findAll()).thenReturn(List.of(user));

        List<UserDto> validUsers = userService.getAllUsers();

        Assertions.assertEquals(List.of(userDto), validUsers);
    }

    @Test
    void checkCorrectlyGetUserById() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto validUser = userService.getById(userId);

        Assertions.assertEquals(userDto, validUser);
    }
}