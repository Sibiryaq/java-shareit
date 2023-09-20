package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public User add(@Valid @RequestBody User user) throws ConflictException {
        log.info("Получен запрос POST /users");
        return userService.add(user);
    }

    @GetMapping(value = "/{id}")
    public User getUserById(@PathVariable long id) throws NotFoundException {
        log.info(String.format("Получен запрос GET /users/%s", id));
        return userService.getUserById(id);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Получен запрос GET /users");
        return userService.getAllUsers();
    }

    @PatchMapping(value = "/{id}")
    public User update(@RequestBody User user, @PathVariable long id) throws NotFoundException {
        log.info(String.format("Получен запрос PATCH /users/%s", id));
        return userService.update(user, id);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable long id) {
        log.info(String.format("Получен запрос DELETE /users/%s", id));
        userService.delete(id);
    }
}