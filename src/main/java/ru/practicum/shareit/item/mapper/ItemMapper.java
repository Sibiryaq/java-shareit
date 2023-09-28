package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDtoResponse toItemDto(Item item) {
        return new ItemDtoResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                null
        );
    }

    public static ItemDtoResponse toItemDto(Item item, List<Comment> comments) {
        return new ItemDtoResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                CommentMapper.toCommentDtoList(comments)
        );
    }

    public static ItemDtoResponse toItemDto(Item item,
                                            List<Comment> comments,
                                            Booking lastBooking,
                                            Booking nextBooking) {
        return new ItemDtoResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                BookingMapper.toBookingItemDtoResponse(lastBooking),
                BookingMapper.toBookingItemDtoResponse(nextBooking),
                CommentMapper.toCommentDtoList(comments)
        );
    }

    public static List<ItemDtoResponse> toItemDtoList(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public static Item toItem(ItemDtoRequest itemDtoRequest) {
        Item item = new Item();
        item.setId(itemDtoRequest.getId());
        item.setName(itemDtoRequest.getName());
        item.setDescription(itemDtoRequest.getDescription());
        item.setAvailable(itemDtoRequest.getAvailable());
        return item;
    }
}

