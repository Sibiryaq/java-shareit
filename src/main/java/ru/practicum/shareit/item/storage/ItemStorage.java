package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Long> {

    @Query("SELECT it " +
            "FROM Item AS it " +
            "WHERE it.owner.id = :id " +
            "ORDER BY it.id")
    Page<Item> findAllByOwnerId(Long id, Pageable pageable);

    @Query("SELECT it " +
            "FROM Item AS it " +
            "WHERE lower(it.name) LIKE lower(concat('%', :text, '%')) " +
            "OR lower(it.description) LIKE lower(concat('%', :text, '%')) " +
            "AND it.available = true")
    Page<Item> itemsSearch(String text, Pageable pageable);

    List<Item> findAllByRequestId(Long requestId);
}
