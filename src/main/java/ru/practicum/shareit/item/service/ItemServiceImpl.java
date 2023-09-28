package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final CommentStorage commentStorage;
    private final BookingStorage bookingStorage;

    @Transactional
    @Override
    public ItemDtoResponse createItem(ItemDtoRequest itemDtoRequest, Long userId) {
        User owner = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(ERR_USER, userId)));
        Item item = ItemMapper.toItem(itemDtoRequest);
        item.setOwner(owner);
        Item result = itemStorage.save(item);
        return ItemMapper.toItemDto(result);
    }

    @Transactional
    @Override
    public ItemDtoResponse updateOwnItem(ItemDtoRequest itemDtoRequest, Long userId) {
        checkUser(userId);
        Item item = itemStorage.findById(itemDtoRequest.getId())
                .orElseThrow(() -> new NotFoundException(String.format(ERR_ITEM, itemDtoRequest.getId())));
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException(String.format(ERR_USER, userId));
        }
        Optional.ofNullable(itemDtoRequest.getName()).ifPresent(item::setName);
        Optional.ofNullable(itemDtoRequest.getDescription()).ifPresent(item::setDescription);
        Optional.ofNullable(itemDtoRequest.getAvailable()).ifPresent(item::setAvailable);
        return ItemMapper.toItemDto(item);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDtoResponse getItem(long itemId, Long userId) {
        checkUser(userId);
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format(ERR_ITEM, itemId)));
        if (item.getOwner().getId().equals(userId)) {
            return setItemDtoWithBooking(item, userId);
        } else {
            List<Comment> comments = commentStorage.findAllByItemId(item.getId());
            return ItemMapper.toItemDto(item, comments);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDtoResponse> getOwnItems(Long userId) {
        checkUser(userId);
        List<ItemDtoResponse> result = new ArrayList<>();
        List<Item> items = itemStorage.findAllByOwnerId(userId);
        for (Item item : items) {
            result.add(setItemDtoWithBooking(item, userId));
        }
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDtoResponse> searchItems(String text, Long userId) {
        checkUser(userId);
        if (StringUtils.isBlank(text)) {
            return new ArrayList<>();
        }
        List<Item> items = itemStorage.itemsSearch(text);
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
            throw new CommentException("Комментарии только для тех вещей, что у Вас есть");
        }
        Comment comment = CommentMapper.toComment(commentDtoRequest, user, item, LocalDateTime.now());
        Comment result = commentStorage.save(comment);
        return CommentMapper.toCommentDto(result);
    }

    private void checkUser(Long userId) {
        if (userId == null) {
            log.warn("Пустой пользователь");
            throw new NotFoundException("Пустой пользователь");
        } else if (!userStorage.existsUserById(userId)) {
            log.warn("Пользователь не найден: {}", userId);
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
