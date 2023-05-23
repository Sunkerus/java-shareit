package ru.practicum.shareit.user.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.*;

@Repository
public class UserStorageImpl implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    private Integer id = 1;

    @Override
    public User saveUser(User user) {
        user.setId(id++);
        users.put(user.getId(),user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(),user);
        return users.get(user.getId());
    }

    @Override
    public Optional<User> getByIdUser(Integer userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteByIdUser(Integer id) {
        users.remove(id);
    }

}
