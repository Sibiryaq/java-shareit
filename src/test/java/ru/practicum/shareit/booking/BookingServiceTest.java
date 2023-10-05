package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.BookingStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    private BookingStorage bookingStorage;
    private ItemStorage itemStorage;
    private UserStorage userStorage;
    private BookingService bookingService;

    @BeforeEach
    void init() {
        bookingStorage = mock(BookingStorage.class);
        itemStorage = mock(ItemStorage.class);
        userStorage = mock(UserStorage.class);
        bookingService = new BookingServiceImpl(bookingStorage, itemStorage, userStorage);
    }

    @Test
    void createBookingTest() {
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(createItem()));
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(createUser()));
        when(bookingStorage.save(any(Booking.class)))
                .thenReturn(createBooking());

        BookingDtoResponse result = bookingService.createBooking(createRequest(), 2L);
        assertNotNull(result);
    }

    @Test
    void createBookingTest_ThrowNotNow() {
        Item item = createItem();
        item.setAvailable(false);
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(createUser()));

        Throwable exception = assertThrows(
                BookingException.class,
                () -> {
                    LocalDateTime now = LocalDateTime.now();
                    BookingDtoRequest request = new BookingDtoRequest(now, now.plusHours(1), 1L);
                    bookingService.createBooking(request, 2L);
                }
        );
        assertEquals("Вещь недоступна для бронирования", exception.getMessage());
    }

    @Test
    void createBookingTest_ThrowNoItem() {
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.empty());

        Throwable exception = assertThrows(
                NotFoundException.class,
                () -> {
                    LocalDateTime now = LocalDateTime.now();
                    BookingDtoRequest request = new BookingDtoRequest(now, now.plusHours(1), 1L);
                    bookingService.createBooking(request, 1L);
                }
        );
        assertEquals("Вещь с ID 1 не существует", exception.getMessage());
    }

    @Test
    void createBookingTest_ThrowEqualTime() {
        Throwable exception = assertThrows(
                BookingException.class,
                () -> {
                    LocalDateTime now = LocalDateTime.now();
                    BookingDtoRequest request = new BookingDtoRequest(now, now, 1L);
                    bookingService.createBooking(request, 2L);
                }
        );
        assertEquals("Время начала и окончания бронирования одинаково!", exception.getMessage());
    }

    @Test
    void createBookingTest_ThrowOwnItem() {
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(createItem()));
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(createUser()));

        Throwable exception = assertThrows(
                NotFoundException.class,
                () -> {
                    LocalDateTime now = LocalDateTime.now();
                    BookingDtoRequest request = new BookingDtoRequest(now, now.plusHours(5), 1L);
                    bookingService.createBooking(request, 1L);
                }
        );
        assertEquals("Вы не можете забронировать свою же вещь!", exception.getMessage());
    }

    @Test
    void createBookingTest_ThrowTimeEndAhead() {
        Throwable exception = assertThrows(
                BookingException.class,
                () -> {
                    LocalDateTime now = LocalDateTime.now();
                    BookingDtoRequest request = new BookingDtoRequest(now, now.minusDays(5), 1L);
                    bookingService.createBooking(request, 2L);
                }
        );
        assertEquals("Время окончания раньше чем время начала бронирования", exception.getMessage());
    }

    @Test
    void createBookingTest_ThrowEmptyTime() {
        Throwable exception = assertThrows(
                BookingException.class,
                () -> {
                    LocalDateTime now = LocalDateTime.now();
                    BookingDtoRequest request = new BookingDtoRequest(now, null, 1L);
                    bookingService.createBooking(request, 2L);
                }
        );
        assertEquals("Время начала и окончания бронирования не должны быть пустыми", exception.getMessage());
    }

    @Test
    void changeStateTest() {
        Booking booking = createBooking();
        booking.setStatus(BookingStatus.REJECTED);
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDtoResponse result = bookingService.changeState(1L, true, 1L);
        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void changeState2Test() {
        Booking booking = createBooking();
        booking.setStatus(BookingStatus.REJECTED);
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDtoResponse result = bookingService.changeState(1L, false, 1L);
        assertNotNull(result);
        assertEquals(BookingStatus.REJECTED, result.getStatus());
    }

    @Test
    void changeStateTest_ThrowOwner() {
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.of(createBooking()));

        Throwable exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.changeState(1L, true, 2L)
        );
        assertEquals("Пользователь с ID 2 не является владельцем", exception.getMessage());
    }

    @Test
    void changeStateTest_throw_approved() {
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.of(createBooking()));

        Throwable exception = assertThrows(
                BookingException.class,
                () -> bookingService.changeState(1L, true, 1L)
        );
        assertEquals("Невозможно подтвердить подтвержденное бронирование", exception.getMessage());
    }

    @Test
    void getBookingTest() {
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.of(createBooking()));

        BookingDtoResponse result = bookingService.getBooking(1L, 1L);
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void getBookingTest_NotAvailable() {
        when(bookingStorage.findById(anyLong()))
                .thenReturn(Optional.of(createBooking()));

        Throwable exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.getBooking(1L, 2L)
        );
        assertEquals("Бронирование с ID 1 сейчас недоступно для пользователя с ID 2", exception.getMessage());
    }

    @Test
    void getOwnBookingsTest() {
        when(userStorage.existsUserById(anyLong()))
                .thenReturn(true);
        when(bookingStorage.findByBookerId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(createBooking())));
        when(bookingStorage.findByBookerIdAndStatus(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));
        when(bookingStorage.findByBookerIdCurrent(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(createBooking())));
        when(bookingStorage.findByBookerIdAndEndIsBefore(
                anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));
        when(bookingStorage.findByBookerIdAndStartIsAfter(
                anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        List<BookingDtoResponse> result = bookingService.getOwnBookings("ALL", 1L, 1, 1);
        assertNotNull(result);
        assertEquals(1, result.size());

        List<BookingDtoResponse> result2 = bookingService.getOwnBookings("REJECTED", 1L, 1, 1);
        assertNotNull(result2);
        assertTrue(result2.isEmpty());

        List<BookingDtoResponse> result3 = bookingService.getOwnBookings("WAITING", 1L, 1, 1);
        assertNotNull(result3);
        assertTrue(result3.isEmpty());

        List<BookingDtoResponse> result4 = bookingService.getOwnBookings("CURRENT", 1L, 1, 1);
        assertNotNull(result4);
        assertEquals(1, result4.size());

        List<BookingDtoResponse> result5 = bookingService.getOwnBookings("PAST", 1L, 1, 1);
        assertNotNull(result5);
        assertTrue(result5.isEmpty());

        List<BookingDtoResponse> result6 = bookingService.getOwnBookings("FUTURE", 1L, 1, 1);
        assertNotNull(result6);
        assertTrue(result6.isEmpty());
    }

    @Test
    void getOwnBookingsTest_ThrowStatus() {
        Throwable exception = assertThrows(
                BookingStatusException.class,
                () -> bookingService.getOwnBookings("SSS", 1L, 1, 1)
        );
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    void getOwnBookingsTest_ThrowUser() {
        when(userStorage.existsUserById(anyLong()))
                .thenReturn(false);
        Throwable exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.getOwnBookings("ALL", 1L, 1, 1)
        );
        assertEquals("Пользователь с ID 1 не существует", exception.getMessage());
    }

    @Test
    void getOwnItemsBookingsTest() {
        when(userStorage.existsUserById(anyLong()))
                .thenReturn(true);
        when(bookingStorage.findBookingByItemOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(createBooking())));
        when(bookingStorage.findBookingByItemOwnerIdAndStatus(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));
        when(bookingStorage.findBookingByItemOwnerCurrent(anyLong(),any(LocalDateTime.class),any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(createBooking())));
        when(bookingStorage.findBookingByItemOwnerIdAndEndIsBefore(anyLong(),any(LocalDateTime.class),any(Pageable.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));
        when(bookingStorage.findBookingByItemOwnerIdAndStartIsAfter(anyLong(),any(LocalDateTime.class),any(Pageable.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        List<BookingDtoResponse> result = bookingService.getOwnItemsBookings("ALL", 1L, 1, 1);
        assertNotNull(result);
        assertEquals(1, result.size());

        List<BookingDtoResponse> result2 = bookingService.getOwnItemsBookings("REJECTED", 1L, 1, 1);
        assertNotNull(result2);
        assertTrue(result2.isEmpty());

        List<BookingDtoResponse> result3 = bookingService.getOwnItemsBookings("WAITING", 1L, 1, 1);
        assertNotNull(result3);
        assertTrue(result3.isEmpty());

        List<BookingDtoResponse> result4 = bookingService.getOwnItemsBookings("CURRENT", 1L, 1, 1);
        assertNotNull(result4);
        assertEquals(1, result.size());

        List<BookingDtoResponse> result5 = bookingService.getOwnItemsBookings("PAST", 1L, 1, 1);
        assertNotNull(result5);
        assertTrue(result5.isEmpty());

        List<BookingDtoResponse> result6 = bookingService.getOwnItemsBookings("FUTURE", 1L, 1, 1);
        assertNotNull(result6);
        assertTrue(result6.isEmpty());
    }

    private BookingDtoRequest createRequest() {
        return new BookingDtoRequest(LocalDateTime.now(), LocalDateTime.now().plusHours(1), 1L);
    }

    private Booking createBooking() {
        return new Booking(1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                createItem(),
                createUser(),
                BookingStatus.APPROVED);
    }

    private User createUser() {
        return new User(1L, "name", "test@mail.ru");
    }

    private ItemRequest createItemRequest() {
        return new ItemRequest(1L, "descr", createUser(), LocalDateTime.now());
    }

    private Item createItem() {
        return new Item(1L, "name", "descr", true, createUser(), createItemRequest());
    }
}
