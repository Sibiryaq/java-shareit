package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User add(User user) throws ConflictException {
        try {
            log.info("Добавлен новый User");
            return userRepository.save(user);
        } catch (Exception e) {
            throw new ConflictException("Такой User уже существует");
        }
    }

    public User getUserById(long id) throws NotFoundException {
        Optional<User> userIdDatabase = userRepository.findById(id);
        if (userIdDatabase.isPresent()) {
            log.info("Получен User с id: " + id);
            return userIdDatabase.get();
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User update(User user, long id) throws NotFoundException {
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User не найден"));

        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }

        if (user.getEmail() != null) {
            userToUpdate.setEmail(user.getEmail());
        }

        log.info("Обновлен пользователь с id: " + id);
        try {
            return userRepository.save(userToUpdate);
        } catch (Exception e) {
            throw new ConflictException(e.getMessage());
        }
    }

    public void delete(long id) {
        log.info("Удалён User с id: " + id);
        userRepository.deleteById(id);
    }
}