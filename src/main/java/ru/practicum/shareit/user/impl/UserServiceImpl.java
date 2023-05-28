package ru.practicum.shareit.user.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.interfaces.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        return UserMapper.toDto(userStorage.save(UserMapper.toUser(userDto)));
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto, Long userId) {
        User user = userStorage
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("User with this id not found"));

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        return UserMapper.toDto(userStorage.save(user));
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.toDto(userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with this id not found")));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userStorage.findAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userStorage.deleteById(userId);
    }
}