package ru.practicum.shareit.user.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDtoResponse {

    private Long id;

    private String name;

    private String email;
}
