package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import org.junit.jupiter.api.Assertions;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmApplicationTests {

    private final FilmStorage filmStorage;

    private final LikeStorage likeStorage;

    private Film newFilm;

    @BeforeEach
    public void setUp() {
        newFilm = filmStorage.createFilms(Film.builder() //создали фильм
                .name("nisi eiusmod")
                .description("adipisicing")
                .duration(100)
                .releaseDate(LocalDate.of(1999, 01, 12))
                .mpa(Mpa.builder().id(1).build())
                .build());

    }

    @Test
    public void createFilm() {
        Film film = filmStorage.createFilms(Film
                .builder() //создали фильм
                .name("ndbadbjwdmod")
                .description("adiewqhebhwb eking")
                .duration(100)
                .releaseDate(LocalDate.of(1999, 01, 12))
                .mpa(Mpa.builder().id(1).build())
                .build());

        Assertions.assertNotNull(film);
    }

    @Test
    public void getFilm() {
        Optional<Film> filmOptional = filmStorage.getFilmForId(1); //получение фильма по id

        Assertions.assertNotNull(filmOptional);
    }

    @Test
    public void getAllFilm() {
        List<Film> filmList = filmStorage.getAllFilms(); // получение все фильмов
        Assertions.assertNotNull(filmList);
    }

    @Test
    public void updateFilm() {
        Film updateFilm = filmStorage.updateFilm(Film.builder() //обновили фильм
                .id(1)
                .name("new nisi eiusmod")
                .description("new adipisicing")
                .duration(100)
                .releaseDate(LocalDate.of(1999, 01, 12))
                .mpa(Mpa.builder().id(1).build())
                .build());

        Assertions.assertNotEquals(updateFilm, newFilm);
    }

    @Test
    public void getPopularFilm() {
        List<Film> popularFilms = likeStorage.getPopularFilms(1);
        Assertions.assertNotNull(popularFilms);
    }
}