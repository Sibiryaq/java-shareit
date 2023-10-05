package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.user.dto.*;
import ru.practicum.shareit.user.service.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserService userService;
    private UserStorage userStorage;

    @BeforeEach
    void init() {
        userStorage = mock(UserStorage.class);
        userService = new UserServiceImpl(userStorage);
    }

    @Test
    void createUserTest() {
        User user = createUser();

        when(userStorage.save(any(User.class)))
                .thenReturn(user);

        UserDtoResponse result = userService.createUser(createDtoRequest());

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        verify(userStorage, times(1)).save(any(User.class));
    }

    @Test
    void updateUserTest() {
        User user = createUser();
        user.setEmail("updated@mail.ru");

        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user));

        UserDtoResponse result = userService.updateUser(createDtoRequest());
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        verify(userStorage, times(1)).findById(anyLong());
    }

    @Test
    void deleteUserTest() {
        User user = createUser();

        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user));

        userService.deleteUser(Mockito.anyLong());
        verify(userStorage, times(1)).deleteById(anyLong());
    }

    @Test
    void getUserTest() {
        User user = createUser();

        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user));

        UserDtoResponse result = userService.getUser(anyLong());

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        verify(userStorage, times(1)).findById(anyLong());
    }

    @Test
    void getUsersTest() {
        List<User> users = List.of(createUser(), createUser());

        when(userStorage.findAll())
                .thenReturn(users);

        List<UserDtoResponse> result = userService.getUsers();
        assertEquals(2, result.size());
        verify(userStorage, times(1)).findAll();
    }

    private User createUser() {
        return new User(1L, "name", "test@mail.ru");
    }

    private UserDtoRequest createDtoRequest() {
        return new UserDtoRequest(1L, "name", "test@mail.ru");
    }


}
