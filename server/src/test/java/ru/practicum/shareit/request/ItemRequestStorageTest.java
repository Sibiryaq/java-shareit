package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestStorageTest {

    @Autowired
    private ItemRequestStorage itemRequestStorage;

    @Autowired
    private UserStorage userStorage;

    private ItemRequest itemRequest;
    private User requestor;

    @BeforeEach
    void init() {
        User owner = User.builder()
                .name("owner")
                .email("test1@mail.ru")
                .build();

        requestor = User.builder()
                .name("requestor")
                .email("test2@mail.ru")
                .build();

        owner = userStorage.save(owner);
        requestor = userStorage.save(requestor);

        itemRequest = ItemRequest.builder()
                .description("desc")
                .requestor(owner)
                .created(LocalDateTime.now())
                .build();

        itemRequest = itemRequestStorage.save(itemRequest);

    }

    @Test
    void findAllOtherRequestsTest() {
        Page<ItemRequest> result = itemRequestStorage.findAllOtherRequests(
                requestor.getId(), Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getSize());
        assertEquals(itemRequest, result.getContent().get(0));
    }
}
