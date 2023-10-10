package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;

import java.util.List;

public interface UserService {

    UserDtoResponse createUser(UserDtoRequest userDtoRequest);

    UserDtoResponse updateUser(UserDtoRequest userDtoRequest);

    UserDtoResponse deleteUser(long userId);

    UserDtoResponse getUser(long userId);

    List<UserDtoResponse> getUsers();
}
