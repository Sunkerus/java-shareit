package ru.practicum.shareit.user.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserStorageImpl implements UserStorage {

    private final Map<Long, User> storage = new HashMap<>();

    private Long id = 1L;

    @Override
    public User saveUser(User user) {
        user.setId(id++);
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        Long id = user.getId();
        storage.put(id, user);
        return storage.get(id);
    }

    @Override
    public Optional<User> getByIdUser(Long userId) {
        return Optional.ofNullable(storage.get(userId));
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteByIdUser(Long id) {
        storage.remove(id);
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return storage.values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }


}
