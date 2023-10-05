package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingStorageTest {

    @Autowired
    private BookingStorage bookingStorage;

    @Autowired
    private ItemStorage itemStorage;

    @Autowired
    private UserStorage userStorage;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void init() {
        owner = User.builder()
                .name("owner")
                .email("test1@mail.ru")
                .build();

        booker = User.builder()
                .name("booker")
                .email("test2@mail.ru")
                .build();

        item = Item.builder()
                .name("Item Name")
                .description("descr")
                .available(true)
                .owner(owner)
                .build();

        owner = userStorage.save(owner);
        booker = userStorage.save(booker);
        item = itemStorage.save(item);

        booking = Booking.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        booking = bookingStorage.save(booking);
    }

    @Test
    void findByBookerIdCurrentTest() {
        Page<Booking> result = bookingStorage.findByBookerIdCurrent(
                booker.getId(), LocalDateTime.now(), Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getSize());
        assertEquals(booking, result.getContent().get(0));
    }

    @Test
    void findBookingByItemOwnerCurrentTest() {
        Page<Booking> result = bookingStorage.findBookingByItemOwnerCurrent(
                owner.getId(), LocalDateTime.now(), Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getSize());
        assertEquals(booking, result.getContent().get(0));
    }

    @Test
    void getLastBookingTest() {
        List<Booking> result = bookingStorage.getLastBooking(
                LocalDateTime.now(), owner.getId(), item.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking, result.get(0));
    }

    @Test
    void getNextBookingTest() {
        List<Booking> result = bookingStorage.getNextBooking(
                LocalDateTime.now(), owner.getId(), item.getId());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}
