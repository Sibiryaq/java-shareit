package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@JsonTest
public class BookingDtoRequestJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    public void testDeserializeValidJson() throws Exception {
        String json = "{\"start\":\"2023-10-05T10:00:00\",\"end\":\"2023-10-05T12:00:00\",\"itemId\":1}";

        BookingDtoRequest bookingDto = objectMapper.readValue(json, BookingDtoRequest.class);

        assertThat(bookingDto.getStart()).isEqualTo(LocalDateTime.of(
                2023, 10, 5, 10, 0, 0));
        assertThat(bookingDto.getEnd()).isEqualTo(LocalDateTime.of(
                2023, 10, 5, 12, 0, 0));
        assertThat(bookingDto.getItemId()).isEqualTo(1L);
    }

    @Test
    public void testSerializeToJson() throws Exception {
        BookingDtoRequest bookingDto = new BookingDtoRequest();
        bookingDto.setStart(LocalDateTime.of(2023, 10, 5, 10, 0, 0));
        bookingDto.setEnd(LocalDateTime.of(2023, 10, 5, 12, 0, 0));
        bookingDto.setItemId(1L);

        String json = objectMapper.writeValueAsString(bookingDto);

        assertThat(json).isEqualTo("{\"start\":\"2023-10-05T10:00:00\",\"end\":\"2023-10-05T12:00:00\",\"itemId\":1}");
    }
}
