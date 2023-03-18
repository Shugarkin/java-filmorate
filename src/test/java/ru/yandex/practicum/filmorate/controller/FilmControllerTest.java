package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    public void makeController() {
        filmController = new FilmController();
    }

    @Test
    public void FilmControllerTest() {
        boolean answer = filmController.getAllFilms().isEmpty();
        Assertions.assertEquals(answer, true, "Список не пуст");

        Film film = Film.builder()
                .name("testFilm")
                .description("testFilm")
                .releaseDate(LocalDate.of(2000, 12, 01))
                .duration(60)
                .build();

        filmController.createFilms(film);
        Assertions.assertEquals(filmController.getAllFilms().get(0), film);

        try {
            filmController.createFilms(Film.builder()
                            .name("testFilm")
                            .description("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                    "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                            .releaseDate(LocalDate.of(2000, 12, 01))
                            .duration(60)
                            .build());

        } catch (ValidationException e) {
            Assertions.assertEquals(e.getMessage(), "Максимальная длина описания — 200 символов");
        }

        try {
            filmController.createFilms(Film.builder()
                    .name("testFilm")
                    .description("testFilm")
                    .releaseDate(LocalDate.of(1810, 12, 01))
                    .duration(60)
                    .build());
        } catch (ValidationException e) {
            Assertions.assertEquals(e.getMessage(), "Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        try {
            filmController.createFilms(Film.builder()
                    .name("testFilm")
                    .description("testFilm")
                    .releaseDate(LocalDate.of(2000, 12, 01))
                    .duration(-60)
                    .build());
        } catch (ValidationException e) {
            Assertions.assertEquals(e.getMessage(), "Продолжительность фильма должна быть положительной");
        }


        Film film1 = Film.builder()
                .id(1)
                .name("testFilm")
                .description("testFilm")
                .releaseDate(LocalDate.of(2000, 12, 01))
                .duration(60)
                .build();
        filmController.updateFilm(film1);
        Assertions.assertEquals(filmController.getAllFilms().get(0), film1);

        try {
            filmController.updateFilm(Film.builder()
                    .id(2)
                    .name("testFilm")
                    .description("testFilm")
                    .releaseDate(LocalDate.of(2000, 12, 01))
                    .duration(60)
                    .build());
        } catch (ValidationException e) {
            Assertions.assertEquals(e.getMessage(), "Фильма с данным id нет");
        }

        try {
            filmController.updateFilm(Film.builder()
                    .id(1)
                    .name("testFilm")
                    .description("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                    .releaseDate(LocalDate.of(2000, 12, 01))
                    .duration(60)
                    .build());
        } catch (ValidationException e) {
            Assertions.assertEquals(e.getMessage(), "Максимальная длина описания — 200 символов");
        }

        try {
            filmController.updateFilm(Film.builder()
                    .id(2)
                    .name("testFilm")
                    .description("testFilm")
                    .releaseDate(LocalDate.of(1810, 12, 01))
                    .duration(60)
                    .build());
        } catch (ValidationException e) {
            Assertions.assertEquals(e.getMessage(), "Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        try {
            filmController.updateFilm(Film.builder()
                    .id(2)
                    .name("testFilm")
                    .description("testFilm")
                    .releaseDate(LocalDate.of(2000, 12, 01))
                    .duration(-60)
                    .build());
        } catch (ValidationException e) {
            Assertions.assertEquals(e.getMessage(), "Продолжительность фильма должна быть положительной");
        }
    }
}