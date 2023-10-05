package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoRequestTest {

    @Autowired
    private JacksonTester<ItemDtoRequest> jacksonTester;

    //Проверяет, корректно ли был сериализован объект ItemDtoRequest
    @Test
    void testSerialize() throws IOException {
        ItemDtoRequest item = new ItemDtoRequest();
        item.setId(1L);
        item.setName("TestItem");
        item.setDescription("TestDescription");
        item.setAvailable(true);
        item.setRequestId(100L);

        assertThat(this.jacksonTester.write(item)).extractingJsonPathStringValue("@.name")
                .isEqualTo("TestItem");
        assertThat(this.jacksonTester.write(item)).extractingJsonPathStringValue("@.description")
                .isEqualTo("TestDescription");
        assertThat(this.jacksonTester.write(item)).extractingJsonPathBooleanValue("@.available")
                .isEqualTo(true);
        assertThat(this.jacksonTester.write(item)).extractingJsonPathNumberValue("@.requestId")
                .isEqualTo(100);
    }
}
