package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestStorage;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoWItemResponse;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private static final String ERR_USER = "Пользователь с ID %s не существует";
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemRequestStorage itemRequestStorage;

    @Transactional
    @Override
    public ItemRequestDtoResponse createRequest(ItemRequestDtoRequest itemRequestDtoRequest, Long userId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(ERR_USER, userId)));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDtoRequest, user);
        ItemRequest result = itemRequestStorage.save(itemRequest);
        log.info("Request Service: Запрос создан. ID {}", result.getId());
        return ItemRequestMapper.toItemRequestDtoResponse(result);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDtoWItemResponse> findByUserId(Long userId) {
        checkUserExist(userId);
        List<ItemRequest> itemRequests = itemRequestStorage.findAllByRequestorIdOrderByCreatedDesc(userId);
        log.info("Request Service: Запрос найден. Количество: {}", itemRequests.size());
        List<ItemRequestDtoWItemResponse> result = ItemRequestMapper.toItemRequestDtoWItemResponse(itemRequests);
        for (ItemRequestDtoWItemResponse req : result) {
            req.setItems(ItemMapper.toItemDtoList(itemStorage.findAllByRequestId(req.getId())));
        }
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDtoWItemResponse> findFromAll(Integer from, Integer size, Long userId) {
        checkUserExist(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
        Page<ItemRequest> pageItemRequests = itemRequestStorage.findAllOtherRequests(userId, pageable);
        log.info("Request Service: Запрос найден. Количество: {}", pageItemRequests.getSize());
        List<ItemRequestDtoWItemResponse> result = ItemRequestMapper.toItemRequestDtoWItemResponse(pageItemRequests);
        for (ItemRequestDtoWItemResponse req : result) {
            req.setItems(ItemMapper.toItemDtoList(itemStorage.findAllByRequestId(req.getId())));
        }
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDtoWItemResponse findByRequestId(Long requestId, Long userId) {
        checkUserExist(userId);
        ItemRequest itemRequest = itemRequestStorage.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с ID %s не существует", requestId)));
        log.info("Request Service: Запрос найден. ID: {}", requestId);
        ItemRequestDtoWItemResponse result = ItemRequestMapper.toItemRequestDtoWItRespSingle(itemRequest);
        result.setItems(ItemMapper.toItemDtoList(itemStorage.findAllByRequestId(result.getId())));
        return result;
    }

    @Transactional
    @Override
    public void deleteById(Long requestId) {
        itemRequestStorage.deleteById(requestId);
    }

    private void checkUserExist(Long userId) {
        if (userId == null) {
            log.error("Пользователь пустой");
            throw new NotFoundException("Пользователь пустой");
        } else if (!userStorage.existsUserById(userId)) {
            log.error("Пользователя нен существует: {}", userId);
            throw new NotFoundException("Пользователя не существует: " + userId);
        }
    }
}
