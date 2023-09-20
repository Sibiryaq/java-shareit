package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(BookingDto dto, long bookerId) throws BadRequestException, NotFoundException;

    BookingDto approveBooking(long bookingId, boolean approved, long bookerId)
            throws BadRequestException, NotFoundException;

    BookingDto getBooking(long bookingId, long bookerId) throws NotFoundException;

    List<BookingDto> getAllBookingsByBookerId(long bookerId, BookingState state) throws NotFoundException;

    List<BookingDto> getAllBookingByItemsByOwnerId(long ownerId, BookingState state) throws NotFoundException;
}
