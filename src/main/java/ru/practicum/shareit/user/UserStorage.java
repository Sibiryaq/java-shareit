package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User add(User user);

    User update(User user, Long userId);

    List<User> getAll();

    void deleteById(Long userId);

    Optional<User> getById(Long id);
}

