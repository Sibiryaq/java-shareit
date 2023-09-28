package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDtoResponse;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    public static Booking toBooking(BookingDtoRequest bookingDtoRequest) {
        Booking booking = new Booking();
        booking.setStart(bookingDtoRequest.getStart());
        booking.setEnd(bookingDtoRequest.getEnd());
        return booking;
    }

    public static List<BookingDtoResponse> toBookingDtoResponsesList(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

    public static BookingDtoResponse toBookingDtoResponse(Booking booking) {
        UserDtoResponse userDtoResponse = UserMapper.toUserDto(booking.getBooker());
        ItemDtoResponse itemDtoResponse = ItemMapper.toItemDto(booking.getItem());
        BookingDtoResponse bookingDtoResponse = new BookingDtoResponse();
        bookingDtoResponse.setId(booking.getId());
        bookingDtoResponse.setStart(booking.getStart());
        bookingDtoResponse.setEnd(booking.getEnd());
        bookingDtoResponse.setItem(itemDtoResponse);
        bookingDtoResponse.setBooker(userDtoResponse);
        bookingDtoResponse.setStatus(booking.getStatus());
        return bookingDtoResponse;
    }

    public static BookingItemDtoResponse toBookingItemDtoResponse(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingItemDtoResponse(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd()
        );
    }
}
