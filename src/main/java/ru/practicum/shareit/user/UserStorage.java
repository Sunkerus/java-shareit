package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User saveUser(User user);

    void deleteByIdUser(Integer id);

    User updateUser(User user);

    Optional<User> getByIdUser(Integer id);

    List<User> getAllUsers();



}
