package ru.practicum.shareit.booking.dto;

import lombok.*;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookingDtoRequest {

    @FutureOrPresent
    private LocalDateTime start;

    @Future
    private LocalDateTime end;

    @NotNull
    private Long itemId;
}
