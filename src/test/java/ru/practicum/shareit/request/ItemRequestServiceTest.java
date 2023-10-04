package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.request.service.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemRequestServiceTest {

    private ItemRequestService itemRequestService;
    private ItemRequestStorage itemRequestStorage;
    private ItemStorage itemStorage;
    private UserStorage userStorage;

    @BeforeEach
    void init() {
        itemRequestStorage = mock(ItemRequestStorage.class);
        userStorage = mock(UserStorage.class);
        itemStorage = mock(ItemStorage.class);
        itemRequestService = new ItemRequestServiceImpl(itemStorage, userStorage, itemRequestStorage);
    }

    @Test
    void createRequestTest() {
        ItemRequestDtoResponse response = createResponse();

        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(createUser()));
        when(itemRequestStorage.save(any(ItemRequest.class)))
                .thenReturn(createItemRequest());

        ItemRequestDtoResponse result = itemRequestService.createRequest(createRequest(), 1L);

        assertNotNull(result);
        assertEquals(response.getId(), result.getId());
        assertEquals(response.getDescription(), result.getDescription());
    }

    @Test
    void findByUserIdTest() {
        List<ItemRequest> requests = List.of(createItemRequest());

        when(userStorage.existsUserById(anyLong()))
                .thenReturn(true);
        when(itemRequestStorage.findAllByRequestorIdOrderByCreatedDesc(anyLong()))
                .thenReturn(requests);
        when(itemStorage.findAllByRequestId(anyLong()))
                .thenReturn(List.of(createItem()));

        List<ItemRequestDtoWItemResponse> result = itemRequestService.findByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(requests.get(0).getId(), result.get(0).getId());
    }

    @Test
    void findFromAllTest() {
        when(userStorage.existsUserById(anyLong()))
                .thenReturn(true);
        when(itemRequestStorage.findAllOtherRequests(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(createItemRequest())));
        when(itemStorage.findAllByRequestId(anyLong()))
                .thenReturn(List.of(createItem()));

        List<ItemRequestDtoWItemResponse> result = itemRequestService.findFromAll(1, 1, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findByRequestIdTest() {
        when(userStorage.existsUserById(anyLong()))
                .thenReturn(true);
        when(itemRequestStorage.findById(anyLong()))
                .thenReturn(Optional.of(createItemRequest()));

        ItemRequestDtoWItemResponse result = itemRequestService.findByRequestId(1L, 1L);

        assertNotNull(result);
    }

    @Test
    void findByRequestIdTest_ThrowUserEmpty() {
        when(userStorage.existsUserById(anyLong()))
                .thenReturn(true);

        Throwable exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.findByRequestId(1L, null)
        );
        assertEquals("Пользователь пустой", exception.getMessage());
    }

    @Test
    void findByRequestIdTest_ThrowUserNotExists() {
        when(userStorage.existsUserById(anyLong()))
                .thenReturn(false);

        Throwable exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.findByRequestId(1L, 1L)
        );
        assertEquals("Пользователя не существует: 1", exception.getMessage());
    }

    @Test
    void findByRequestIdTest_throw() {
        when(userStorage.existsUserById(anyLong()))
                .thenReturn(true);
        when(itemRequestStorage.findById(anyLong()))
                .thenReturn(Optional.empty());

        Throwable exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.findByRequestId(1L, 1L)
        );
        assertEquals("Запрос с ID 1 не существует", exception.getMessage());
    }

    private User createUser() {
        return new User(1L, "name", "test@mail.ru");
    }

    private ItemRequestDtoResponse createResponse() {
        return new ItemRequestDtoResponse(1L, "descr", LocalDateTime.now());
    }

    private ItemRequestDtoRequest createRequest() {
        return new ItemRequestDtoRequest("descr");
    }

    private ItemRequest createItemRequest() {
        return new ItemRequest(1L, "descr", createUser(), LocalDateTime.now());
    }

    private Item createItem() {
        return new Item(1L, "name", "descr", true, createUser(), createItemRequest());
    }
}
