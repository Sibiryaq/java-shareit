package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceIntTest {

    @Autowired
    private ItemService service;

    @Autowired
    private UserService userService;

    @Test
    void createItemTest() {
        UserDtoRequest userR = UserDtoRequest.builder()
                .name("name")
                .email("eto@mail.ru")
                .build();

        UserDtoResponse user = userService.createUser(userR);

        ItemDtoRequest itemR = ItemDtoRequest.builder()
                .name("name item")
                .description("descr")
                .available(true)
                .build();

        ItemDtoResponse item = service.createItem(itemR, user.getId());

        assertNotNull(item);
        assertNotNull(item.getId());
    }

    @Test
    void getOwnItemsTest() {
        UserDtoRequest userR = UserDtoRequest.builder()
                .name("name")
                .email("eto@mail.ru")
                .build();
        UserDtoResponse user = userService.createUser(userR);
        ItemDtoRequest itemR = ItemDtoRequest.builder()
                .name("name item")
                .description("descr")
                .available(true)
                .build();
        service.createItem(itemR, user.getId());
        service.createItem(itemR, user.getId());
        service.createItem(itemR, user.getId());
        List<ItemDtoResponse> result2 = service.getOwnItems(user.getId(), 1, 5);

        assertNotNull(result2);
        assertEquals(3, result2.size());
    }
}
