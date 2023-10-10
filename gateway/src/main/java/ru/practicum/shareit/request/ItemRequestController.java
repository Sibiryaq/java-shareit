package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@Valid @RequestBody ItemRequestDtoRequest itemRequestDtoRequest,
                                                @RequestHeader(USER_HEADER) Long userId) {
        log.info("Request Controller: Создан запрос. ID пользователя {}", userId);
        return itemRequestClient.createRequest(itemRequestDtoRequest, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findByUserId(@RequestHeader(USER_HEADER) Long userId) {
        log.info("Request Controller: Найдены все запросы от пользователя с ID {}", userId);
        return itemRequestClient.findByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findFromAll(@RequestHeader(USER_HEADER) Long userId,
                                              @RequestParam(defaultValue = "0")
                                              @Min(value = 0, message = "Minimum from: 0") int from,
                                              @RequestParam(defaultValue = "10")
                                              @Min(value = 1, message = "Minimum size: 1") int size) {
        log.info("Request Controller: Найдены все запросы от пользователя с ID {}", userId);
        return itemRequestClient.findFromAll(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findByRequestId(@PathVariable Long requestId,
                                                  @RequestHeader(USER_HEADER) Long userId) {
        log.info("Request Controller: Поиск по запросу с ID {}. По пользователю с ID {}", requestId, userId);
        return itemRequestClient.findByRequestId(requestId, userId);
    }

}
