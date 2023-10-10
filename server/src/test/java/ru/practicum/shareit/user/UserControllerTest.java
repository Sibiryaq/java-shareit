package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @MockBean
    private UserService userService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @Test
    void createUserTest() throws Exception {
        UserDtoResponse user = createUser();

        when(userService.createUser(any(UserDtoRequest.class)))
                .thenReturn(user);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(createRequest()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(user.getName()), String.class))
                .andExpect(jsonPath("$.email", Matchers.is(user.getEmail()), String.class));

        verify(userService, times(1)).createUser(any(UserDtoRequest.class));
    }

    @Test
    void updateUserTest() throws Exception {
        UserDtoResponse user = createUser();

        when(userService.updateUser(any(UserDtoRequest.class)))
                .thenReturn(user);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(createRequest()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(user.getName()), String.class))
                .andExpect(jsonPath("$.email", Matchers.is(user.getEmail()), String.class));

        verify(userService, times(1)).updateUser(any(UserDtoRequest.class));
    }

    @Test
    void updateUserTest_noUserId() throws Exception {
        UserDtoResponse user = createUser();

        when(userService.updateUser(any(UserDtoRequest.class)))
                .thenReturn(user);

        mvc.perform(patch("/users/")
                        .content(mapper.writeValueAsString(createRequest()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        verify(userService, times(0)).updateUser(any(UserDtoRequest.class));
    }

    @Test
    void deleteUserTest() throws Exception {
        UserDtoResponse user = createUser();

        when(userService.deleteUser(Mockito.anyLong()))
                .thenReturn(user);

        mvc.perform(delete("/users/1")
                        .content(mapper.writeValueAsString(createRequest()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(user.getName()), String.class))
                .andExpect(jsonPath("$.email", Matchers.is(user.getEmail()), String.class));

        verify(userService, times(1)).deleteUser(anyLong());
    }

    @Test
    void getUserTest() throws Exception {
        UserDtoResponse user = createUser();

        when(userService.getUser(anyLong()))
                .thenReturn(user);

        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(user.getName()), String.class))
                .andExpect(jsonPath("$.email", Matchers.is(user.getEmail()), String.class));

        verify(userService, times(1)).getUser(anyLong());
    }

    @Test
    void getUsersTest() throws Exception {
        UserDtoResponse user = createUser();
        List<UserDtoResponse> result = new ArrayList<>();
        result.add(user);

        when(userService.getUsers())
                .thenReturn(result);

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)));

        verify(userService, times(1)).getUsers();
    }

    private UserDtoResponse createUser() {
        return new UserDtoResponse(
                1L,
                "Test Name",
                "test@mail.ru"
        );
    }

    private UserDtoRequest createRequest() {
        return new UserDtoRequest(
                null,
                "Test Name",
                "test@mail.ru"
        );
    }
}
