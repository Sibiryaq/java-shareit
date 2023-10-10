package ru.practicum.shareit.item.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CommentDtoRequest {

    private String text;
}
