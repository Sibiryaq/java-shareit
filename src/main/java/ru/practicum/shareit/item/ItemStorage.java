package ru.practicum.shareit.item;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {
    Optional<Item> getItemInfo(Long itemId);

    Item add(Item item);

    Item updateItem(Long itemId, Long userId, Item itemUpdate);

    Collection<Item> getById(Long userId);

    Collection<Item> getByKeyWords(String text);
}