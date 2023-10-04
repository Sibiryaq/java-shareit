package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemStorageTest {

    @Autowired
    private ItemStorage itemStorage;

    @Autowired
    private UserStorage userStorage;

    private User owner;
    private Item item;

    @BeforeEach
    void init() {
        owner = User.builder()
                .name("owner")
                .email("test1@mail.ru")
                .build();

        item = Item.builder()
                .name("Item Name")
                .description("descr")
                .available(true)
                .owner(owner)
                .build();

        owner = userStorage.save(owner);
        item = itemStorage.save(item);
    }

    @Test
    void findAllByOwnerIdTest() {
        Page<Item> result = itemStorage.findAllByOwnerId(owner.getId(), Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getSize());
        assertEquals(item, result.getContent().get(0));
    }

    @Test
    void itemSearchTest() {
        Page<Item> result = itemStorage.itemsSearch("eS", Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getSize());
        assertEquals(item, result.getContent().get(0));
    }
}
