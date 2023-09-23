package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(BookingDto dto, long bookerId);

    BookingDto approveBooking(long bookingId, boolean approved, long bookerId);

    BookingDto getBooking(long bookingId, long bookerId);

    List<BookingDto> getAllBookingsByBookerId(long bookerId, BookingState state);

    List<BookingDto> getAllBookingByItemsByOwnerId(long ownerId, BookingState state);
}
