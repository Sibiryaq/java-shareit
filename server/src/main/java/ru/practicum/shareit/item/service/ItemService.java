package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import java.util.List;

public interface ItemService {

    ItemDtoResponse createItem(ItemDtoRequest itemDtoRequest, Long userId);

    ItemDtoResponse updateOwnItem(ItemDtoRequest itemDtoRequest, Long userId);

    ItemDtoResponse getItem(long itemId, Long userId);

    List<ItemDtoResponse> getOwnItems(Long userId, Integer from, Integer size);

    List<ItemDtoResponse> searchItems(String text, Long userId, Integer from, Integer size);

    CommentDtoResponse createComment(CommentDtoRequest comment, Long itemId, Long userId);
}
