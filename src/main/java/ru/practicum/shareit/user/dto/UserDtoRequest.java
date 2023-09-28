package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDtoRequest {

    private Long id;

    @NotNull
    private String name;

    @Email
    @NotNull
    private String email;
}