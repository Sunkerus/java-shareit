package ru.practicum.shareit.user.impl;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ExistDataException;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.interfaces.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto createUser(UserDto user) {

        if (userStorage.existByEmail(user.getEmail())) {
            throw new ExistDataException("User with this email: " + user.getEmail() + " is exist");
        }
        return UserMapper.toDto(userStorage.saveUser(UserMapper.toUser(user)));
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {

        User user = userStorage.getByIdUser(userId)
                .orElseThrow(
                        () -> new NotFoundException("User:" + userId + " not found")
                );

        boolean isReplicated = userStorage.getAllUsers().stream()
                .anyMatch(u -> u.getEmail().equals(userDto.getEmail()) && !Objects.equals(u.getId(), userId));

        if (isReplicated) {
            throw new ExistDataException("User with this email: " + userDto.getEmail() + " is exist");
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
    public void deleteUser(Long userId) {
        userStorage.deleteByIdUser(userId);
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.toDto(userStorage.getByIdUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь " + userId + "не найден")));
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
