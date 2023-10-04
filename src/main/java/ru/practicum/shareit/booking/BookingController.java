package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Validated
@Slf4j
public class BookingController {
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse createBooking(@Valid @RequestBody BookingDtoRequest bookingDtoRequest,
                                            @RequestHeader(USER_HEADER) Long userId) {
        log.info("Booking Controller: Создание бронирования. ID Пользователя {}", userId);
        return bookingService.createBooking(bookingDtoRequest, userId);
    }

    @PatchMapping(value = "/{bookingId}")
    public BookingDtoResponse changeState(@PathVariable Long bookingId,
                                          @RequestParam Boolean approved,
                                          @RequestHeader(USER_HEADER) Long userId) {
        log.info("Booking Controller: изменение состояние бронирования. " +
                "ID Пользователя {}, ID бронирования {}", userId, bookingId);
        return bookingService.changeState(bookingId, approved, userId);
    }

    @GetMapping(value = "/{bookingId}")
    public BookingDtoResponse getBooking(@PathVariable Long bookingId,
                                         @RequestHeader(USER_HEADER) Long userId) {
        log.info("Booking Controller: Получение бронирования. ID Пользователя {}," +
                " ID бронирования {}", userId, bookingId);
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoResponse> getOwnBookings(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                                   @RequestParam(defaultValue = "0")
                                                   @Min(value = 0, message = "Минимум с: 0") int from,
                                                   @RequestParam(defaultValue = "10")
                                                   @Min(value = 1, message = "Минимальный размер: 1") int size,
                                                   @RequestHeader(USER_HEADER) Long userId) {
        log.info("Booking Controller: Получены бронирования владельца. Пользователь с ID {}, Состояние {}", userId, state);
        return bookingService.getOwnBookings(state, userId, from, size);
    }

    @GetMapping(value = "/owner")
    public List<BookingDtoResponse> getOwnItemsBookings(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                                        @RequestParam(defaultValue = "0")
                                                        @Min(value = 0, message = "Minimum from: 0") int from,
                                                        @RequestParam(defaultValue = "10")
                                                        @Min(value = 1, message = "Minimum size: 1") int size,
                                                        @RequestHeader(USER_HEADER) Long userId) {
        log.info("Booking Controller: Получен владелец бронирований. ID пользователя {}, Состояние {}", userId, state);
        return bookingService.getOwnItemsBookings(state, userId, from, size);
    }
}
