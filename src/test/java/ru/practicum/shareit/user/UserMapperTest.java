package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    @Test
    void toUserDtoTest() {
        User user = new User(1L, "name", "email");
        UserDtoResponse userDtoResponse = UserMapper.toUserDto(user);

        assertEquals(user.getId(), userDtoResponse.getId());
        assertEquals(user.getName(), userDtoResponse.getName());
        assertEquals(user.getEmail(), userDtoResponse.getEmail());
    }

    @Test
    void toUserDtoListTest() {
        List<User> users = List.of(
                new User(1L, "name1", "email1"),
                new User(2L, "name2", "email2"));

        List<UserDtoResponse> result = UserMapper.toUserDtoList(users);
        assertEquals(2, result.size());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void toUserTest() {
        UserDtoRequest userDtoRequest = new UserDtoRequest(1L, "name", "email");

        User user = UserMapper.toUser(userDtoRequest);
        assertEquals(userDtoRequest.getId(), user.getId());
        assertEquals(userDtoRequest.getName(), user.getName());
        assertEquals(userDtoRequest.getEmail(), user.getEmail());
    }


}
