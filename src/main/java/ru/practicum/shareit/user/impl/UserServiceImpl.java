package ru.practicum.shareit.user.impl;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.User;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.existDataException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    /**
     * creating user
     * @param user object UserDto
     * @return create and returns UserDto Object
     */

    @Override
    public UserDto createUser(UserDto user) {

        boolean isExist = userStorage
                .getAllUsers()
                .stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()));

        if (isExist){
            throw new existDataException("User with this email: " + user.getEmail() + " is exist");
        }
        return UserMapper.toDto(userStorage.saveUser(UserMapper.toUser(user)));
    }

    @Override
    public UserDto updateUser(UserDto userDto, Integer userId) {

        User user = userStorage.getByIdUser(userId)
                .orElseThrow(
                        () -> new RuntimeException("Пользователь не найден")
                );


        boolean isExist = userStorage
                .getAllUsers()
                .stream()
                .anyMatch(u -> u.getEmail().equals(userDto.getEmail()));

        if (isExist) {
            throw new existDataException("User with this email: " + userDto.getEmail() + " is exist");
        }

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        return UserMapper.toDto(userStorage.updateUser(user));
    }

    @Override
    public void deleteUser(Integer userId) {
        userStorage.deleteByIdUser(userId);
    }

    @Override
    public UserDto getUserById(Integer userId) {
        return UserMapper.toDto(userStorage.getByIdUser(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден")));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userStorage
                .getAllUsers()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }
}
