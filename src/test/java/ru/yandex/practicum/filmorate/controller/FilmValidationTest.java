package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class FilmValidationTest {

    private Validator validator;

    private FilmController filmController;

    private FilmStorage inMemoryFilmStorage;

    private FilmService filmService;

    private UserStorage inMemoryUserStorage;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        inMemoryUserStorage = new InMemoryUserStorage();
        inMemoryFilmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(inMemoryFilmStorage);
        filmController = new FilmController(filmService);
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

        User user = User.builder()
                .email("test@user.ru")
                .login("testUser")
                .name("TestUser")
                .birthday(LocalDate.of(1993, 03, 25))
                .build();
        inMemoryUserStorage.createUser(user);

        Film film7 = Film.builder()
                .name("testFilm")
                .description("testFilm")
                .releaseDate(LocalDate.of(2000, 12, 01))
                .duration(60)
                .build();
        filmController.createFilms(film7);

        filmController.addLike(1, 1);
        Assertions.assertEquals(film7.getLike().isEmpty(), false); //проверка на добавление лайка

        List<Film> list = filmController.getPopularFilms(1);
        Film film8 = Film.builder()
                .name("testFilm2")
                .description("testFilm2")
                .releaseDate(LocalDate.of(2000, 12, 01))
                .duration(60)
                .build();
        inMemoryFilmStorage.createFilms(film8);
        Assertions.assertEquals(list, List.of(film7)); //проверка на популярный список

        Assertions.assertEquals(filmController.getFilmForId(1), film7);//проверка на получение по айди

        filmController.deleteLike(1,1);
        Assertions.assertEquals(film7.getLike().isEmpty(), true); //проверка на удаление лайка
    }
}
