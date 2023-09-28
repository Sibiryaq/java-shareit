package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Long> {

    @Query("SELECT it " +
            "FROM Item AS it " +
            "WHERE it.owner.id = :id " +
            "ORDER BY it.id")
    List<Item> findAllByOwnerId(Long id);

    @Query("SELECT it " +
            "FROM Item AS it " +
            "WHERE lower(it.name) LIKE lower(concat('%', :text, '%')) " +
            "OR lower(it.description) LIKE lower(concat('%', :text, '%')) " +
            "AND it.available = true")
    List<Item> itemsSearch(String text);
}
