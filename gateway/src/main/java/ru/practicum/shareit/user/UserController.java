package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDtoRequest;

import javax.validation.Valid;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDtoRequest userDtoRequest) {
        log.info("User Controller: Пользователь создан. Его email: {}", userDtoRequest.getEmail());
        return userClient.createUser(userDtoRequest);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDtoRequest userDtoRequest, @PathVariable long userId) {
        log.info("User Controller: Пользователь обновлен. ID пользователя: {}", userId);
        userDtoRequest.setId(userId);
        return userClient.updateUser(userDtoRequest);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable long userId) {
        log.info("User Controller: Пользователь удален. ID пользователя было: {}", userId);
        return userClient.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable long userId) {
        log.info("User Controller: Получен пользователь. ID пользователя: {}", userId);
        return userClient.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("User Controller: Получены все пользователи");
        return userClient.getUsers();
    }
}
