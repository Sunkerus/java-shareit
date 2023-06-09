package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentStorage extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItemIdOrderByCreated(Long itemId);

    List<Comment> findByItemIdInOrderByItemId(List<Long> itemIds);
}

