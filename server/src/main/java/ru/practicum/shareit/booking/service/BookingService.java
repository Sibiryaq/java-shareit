package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.enums.BookingState;

import java.util.List;

public interface BookingService {

    BookingDtoResponse createBooking(BookingDtoRequest bookingDtoRequest, Long userId);

    BookingDtoResponse changeState(Long bookingId, Boolean approved, Long userId);

    BookingDtoResponse getBooking(Long bookingId, Long userId);

    List<BookingDtoResponse> getOwnBookings(BookingState state, Long userId, Integer from, Integer size);

    List<BookingDtoResponse> getOwnItemsBookings(BookingState state, Long userId, Integer from, Integer size);
}
