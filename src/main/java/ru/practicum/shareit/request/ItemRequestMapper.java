package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDtoRequest itemRequestDtoRequest, User user) {
        return new ItemRequest(
                null,
                itemRequestDtoRequest.getDescription(),
                user,
                LocalDateTime.now()
        );
    }

    public static ItemRequestDtoResponse toItemRequestDtoResponse(ItemRequest itemRequest) {
        return new ItemRequestDtoResponse(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated()
        );
    }

    public static List<ItemRequestDtoWItemResponse> toItemRequestDtoWItemResponse(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(itemRequest -> new ItemRequestDtoWItemResponse(
                        itemRequest.getId(),
                        itemRequest.getDescription(),
                        itemRequest.getCreated(),
                        null
                )).collect(Collectors.toList());
    }

    public static List<ItemRequestDtoWItemResponse> toItemRequestDtoWItemResponse(Page<ItemRequest> pageItemRequests) {
        return pageItemRequests.stream()
                .map((ItemRequest itemRequest) -> new ItemRequestDtoWItemResponse(
                        itemRequest.getId(),
                        itemRequest.getDescription(),
                        itemRequest.getCreated(),
                        null
                )).collect(Collectors.toList());
    }

    public static ItemRequestDtoWItemResponse toItemRequestDtoWItRespSingle(ItemRequest itemRequest) {
        return new ItemRequestDtoWItemResponse(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                null
        );
    }
}
