package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User saveUser(User user);

    void deleteByIdUser(Long id);

    User updateUser(User user);

    Optional<User> getByIdUser(Long id);

    List<User> getAllUsers();

    boolean existByEmail(String email);

}
