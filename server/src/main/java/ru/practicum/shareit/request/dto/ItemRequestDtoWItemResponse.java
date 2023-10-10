package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ItemRequestDtoWItemResponse {

    private Long id;

    private String description;

    private LocalDateTime created;

    private List<ItemDtoResponse> items;
}
