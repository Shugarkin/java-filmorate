package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

  @Data
  @Builder
public class User {

    private int id;

    @NotNull
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @NotNull
    private String login;

    private String name;

    private LocalDate birthday;
}
