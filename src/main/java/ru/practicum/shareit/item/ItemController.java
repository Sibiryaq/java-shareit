package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/items")
@AllArgsConstructor
@Slf4j
public class ItemController {
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDtoResponse createItem(@Valid @RequestBody ItemDtoRequest itemDtoRequest,
                                      @RequestHeader(USER_HEADER) Long userId) {
        log.info("Item Controller: Вещь создана. ID пользователя: {}", userId);
        return itemService.createItem(itemDtoRequest, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDtoResponse updateOwnItem(@RequestBody ItemDtoRequest itemDtoRequest,
                                         @PathVariable long itemId,
                                         @RequestHeader(USER_HEADER) Long userId) {
        log.info("Item Controller: Владелец вещи обновлен. ID пользователя: {}, ID вещи {}", userId, itemId);
        itemDtoRequest.setId(itemId);
        return itemService.updateOwnItem(itemDtoRequest, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoResponse getItem(@PathVariable long itemId,
                                   @RequestHeader(USER_HEADER) Long userId) {
        log.info("Item Controller: Получена вещь. ID пользователя: {}, ID вещи: {}", userId, itemId);
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoResponse> getOwnItems(@RequestHeader(USER_HEADER) Long userId) {
        log.info("Item Controller: Получены вещи пользователя. ID пользователя: {}", userId);
        return itemService.getOwnItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDtoResponse> searchItems(@RequestParam String text,
                                             @RequestHeader(USER_HEADER) Long userId) {
        log.info("Item Controller: Поиск вещей. ID пользователя: {}, запрос: {}", userId, text);
        return itemService.searchItems(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse createComment(@Valid @RequestBody CommentDtoRequest commentDtoRequest,
                                            @PathVariable long itemId,
                                            @RequestHeader(USER_HEADER) Long userId) {
        log.info("Item Controller: Создание комментария. ID пользователя: {} для вещи с ID {}", userId, itemId);
        return itemService.createComment(commentDtoRequest, itemId, userId);
    }
}