package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {
    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Test
    void createBookingTest() throws Exception {
        BookingDtoResponse response = createResponse();

        when(bookingService.createBooking(any(BookingDtoRequest.class), anyLong()))
                .thenReturn(response);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(createRequest()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(response.getId()), Long.class));
        verify(bookingService, times(1)).createBooking(any(BookingDtoRequest.class), anyLong());
    }

    @Test
    void changeStateTest() throws Exception {
        BookingDtoResponse response = createResponse();

        when(bookingService.changeState(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(response);

        mvc.perform(patch("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("approved", "TRUE")
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(response.getId()), Long.class));
        verify(bookingService, times(1)).changeState(anyLong(), anyBoolean(), anyLong());
    }

    @Test
    void getBookingTest() throws Exception {
        BookingDtoResponse response = createResponse();

        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(response);

        mvc.perform(get("/bookings/1")
                        .content(mapper.writeValueAsString(createRequest()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(response.getId()), Long.class));
        verify(bookingService, times(1)).getBooking(anyLong(), anyLong());
    }

    @Test
    void getOwnBookingsTest() throws Exception {
        List<BookingDtoResponse> responses = List.of(createResponse());

        when(bookingService.getOwnBookings(any(BookingState.class), anyLong(), anyInt(), anyInt()))
                .thenReturn(responses);

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.[0].id", Matchers.is(responses.get(0).getId()), Long.class));
        verify(bookingService, times(1)).getOwnBookings(any(BookingState.class), anyLong(), anyInt(), anyInt());
    }

    @Test
    void getOwnItemsBookingsTest() throws Exception {
        List<BookingDtoResponse> responses = List.of(createResponse());

        when(bookingService.getOwnItemsBookings(any(BookingState.class), anyLong(), anyInt(), anyInt()))
                .thenReturn(responses);

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.[0].id", Matchers.is(responses.get(0).getId()), Long.class));
        verify(bookingService, times(1)).getOwnItemsBookings(any(BookingState.class), anyLong(), anyInt(), anyInt());
    }


    private BookingDtoResponse createResponse() {
        return new BookingDtoResponse(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                null,
                BookingStatus.APPROVED
        );
    }

    private BookingDtoRequest createRequest() {
        return new BookingDtoRequest(
                null,
                null,
                1L
        );
    }

}
