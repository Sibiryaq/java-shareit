package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.exception.BookingException;
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
        Item item = itemStorage.findById(bookingDtoRequest.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Вещь с ID %s не существует", bookingDtoRequest.getItemId())));
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
                .orElseThrow(() -> new NotFoundException(String.format("Бронирования с ID %s не существует", bookingId)));
        if (!booking.getItem().getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new NotFoundException(
                    String.format("Бронирование с ID %s сейчас недоступно для пользователя с ID %s", bookingId, userId));
        }
        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDtoResponse> getOwnBookings(BookingState state, Long userId, Integer from, Integer size) {
        checkUserExist(userId);
        Sort sort = Sort.by("start").descending();
        LocalDateTime time = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size, sort);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingStorage.findByBookerId(userId, pageable).toList();
                break;
            case CURRENT:
                bookings = bookingStorage.findByBookerIdCurrent(userId, time, pageable).toList();
                break;
            case PAST:
                bookings = bookingStorage.findByBookerIdAndEndIsBefore(userId, time, pageable).toList();
                break;
            case FUTURE:
                bookings = bookingStorage.findByBookerIdAndStartIsAfter(userId, time, pageable).toList();
                break;
            case WAITING:
                bookings = bookingStorage.findByBookerIdAndStatus(userId, BookingStatus.WAITING, pageable).toList();
                break;
            case REJECTED:
                bookings = bookingStorage.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageable).toList();
        }
        log.info("Booking Service: Владелец бронирований найден. Количество: {}", bookings.size());
        return BookingMapper.toBookingDtoResponsesList(bookings);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDtoResponse> getOwnItemsBookings(BookingState state, Long userId, Integer from, Integer size) {
        checkUserExist(userId);
        Sort sort = Sort.by("start").descending();
        LocalDateTime time = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size, sort);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingStorage.findBookingByItemOwnerId(userId, pageable).toList();
                break;
            case CURRENT:
                bookings = bookingStorage.findBookingByItemOwnerCurrent(userId, time, pageable).toList();
                break;
            case PAST:
                bookings = bookingStorage.findBookingByItemOwnerIdAndEndIsBefore(userId, time, pageable).toList();
                break;
            case FUTURE:
                bookings = bookingStorage.findBookingByItemOwnerIdAndStartIsAfter(userId, time, pageable).toList();
                break;
            case WAITING:
                bookings = bookingStorage.findBookingByItemOwnerIdAndStatus(
                        userId, BookingStatus.WAITING, pageable).toList();
                break;
            case REJECTED:
                bookings = bookingStorage.findBookingByItemOwnerIdAndStatus(
                        userId, BookingStatus.REJECTED, pageable).toList();
        }
        log.info("Booking Service: Бронирования владельца найдены. Количество бронирований: {}", bookings.size());
        return BookingMapper.toBookingDtoResponsesList(bookings);
    }

    private void checkUserExist(Long userId) {
        if (!userStorage.existsUserById(userId)) {
            throw new NotFoundException(String.format("Пользователь с ID %s не существует", userId));
        }
    }
}
