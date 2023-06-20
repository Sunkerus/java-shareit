package ru.practicum.shareit.user.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;


public interface UserStorage extends JpaRepository<User, Long> {
}
