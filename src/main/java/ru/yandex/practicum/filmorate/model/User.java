package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
