package ru.practicum.shareit.user;
import  ru.practicum.shareit.user.dto.*;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto user);

    UserDto updateUser(UserDto userDto, Integer userId);

    void deleteUser(Integer userId);

    UserDto getUserById(Integer userId);

    List<UserDto> getAllUsers();



}
