package ru.practicum.shareit.item.interfaces;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerIdOrderById(Long ownerId, Pageable pageable);

    List<Item> findAllByRequestId(Long requestId);

    List<Item> findAllByRequestIdIn(List<Long> requestIds);

    @Query("SELECT i FROM Item i " +
            "WHERE (LOWER(i.name) LIKE '%'||?1||'%' OR LOWER(i.description) LIKE '%'||?1||'%') AND i.available = TRUE")
    List<Item> searchByText(String text, Pageable pageable);

}
