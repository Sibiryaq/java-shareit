package ru.practicum.shareit.item;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto dto, long ownerId) throws NotFoundException;

    ItemDto patchItem(ItemDto dto, long ownerId, long itemId);

    ItemDto getItem(long itemId, long ownerId);

    List<ItemDto> getAllItemsByOwner(long ownerId);

    List<ItemDto> searchItem(String text, long ownerId);

    Comment addComment(Comment dto, long itemId, long authorId);
}
