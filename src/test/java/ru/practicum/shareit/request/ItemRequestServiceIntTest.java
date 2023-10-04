package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoWItemResponse;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestServiceIntTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private ItemRequestService service;

    @Autowired
    private UserService userService;

    @Test
    void createRequestTest() {
        UserDtoResponse user = createUser();
        ItemRequestDtoRequest request = new ItemRequestDtoRequest("text");
        service.createRequest(request, user.getId());
        TypedQuery<ItemRequest> query2 = em.createQuery(
                "select ir from ItemRequest as ir where ir.description = :text", ItemRequest.class);
        ItemRequest itemRequest = query2.setParameter("text", request.getDescription()).getSingleResult();

        assertNotNull(itemRequest);
        assertNotNull(itemRequest.getId());
        assertEquals(user.getId(), itemRequest.getRequestor().getId());
    }

    @Test
    void createRequestTest_Throw() {
        ItemRequestDtoRequest request = new ItemRequestDtoRequest("text");
        Throwable exception = assertThrows(
                NotFoundException.class,
                () -> service.createRequest(request, 1L)
        );
        assertEquals("Пользователь с ID 1 не существует", exception.getMessage());
    }

    @Test
    void findByRequestIdTest() {
        UserDtoResponse user = createUser();
        ItemRequestDtoRequest request = new ItemRequestDtoRequest("text");
        ItemRequestDtoResponse created = service.createRequest(request, user.getId());
        ItemRequestDtoWItemResponse response = service.findByRequestId(created.getId(), user.getId());

        assertNotNull(response);
        assertEquals(request.getDescription(), response.getDescription());
    }

    @Test
    void findByUserIdTest() {
        UserDtoResponse user = createUser();
        ItemRequestDtoRequest request1 = new ItemRequestDtoRequest("text1");
        service.createRequest(request1, user.getId());
        List<ItemRequestDtoWItemResponse> result = service.findByUserId(user.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void deleteByIdTest() {
        UserDtoResponse user = createUser();
        ItemRequestDtoRequest request1 = new ItemRequestDtoRequest("text1");
        service.createRequest(request1, user.getId());
        List<ItemRequestDtoWItemResponse> result = service.findByUserId(user.getId());

        assertNotNull(result);
        assertEquals(1, result.size());

        service.deleteById(result.get(0).getId());
        List<ItemRequestDtoWItemResponse> result2 = service.findByUserId(user.getId());
        assertNotNull(result2);
        assertTrue(result2.isEmpty());
    }

    private UserDtoResponse createUser() {
        UserDtoRequest req = UserDtoRequest.builder()
                .name("name")
                .email("email@mail.ru")
                .build();
        return userService.createUser(req);
    }
}
