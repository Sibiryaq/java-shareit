package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

import javax.validation.Valid;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemDtoRequest itemDtoRequest,
                                             @RequestHeader(USER_HEADER) Long userId) {
        log.info("Item Controller: Вещь создана. ID пользователя: {}", userId);
        return itemClient.createItem(itemDtoRequest, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateOwnItem(@RequestBody ItemDtoRequest itemDtoRequest,
                                                @PathVariable long itemId,
                                                @RequestHeader(USER_HEADER) Long userId) {
        log.info("Item Controller: Владелец вещи обновлен. ID пользователя: {}, ID вещи {}", userId, itemId);
        itemDtoRequest.setId(itemId);
        return itemClient.updateOwnItem(itemDtoRequest, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable long itemId,
                                          @RequestHeader(USER_HEADER) Long userId) {
        log.info("Item Controller: Получена вещь. ID пользователя: {}, ID вещи: {}", userId, itemId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnItems(@RequestHeader(USER_HEADER) Long userId) {
        log.info("Item Controller: Получен владелец вещей. ID пользователя {}", userId);
        return itemClient.getOwnItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @RequestHeader(USER_HEADER) Long userId) {
        log.info("Item Controller: Найдены вещи. ID пользователя {}, текст: {}", userId, text);
        return itemClient.searchItems(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDtoRequest commentDtoRequest,
                                                @PathVariable long itemId,
                                                @RequestHeader(USER_HEADER) Long userId) {
        log.info("Item Controller: Создание комментария. ID пользователя: {} для вещи с ID {}", userId, itemId);
        return itemClient.createComment(commentDtoRequest, itemId, userId);
    }
}
