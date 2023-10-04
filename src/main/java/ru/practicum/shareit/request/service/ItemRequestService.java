package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoWItemResponse;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDtoResponse createRequest(ItemRequestDtoRequest itemRequestDtoRequest, Long userId);

    List<ItemRequestDtoWItemResponse> findByUserId(Long userId);

    List<ItemRequestDtoWItemResponse> findFromAll(Integer from, Integer size, Long userId);

    ItemRequestDtoWItemResponse findByRequestId(Long requestId, Long userId);

    void deleteById(Long requestId);
}
