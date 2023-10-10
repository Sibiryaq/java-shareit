package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ItemRequestDtoResponse {

    private Long id;

    private String description;

    private LocalDateTime created;
}
