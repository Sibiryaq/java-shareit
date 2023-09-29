package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDtoResponse createUser(@Valid @RequestBody UserDtoRequest userDtoRequest) {
        log.info("User Controller: Пользователь создан. Его email: {}", userDtoRequest.getEmail());
        return userService.createUser(userDtoRequest);
    }

    @PatchMapping("/{userId}")
    public UserDtoResponse updateUser(@RequestBody UserDtoRequest userDtoRequest, @PathVariable long userId) {
        log.info("User Controller: Пользователь обновлен. ID пользователя: {}", userId);
        userDtoRequest.setId(userId);
        return userService.updateUser(userDtoRequest);
    }

    @DeleteMapping("/{userId}")
    public UserDtoResponse deleteUser(@PathVariable long userId) {
        log.info("User Controller: Пользователь удален. ID пользователя было: {}", userId);
        return userService.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public UserDtoResponse getUser(@PathVariable long userId) {
        log.info("User Controller: Получен пользователь. ID пользователя: {}", userId);
        return userService.getUser(userId);
    }

    @GetMapping
    public List<UserDtoResponse> getUsers() {
        log.info("User Controller: Получены все пользователи");
        return userService.getUsers();
    }

}
