package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto user);

    UserDto updateUser(UserDto userDto, Long userId);

    void deleteUser(Long userId);

    UserDto getById(Long userId);

    List<UserDto> getAllUsers();


}