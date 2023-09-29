package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.user.dto.UserDtoResponse;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookingDtoResponse {

    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private ItemDtoResponse item;

    private UserDtoResponse booker;

    private BookingStatus status;
}
