package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

public class FilmValidationTest {

    private Validator validator;
    private FilmController filmController;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        filmController = new FilmController();
    }

    @Test
    public void filmValidationTest() {
        Film film = Film.builder() //должен пройти проверку
                .name("testFilm")
                .description("testFilm")
                .releaseDate(LocalDate.of(2000, 12, 01))
                .duration(60)
                .build();

        Set<ConstraintViolation<Film>> violation = validator.validate(film);
        Assertions.assertTrue(violation.isEmpty());

        Film film1 = Film.builder() //отсутствие name
                .description("testFilm")
                .releaseDate(LocalDate.of(2000, 12, 01))
                .duration(60)
                .build();

        Set<ConstraintViolation<Film>> violation1 = validator.validate(film1);
        Assertions.assertFalse(violation1.isEmpty());

        Film film2 = Film.builder() //описание более 200 символов
                .name("testFilm")
                .description("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaф")
                .releaseDate(LocalDate.of(2000, 12, 01))
                .duration(60)
                .build();

        Set<ConstraintViolation<Film>> violation2 = validator.validate(film2);
        Assertions.assertFalse(violation2.isEmpty());

        Film film3 = Film.builder() //без даты релиза
                .name("testFilm")
                .description("testFilm")
                .duration(60)
                .build();

        Set<ConstraintViolation<Film>> violation3 = validator.validate(film3);
        Assertions.assertFalse(violation3.isEmpty());

        Film film4 = Film.builder() //отрицательная продолжительность
                .name("testFilm")
                .description("testFilm")
                .releaseDate(LocalDate.of(2000, 12, 01))
                .duration(-60)
                .build();

        Set<ConstraintViolation<Film>> violation4 = validator.validate(film4);
        Assertions.assertFalse(violation4.isEmpty());

        Film film5 = Film.builder() //без описания
                .name("testFilm")
                .duration(60)
                .releaseDate(LocalDate.of(2000, 12, 01))
                .build();

        Set<ConstraintViolation<Film>> violation5 = validator.validate(film5);
        Assertions.assertFalse(violation5.isEmpty());

        Film film6 = Film.builder() // проверка кастомной анотации
                .name("testFilm")
                .duration(60)
                .description("testFilm")
                .releaseDate(LocalDate.of(1810, 12, 01))
                .build();

        Set<ConstraintViolation<Film>> violation6 = validator.validate(film6);
        Assertions.assertFalse(violation6.isEmpty());
    }
}
