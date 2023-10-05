package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.exception.CommentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.*;
import ru.practicum.shareit.item.storage.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private static final String ERR_ITEM = "Вещь с ID %s не найдена";
    private static final String ERR_USER = "Пользователь с ID %s не найден";
    private static final String ERR_REQ = "Запрос с ID %s не найден";
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final CommentStorage commentStorage;
    private final BookingStorage bookingStorage;
    private final ItemRequestStorage itemRequestStorage;

    @Transactional
    @Override
    public ItemDtoResponse createItem(ItemDtoRequest itemDtoRequest, Long userId) {
        User owner = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(ERR_USER, userId)));

        Item item = ItemMapper.toItem(itemDtoRequest);
        item.setOwner(owner);

        if (itemDtoRequest.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestStorage
                    .findById(itemDtoRequest.getRequestId())
                    .orElseThrow(() -> new NotFoundException(String.format(ERR_REQ, itemDtoRequest.getRequestId())));
            item.setRequest(itemRequest);
        }

        Item result = itemStorage.save(item);
        log.info("Item Service: Вещь создана. ID вещи {}", result.getId());
        return ItemMapper.toItemDto(result);
    }

    @Transactional
    @Override
    public ItemDtoResponse updateOwnItem(ItemDtoRequest itemDtoRequest, Long userId) {
        checkUserExist(userId);
        Item item = itemStorage.findById(itemDtoRequest.getId())
                .orElseThrow(() -> new NotFoundException(String.format(ERR_ITEM, itemDtoRequest.getId())));
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException(String.format("Пользователь с ID %s не является владельцем", userId));
        }
        Optional.ofNullable(itemDtoRequest.getName()).ifPresent(item::setName);
        Optional.ofNullable(itemDtoRequest.getDescription()).ifPresent(item::setDescription);
        Optional.ofNullable(itemDtoRequest.getAvailable()).ifPresent(item::setAvailable);
        log.info("Item Service: Запись вещи обновлена. ID вещи {}", item.getId());
        return ItemMapper.toItemDto(item);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDtoResponse getItem(long itemId, Long userId) {
        checkUserExist(userId);
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format(ERR_ITEM, itemId)));
        log.info("Item Service: Вещь найдена. ID вещи {}", itemId);
        if (item.getOwner().getId().equals(userId)) {
            return setItemDtoWithBooking(item, userId);
        } else {
            List<Comment> comments = commentStorage.findAllByItemId(item.getId());
            return ItemMapper.toItemDto(item, comments);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDtoResponse> getOwnItems(Long userId, Integer from, Integer size) {
        checkUserExist(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemDtoResponse> result = new ArrayList<>();
        List<Item> items = itemStorage.findAllByOwnerId(userId, pageable).toList();
        for (Item item : items) {
            result.add(setItemDtoWithBooking(item, userId));
        }
        log.info("Item Service: Вещи найдены. Количество: {}", result.size());
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDtoResponse> searchItems(String text, Long userId, Integer from, Integer size) {
        checkUserExist(userId);
        if (StringUtils.isBlank(text)) {
            return new ArrayList<>();
        }
        Pageable pageable = PageRequest.of(from / size, size);
        List<Item> items = itemStorage.itemsSearch(text, pageable).toList();
        log.info("Item Service: Вещи найдены. Количество: {}", items.size());
        return ItemMapper.toItemDtoList(items);
    }

    @Transactional
    @Override
    public CommentDtoResponse createComment(CommentDtoRequest commentDtoRequest, Long itemId, Long userId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(ERR_USER, userId)));
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format(ERR_ITEM, itemId)));
        if (bookingStorage.findBookingForComments(userId, itemId, LocalDateTime.now()).isEmpty()) {
            throw new CommentException("Комментировать можно только то, чем пользуетесь");
        }
        Comment comment = CommentMapper.toComment(commentDtoRequest, user, item, LocalDateTime.now());
        Comment result = commentStorage.save(comment);
        log.info("Item Service: Комментарий создан. ID: {}", result.getId());
        return CommentMapper.toCommentDto(result);
    }

    private void checkUserExist(Long userId) {
        if (userId == null) {
            log.error("Пользователь пуст");
            throw new NotFoundException("Empty user");
        } else if (!userStorage.existsUserById(userId)) {
            log.error("Пользователь не найден: {}", userId);
            throw new NotFoundException("Пользователь не найден: " + userId);
        }
    }

    private ItemDtoResponse setItemDtoWithBooking(Item item, Long userId) {
        List<Comment> comments = commentStorage.findAllByItemId(item.getId());
        Booking lastBooking = bookingStorage
                .getLastBooking(LocalDateTime.now(), userId, item.getId())
                .stream()
                .findFirst()
                .orElse(null);
        Booking nextBooking = bookingStorage
                .getNextBooking(LocalDateTime.now(), userId, item.getId())
                .stream()
                .findFirst()
                .orElse(null);

        return ItemMapper.toItemDto(item, comments, lastBooking, nextBooking);
    }
}
