package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.exception.CommentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class ItemServiceTest {

    private ItemService itemService;
    private ItemStorage itemStorage;
    private UserStorage userStorage;
    private CommentStorage commentStorage;
    private BookingStorage bookingStorage;
    private ItemRequestStorage itemRequestStorage;

    @BeforeEach
    void init() {
        itemStorage = mock(ItemStorage.class);
        userStorage = mock(UserStorage.class);
        commentStorage = mock(CommentStorage.class);
        bookingStorage = mock(BookingStorage.class);
        itemRequestStorage = mock(ItemRequestStorage.class);
        itemService = new ItemServiceImpl(
                itemStorage, userStorage, commentStorage, bookingStorage, itemRequestStorage);
    }

    @Test
    void createItemTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(createUser()));
        when(itemRequestStorage.findById(anyLong()))
                .thenReturn(Optional.of(createItemRequest()));
        when(itemStorage.save(any(Item.class)))
                .thenReturn(createItem());

        ItemDtoResponse result = itemService.createItem(createDtoRequest(), 1L);
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void updateOwnItemTest() {
        when(userStorage.existsUserById(anyLong()))
                .thenReturn(true);
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(createItem()));

        ItemDtoResponse result = itemService.updateOwnItem(updateRequest(), 1L);
        assertNotNull(result);
        assertEquals("updated", result.getName());
    }

    @Test
    void updateOwnItemTest_throw() {
        when(userStorage.existsUserById(anyLong()))
                .thenReturn(true);
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(createItem()));

        Throwable exception = assertThrows(
                NotFoundException.class,
                () -> itemService.updateOwnItem(updateRequest(), 2L)
        );
        assertEquals("Пользователь с ID 2 не является владельцем", exception.getMessage());
    }

    @Test
    void getItemTest() {
        when(userStorage.existsUserById(anyLong()))
                .thenReturn(true);
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(createItem()));
        when(commentStorage.findAllByItemId(anyLong()))
                .thenReturn(List.of(createComment()));
        when(bookingStorage.getLastBooking(any(LocalDateTime.class), anyLong(), anyLong()))
                .thenReturn(List.of(createBooking()));
        when(bookingStorage.getNextBooking(any(LocalDateTime.class), anyLong(), anyLong()))
                .thenReturn(List.of(createBooking()));

        ItemDtoResponse result = itemService.getItem(1L, 1L);
        assertNotNull(result);
        assertNotNull(result.getLastBooking());
        assertNotNull(result.getNextBooking());
        assertEquals(1L, result.getId());
    }

    @Test
    void getItemTest_throw() {
        when(userStorage.existsUserById(anyLong()))
                .thenReturn(true);
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.empty());

        Throwable exception = assertThrows(
                NotFoundException.class,
                () -> itemService.getItem(1L, 1L)
        );
        assertEquals("Вещь с ID 1 не найдена", exception.getMessage());
    }

    @Test
    void getItemTest_throwEmptyUser() {
        Throwable exception = assertThrows(
                NotFoundException.class,
                () -> itemService.getItem(1L, null)
        );
        assertEquals("Empty user", exception.getMessage());
    }

    @Test
    void getItemTest_throwNoUser() {
        when(userStorage.existsUserById(anyLong()))
                .thenReturn(false);

        Throwable exception = assertThrows(
                NotFoundException.class,
                () -> itemService.getItem(1L, 1L)
        );
        assertEquals("Пользователь не найден: 1", exception.getMessage());
    }


    @Test
    void getItemTest_WithoutBookings() {
        when(userStorage.existsUserById(anyLong()))
                .thenReturn(true);
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(createItem()));
        when(commentStorage.findAllByItemId(anyLong()))
                .thenReturn(List.of(createComment()));
        when(bookingStorage.getLastBooking(any(LocalDateTime.class), anyLong(), anyLong()))
                .thenReturn(List.of(createBooking()));
        when(bookingStorage.getNextBooking(any(LocalDateTime.class), anyLong(), anyLong()))
                .thenReturn(List.of(createBooking()));

        ItemDtoResponse result = itemService.getItem(1L, 2L);
        assertNotNull(result);
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
        assertEquals(1L, result.getId());
    }

    @Test
    void getOwnItemsTest() {
        when(userStorage.existsUserById(anyLong()))
                .thenReturn(true);
        when(itemStorage.findAllByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(createItem())));
        when(commentStorage.findAllByItemId(anyLong()))
                .thenReturn(List.of(createComment()));
        when(bookingStorage.getLastBooking(any(LocalDateTime.class), anyLong(), anyLong()))
                .thenReturn(List.of(createBooking()));
        when(bookingStorage.getNextBooking(any(LocalDateTime.class), anyLong(), anyLong()))
                .thenReturn(List.of(createBooking()));

        List<ItemDtoResponse> result = itemService.getOwnItems(1L, 1, 1);
        assertNotNull(result);
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void searchItemsTest() {
        when(userStorage.existsUserById(anyLong()))
                .thenReturn(true);
        when(itemStorage.itemsSearch(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(createItem())));

        List<ItemDtoResponse> result = itemService.searchItems("descr", 1L, 1, 1);
        assertNotNull(result);
        assertEquals(1, result.get(0).getId());
    }

    @Test
    void searchItemsTest_NoData() {
        when(userStorage.existsUserById(anyLong()))
                .thenReturn(true);

        List<ItemDtoResponse> result = itemService.searchItems(null, 1L, 1, 1);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void createCommentTest() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(createUser()));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(createItem()));
        when(commentStorage.save(any(Comment.class)))
                .thenReturn(createComment());
        when(bookingStorage.findBookingForComments(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(createBooking()));

        CommentDtoRequest request = createCommentRequest();
        CommentDtoResponse result = itemService.createComment(request, 1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("name", result.getAuthorName());
    }

    @Test
    void createCommentTest_throwNotUsed() {
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(createUser()));
        when(itemStorage.findById(anyLong()))
                .thenReturn(Optional.of(createItem()));
        when(commentStorage.save(any(Comment.class)))
                .thenReturn(createComment());
        when(bookingStorage.findBookingForComments(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>());

        Throwable exception = assertThrows(
                CommentException.class,
                () -> {
                    CommentDtoRequest request = createCommentRequest();
                    itemService.createComment(request, 1L, 1L);
                }
        );
        assertEquals("Комментировать можно только то, чем пользуетесь", exception.getMessage());
    }

    private ItemDtoRequest createDtoRequest() {
        return new ItemDtoRequest(1L, "name", "descr", true, 1L);
    }

    private ItemDtoRequest updateRequest() {
        return new ItemDtoRequest(1L, "updated", "descr", true, 1L);
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

    private Comment createComment() {
        return new Comment(1L, "text", createItem(), createUser(), LocalDateTime.now());
    }

    private CommentDtoRequest createCommentRequest() {
        return new CommentDtoRequest("text");
    }

    private Booking createBooking() {
        return new Booking(1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                createItem(),
                createUser(),
                BookingStatus.APPROVED);
    }
}