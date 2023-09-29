package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
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
                "ID Пользователя {}, booking ID {}", userId, bookingId);
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
                                                   @RequestHeader(USER_HEADER) Long userId) {
        log.info("Booking Controller: Получение владельца бронирования. " +
                "ID пользователя {}, состояние - {}", userId, state);
        return bookingService.getOwnBookings(state, userId);
    }

    @GetMapping(value = "/owner")
    public List<BookingDtoResponse> getOwnItemsBookings(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                                        @RequestHeader(USER_HEADER) Long userId) {
        log.info("Booking Controller: Получение бронирований для предметов владельца. " +
                "Пользователь с ID {}, состояние - {}", userId, state);
        return bookingService.getOwnItemsBookings(state, userId);
    }
}
