package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.*;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceIntTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Test
    void createBookingTest() {
        UserDtoResponse user = createUser();
        ItemDtoResponse item = createItem(user);

        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .itemId(item.getId())
                .build();

        UserDtoResponse user2 = createUser2();
        BookingDtoResponse result = bookingService.createBooking(bookingDtoRequest, user2.getId());
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(item.getId(), result.getItem().getId());
        assertEquals(user2.getId(), result.getBooker().getId());
    }

    @Test
    void getOwnItemsBookingTest() {
        UserDtoResponse user = createUser();
        ItemDtoResponse item1 = createItem(user);
        ItemDtoResponse item2 = createItem(user);
        ItemDtoResponse item3 = createItem(user);
        UserDtoResponse user2 = createUser2();

        BookingDtoRequest bookingDtoRequest1 = BookingDtoRequest.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .itemId(item1.getId())
                .build();
        bookingService.createBooking(bookingDtoRequest1, user2.getId());

        BookingDtoRequest bookingDtoRequest2 = BookingDtoRequest.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .itemId(item2.getId())
                .build();
        bookingService.createBooking(bookingDtoRequest2, user2.getId());

        BookingDtoRequest bookingDtoRequest3 = BookingDtoRequest.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .itemId(item3.getId())
                .build();
        bookingService.createBooking(bookingDtoRequest3, user2.getId());

        List<BookingDtoResponse> result = bookingService.getOwnItemsBookings("ALL", user.getId(), 1, 5);
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    private UserDtoResponse createUser() {
        UserDtoRequest req = UserDtoRequest.builder()
                .name("name")
                .email("email@mail.ru")
                .build();
        return userService.createUser(req);
    }

    private UserDtoResponse createUser2() {
        UserDtoRequest req = UserDtoRequest.builder()
                .name("name2")
                .email("email2@mail.ru")
                .build();
        return userService.createUser(req);
    }

    private ItemDtoResponse createItem(UserDtoResponse user) {
        ItemDtoRequest req = ItemDtoRequest.builder()
                .name("name")
                .description("desc")
                .available(true)
                .build();
        return itemService.createItem(req, user.getId());
    }


}
