package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    ItemDtoResponse createItem(ItemDtoRequest itemDtoRequest, Long userId);

    ItemDtoResponse updateOwnItem(ItemDtoRequest itemDtoRequest, Long userId);

    ItemDtoResponse getItem(long itemId, Long userId);

    List<ItemDtoResponse> getOwnItems(Long userId);

    List<ItemDtoResponse> searchItems(String text, Long userId);

    CommentDtoResponse createComment(CommentDtoRequest comment, Long itemId, Long userId);
}

