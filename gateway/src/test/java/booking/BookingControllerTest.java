package booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.BookingStatusException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class BookingControllerTest {

    private BookingController bookingController;
    private BookingClient bookingClient;

    @BeforeEach
    void init() {
        bookingClient = mock(BookingClient.class);
        bookingController = new BookingController(bookingClient);
    }

    @Test
    void createBookingTest_ThrowEqualTime() {
        LocalDateTime now = LocalDateTime.now();
        BookingDtoRequest request = BookingDtoRequest.builder()
                .start(now)
                .end(now)
                .itemId(1L)
                .build();

        Throwable exception = assertThrows(BookingException.class,
                () -> bookingController.createBooking(request, 1L)
        );
        assertEquals("Начало и окончание бронирования одинаковы", exception.getMessage());
    }

    @Test
    void createBookingTest_ThrowTimeEndAhead() {
        LocalDateTime now = LocalDateTime.now();
        BookingDtoRequest request = BookingDtoRequest.builder()
                .start(now)
                .end(now.minusDays(5))
                .itemId(1L)
                .build();

        Throwable exception = assertThrows(BookingException.class,
                () -> bookingController.createBooking(request, 1L)
        );
        assertEquals("Окончание бронирования раньше начала бронирования", exception.getMessage());
    }

    @Test
    void createBookingTest_ThrowEmptyTime() {
        LocalDateTime now = LocalDateTime.now();
        BookingDtoRequest request = BookingDtoRequest.builder()
                .start(now)
                .end(null)
                .itemId(1L)
                .build();

        Throwable exception = assertThrows(BookingException.class,
                () -> bookingController.createBooking(request, 1L)
        );
        assertEquals("Начало и окончание бронирования не должны быть пустыми", exception.getMessage());
    }

    @Test
    void getOwnBookings_TrowByState() {
        Throwable exception = assertThrows(BookingStatusException.class,
                () -> bookingController.getOwnBookings("SSS", 1, 1, 1L)
        );
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }
}
