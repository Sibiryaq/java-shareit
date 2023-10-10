package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoWItemResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Slf4j
public class ItemRequestController {

    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoResponse createRequest(@RequestBody ItemRequestDtoRequest itemRequestDtoRequest,
                                                @RequestHeader(USER_HEADER) Long userId) {
        log.info("Request Controller: Создан запрос. ID пользователя {}", userId);
        return itemRequestService.createRequest(itemRequestDtoRequest, userId);
    }

    @GetMapping
    public List<ItemRequestDtoWItemResponse> findByUserId(@RequestHeader(USER_HEADER) Long userId) {
        log.info("Request Controller: Найдены все запросы от пользователя с ID {}", userId);
        return itemRequestService.findByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWItemResponse> findFromAll(@RequestHeader(USER_HEADER) Long userId,
                                                         @RequestParam(defaultValue = "0") int from,
                                                         @RequestParam(defaultValue = "10") int size) {
        log.info("Request Controller: Найдены все запросы от пользователя с ID {}", userId);
        return itemRequestService.findFromAll(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWItemResponse findByRequestId(@PathVariable Long requestId,
                                                       @RequestHeader(USER_HEADER) Long userId) {
        log.info("Request Controller: Поиск по запросу с ID {}. По пользователю с ID {}", requestId, userId);
        return itemRequestService.findByRequestId(requestId, userId);
    }

}
