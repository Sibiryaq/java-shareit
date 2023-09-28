package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.enums.*;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.BookingStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingStorage bookingStorage;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Transactional
    @Override
    public BookingDtoResponse createBooking(BookingDtoRequest bookingDtoRequest, Long userId) {
        if (bookingDtoRequest.getStart() == null || bookingDtoRequest.getEnd() == null) {
            throw new BookingException("Время начала и окончания бронирования не должны быть пустыми");
        }
        if (bookingDtoRequest.getStart().isEqual(bookingDtoRequest.getEnd())) {
            throw new BookingException("Время начала и окончания бронирования одинаково!");
        }
        if (bookingDtoRequest.getEnd().isBefore(bookingDtoRequest.getStart())) {
            throw new BookingException("Время окончания раньше чем время начала бронирования");
        }
        Item item = itemStorage.findById(bookingDtoRequest.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с ID %s не существует",
                        bookingDtoRequest.getItemId())));
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с ID %s не существует", userId)));
        if (userId.equals(item.getOwner().getId())) {
            throw new NotFoundException("Вы не можете забронировать свою же вещь!");
        }
        if (!item.getAvailable()) {
            throw new BookingException("Вещь недоступна для бронирования");
        }
        Booking booking = BookingMapper.toBooking(bookingDtoRequest);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        Booking result = bookingStorage.save(booking);
        return BookingMapper.toBookingDtoResponse(result);
    }

    @Transactional
    @Override
    public BookingDtoResponse changeState(Long bookingId, Boolean approved, Long userId) {
        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирования с ID %s не существует", bookingId)));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException(String.format("Пользователь с ID %s не является владельцем", userId));
        }
        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new BookingException("Невозможно подтвердить подтвержденное бронирование");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDtoResponse getBooking(Long bookingId, Long userId) {
        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Бронирования с ID %s не существует", bookingId)));
        if (!booking.getItem().getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new NotFoundException(
                    String.format("Бронирование с ID %s сейчас недоступно для пользователя с ID %s", bookingId, userId));
        }
        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    public List<BookingDtoResponse> getOwnBookings(String state, Long userId) {
        BookingState parseState = checkState(state);
        checkUser(userId);
        Sort sort = Sort.by("start").descending(); //вынес отдельно из конструкции switch
        LocalDateTime time = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();
        switch (parseState) {
            case ALL:
                bookings = bookingStorage.findByBookerId(userId, sort);
                break;
            case CURRENT:
                bookings = bookingStorage.findByBookerIdCurrent(userId, time);
                break;
            case PAST:
                bookings = bookingStorage.findByBookerIdAndEndIsBefore(userId, time, sort);
                break;
            case FUTURE:
                bookings = bookingStorage.findByBookerIdAndStartIsAfter(userId, time, sort);
                break;
            case WAITING:
                bookings = bookingStorage.findByBookerIdAndStatus(userId, BookingStatus.WAITING, sort);
                break;
            case REJECTED:
                bookings = bookingStorage.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, sort);
        }
        return BookingMapper.toBookingDtoResponsesList(bookings);
    }

    @Override
    public List<BookingDtoResponse> getOwnItemsBookings(String state, Long userId) {
        BookingState parseState = checkState(state);
        checkUser(userId);
        Sort sort = Sort.by("start").descending();
        LocalDateTime time = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();
        switch (parseState) {
            case ALL:
                bookings = bookingStorage.findBookingByItemOwnerId(userId, sort);
                break;
            case CURRENT:
                bookings = bookingStorage.findBookingByItemOwnerCurrent(userId, time);
                break;
            case PAST:
                bookings = bookingStorage.findBookingByItemOwnerIdAndEndIsBefore(userId, time, sort);
                break;
            case FUTURE:
                bookings = bookingStorage.findBookingByItemOwnerIdAndStartIsAfter(userId, time, sort);
                break;
            case WAITING:
                bookings = bookingStorage.findBookingByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, sort);
                break;
            case REJECTED:
                bookings = bookingStorage.findBookingByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, sort);
        }
        return BookingMapper.toBookingDtoResponsesList(bookings);
    }

    private BookingState checkState(String state) {
        try {
            return BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BookingStatusException("Unknown state: UNSUPPORTED_STATUS");
            /*
            Если вместо Unknown state написать: "неизвестное состояние",
            то два теста из постмана проваливаются, они именно требуют чтобы  было
            equals Unknown state
             */
        }
    }

    private void checkUser(Long userId) {
        if (!userStorage.existsUserById(userId)) {
            throw new NotFoundException(String.format("Пользователя с ID %s не существует", userId));
        }
    }
}