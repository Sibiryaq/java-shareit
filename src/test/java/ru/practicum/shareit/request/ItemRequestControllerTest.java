package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @MockBean
    private ItemRequestService itemRequestService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Test
    void createRequestTest() throws Exception {
        ItemRequestDtoResponse request = createDto();

        when(itemRequestService.createRequest(any(ItemRequestDtoRequest.class), anyLong()))
                .thenReturn(request);

        mvc.perform(post("/requests")
                        .header(USER_HEADER, 1L)
                        .content(mapper.writeValueAsString(createRequest()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", Matchers.is(request.getDescription()), String.class));
        verify(itemRequestService, times(1)).createRequest(any(ItemRequestDtoRequest.class), anyLong());

    }

    @Test
    void findByUserIdTest() throws Exception {
        List<ItemRequestDtoWItemResponse> requests = List.of(createDtoWItem());

        when(itemRequestService.findByUserId(anyLong()))
                .thenReturn(requests);

        mvc.perform(get("/requests")
                        .header(USER_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.[0].id", Matchers.is(requests.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", Matchers.is(requests.get(0).getDescription()), String.class));
        verify(itemRequestService, times(1)).findByUserId(anyLong());
    }

    @Test
    void findFromAllTest() throws Exception {
        List<ItemRequestDtoWItemResponse> requests = List.of(createDtoWItem());

        when(itemRequestService.findFromAll(anyInt(), anyInt(), anyLong()))
                .thenReturn(requests);

        mvc.perform(get("/requests/all")
                        .header(USER_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.[0].id", Matchers.is(requests.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", Matchers.is(requests.get(0).getDescription()), String.class));
        verify(itemRequestService, times(1)).findFromAll(anyInt(), anyInt(), anyLong());
    }

    @Test
    void findByRequestIdTest() throws Exception {
        ItemRequestDtoWItemResponse request = createDtoWItem();

        when(itemRequestService.findByRequestId(anyLong(), anyLong()))
                .thenReturn(request);

        mvc.perform(get("/requests/1")
                        .header(USER_HEADER, 1L)
                        .content(mapper.writeValueAsString(createRequest()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", Matchers.is(request.getDescription()), String.class));
        verify(itemRequestService, times(1)).findByRequestId(anyLong(), anyLong());
    }

    private ItemRequestDtoResponse createDto() {
        return new ItemRequestDtoResponse(1L, "descr", LocalDateTime.now());
    }

    private ItemRequestDtoWItemResponse createDtoWItem() {
        return new ItemRequestDtoWItemResponse(1L, "descr", LocalDateTime.now(), new ArrayList<>());
    }

    private ItemRequestDtoRequest createRequest() {
        return new ItemRequestDtoRequest("descr");
    }
}
