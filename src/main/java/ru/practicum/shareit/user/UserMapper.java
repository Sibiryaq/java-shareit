package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDtoResponse toUserDto(User user) {
        return new UserDtoResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static List<UserDtoResponse> toUserDtoList(List<User> users) {
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public static User toUser(UserDtoRequest userDtoRequest) {
        User user = new User();
        user.setId(userDtoRequest.getId());
        user.setName(userDtoRequest.getName());
        user.setEmail(userDtoRequest.getEmail());
        return user;
    }
}