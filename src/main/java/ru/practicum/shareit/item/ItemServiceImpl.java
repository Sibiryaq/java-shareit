package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.storage.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.ItemMapper.*;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public ItemDto addItem(ItemDto dto, long ownerId) throws NotFoundException {
        log.info("Добавлен предмет");
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id " + ownerId));
        Item item = toItem(dto, owner);
        item.setUser(owner);
        itemRepository.save(item);
        return toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto patchItem(ItemDto dto, long ownerId, long itemId) throws NotFoundException {
        long itemOwnerId = getItemOwnerId(itemId);
        if (itemOwnerId != ownerId) {
            throw new NotFoundException("Обновление невозможно");
        }
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();
            if (dto.getName() != null) {
                item.setName(dto.getName());
            }
            if (dto.getDescription() != null) {
                item.setDescription(dto.getDescription());
            }
            if (dto.getAvailable() != null) {
                item.setAvailable(dto.getAvailable());
            }
            log.info("Обновлен предмет с id " + itemId);
            return toItemDto(itemRepository.save(item));
        } else {
            throw new NotFoundException("Обновление невозможно");
        }
    }

    public long getItemOwnerId(long itemId) {
        return itemRepository.getReferenceById(itemId).getUser().getId();
    }

    @Override
    public ItemDto getItem(long itemId, long ownerId) throws NotFoundException {
        return itemRepository.findById(itemId)
                .map(item -> {
                    List<CommentDto> comments = commentRepository.findAllByItemId(itemId).stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
                    log.info("Получен предмет с id " + itemId);

                    List<BookingForItemDto> bookings = item.getUser().getId() == ownerId
                            ? bookingRepository.allBookingsForItem(itemId)
                            .stream().map(bookingMapper::toBookingForItemDto).collect(Collectors.toList())
                            : Collections.emptyList();

                    return bookings.isEmpty() && item.getUser().getId() == ownerId
                            ? toGetItemDto(item, null, comments)
                            : toGetItemDto(item, bookings, comments);
                })
                .orElseThrow(() -> new NotFoundException("Данный предмет не существует"));
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(long ownerId) {
        List<ItemDto> allItems =
                itemRepository.findAllByOwnerId(ownerId).stream()
                        .map(l -> ItemMapper.toGetItemDto(l, null, null))
                        .sorted(Comparator.comparing(ItemDto::getId))
                        .collect(Collectors.toList());

        List<Comment> allCommentsByItemsOwner = commentRepository.findAllByItemsOwnerId(ownerId);
        List<Booking> allBookingsByItemsOwner = bookingRepository.findAllByItemsOwnerId(ownerId);

        for (ItemDto item : allItems) {

            List<CommentDto> comments = allCommentsByItemsOwner
                    .stream()
                    .filter(l -> l.getItem().getId() == item.getId())
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList());
            item.setComments(comments);

            List<Booking> bookings = allBookingsByItemsOwner
                    .stream()
                    .filter(l -> l.getItem().getId() == item.getId())
                    .collect(Collectors.toList());

            if (bookings.size() != 0) {
                item.setLastBooking(bookingMapper.toBookingForItemDto(bookings.get(0)));
                item.setNextBooking(bookingMapper.toBookingForItemDto(bookings.get(bookings.size() - 1)));
            }
        }
        return allItems;
    }


    @Override
    public List<ItemDto> searchItem(String text, long ownerId) {
        if (text.isBlank()) {
            return List.of();
        } else {
            return itemRepository.search(text).stream()
                    .filter(Item::isAvailable)
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public Comment addComment(Comment dto, long itemId, long authorId) throws BadRequestException {
        LocalDateTime now = LocalDateTime.now();

        if (!bookingRepository.bookingsForItemAndBookerPast(authorId, itemId, now).isEmpty()) {
            User author = userRepository.findById(authorId).orElseThrow(() -> new BadRequestException("Пользователь не найден"));
            Item item = itemRepository.findById(itemId).orElseThrow(() -> new BadRequestException("Предмет не найден"));
            Comment comment = new Comment();
            comment.setAuthor(author);
            comment.setItem(item);
            comment.setText(dto.getText());
            comment.setCreated(now);
            return commentRepository.save(comment);
        } else {
            throw new BadRequestException("Ошибка запроса");
        }
    }
}
