package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Test
    void createItemTest() throws Exception {
        ItemDtoResponse response = createResponse();

        when(itemService.createItem(any(ItemDtoRequest.class), anyLong()))
                .thenReturn(response);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(createRequest()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(response.getId()), Long.class));
        verify(itemService, times(1)).createItem(any(ItemDtoRequest.class), anyLong());
    }

    @Test
    void updateOwnItemTest() throws Exception {
        ItemDtoResponse response = createResponse();

        when(itemService.updateOwnItem(any(ItemDtoRequest.class), anyLong()))
                .thenReturn(response);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(createRequest()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(response.getId()), Long.class));
        verify(itemService, times(1)).updateOwnItem(any(ItemDtoRequest.class), anyLong());
    }

    @Test
    void getItemTest() throws Exception {
        ItemDtoResponse response = createResponse();

        when(itemService.getItem(anyLong(), anyLong()))
                .thenReturn(response);

        mvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(response.getId()), Long.class));
        verify(itemService, times(1)).getItem(anyLong(), anyLong());
    }

    @Test
    void getOwnItemsTest() throws Exception {
        List<ItemDtoResponse> response = List.of(createResponse());

        when(itemService.getOwnItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(response);

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.[0].id", Matchers.is(response.get(0).getId()), Long.class));
        verify(itemService, times(1)).getOwnItems(anyLong(), anyInt(), anyInt());
    }

    @Test
    void searchItemsTest() throws Exception {
        List<ItemDtoResponse> response = List.of(createResponse());

        when(itemService.searchItems(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(response);

        mvc.perform(get("/items/search")
                        .param("text", "descr")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.[0].id", Matchers.is(response.get(0).getId()), Long.class));
        verify(itemService, times(1)).searchItems(anyString(), anyLong(), anyInt(), anyInt());
    }

    @Test
    void createCommentTest() throws Exception {
        CommentDtoResponse response = new CommentDtoResponse(1L, "text", "author", LocalDateTime.now());
        CommentDtoRequest request = new CommentDtoRequest("text");

        when(itemService.createComment(any(CommentDtoRequest.class), anyLong(), anyLong()))
                .thenReturn(response);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(response.getId()), Long.class));
        verify(itemService, times(1)).createComment(any(CommentDtoRequest.class), anyLong(), anyLong());
    }

    private ItemDtoResponse createResponse() {
        return new ItemDtoResponse(
                1L,
                "name",
                "descr",
                true,
                null,
                null,
                new ArrayList<>(),
                2L
        );
    }

    private ItemDtoRequest createRequest() {
        return new ItemDtoRequest(
                1L,
                "name",
                "descr",
                true,
                2L
        );
    }
}
