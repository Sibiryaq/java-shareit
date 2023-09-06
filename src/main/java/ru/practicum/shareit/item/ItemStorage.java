package ru.practicum.shareit.item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Optional<Item> getItemInfo(Long itemId);

    Item add(Item item);

    Item updateItem(Long itemId, Long userId, Item itemUpdate);

    List<Item> getById(Long userId);

    List<Item> getByKeyWords(String text);
}
