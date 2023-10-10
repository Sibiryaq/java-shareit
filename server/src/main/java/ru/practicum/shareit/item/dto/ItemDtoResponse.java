package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingItemDtoResponse;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ItemDtoResponse {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingItemDtoResponse lastBooking;

    private BookingItemDtoResponse nextBooking;

    private List<CommentDtoResponse> comments;

    private Long requestId;

}
