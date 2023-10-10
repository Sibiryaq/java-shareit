package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookingItemDtoResponse {

    private Long id;

    private Long bookerId;

    private LocalDateTime start;

    private LocalDateTime end;

}
