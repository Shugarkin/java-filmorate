package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmApplicationTests {

    private final FilmService filmService;

    private Film newFilm;

    @BeforeEach
    public void setUp() {
        LinkedHashSet<Genre> genre = new LinkedHashSet<>();
        genre.add(Genre.builder().id(1).name("Комедия").build());
        newFilm = filmService.createFilms(Film.builder() //создали фильм
                .name("nisi eiusmod")
                .description("adipisicing")
                .duration(100)
                .releaseDate(LocalDate.of(1999, 01, 12))
                .mpa(Mpa.builder().id(1).name("G").build())
                .genres(genre)
                .build());

    }

    @Test
    public void createFilm() {
        Film film = filmService.createFilms(Film
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
        Film filmOptional = filmService.getFilmForId(1); //получение фильма по id
        Assertions.assertNotNull(filmOptional);
    }

    @Test
    public void getAllFilm() {
        List<Film> filmList = filmService.getAllFilms(); // получение все фильмов
        Assertions.assertNotNull(filmList);
    }

    @Test
    public void updateFilm() {
        Film updateFilm = filmService.updateFilm(Film.builder() //обновили фильм
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
        List<Film> list1 = filmService.getPopularFilms(1, null, null);
        Assertions.assertNotNull(list1);

        List<Film> list2 = filmService.getPopularFilms(1, 1, null);
        Assertions.assertEquals(list2, List.of(filmService.getFilmForId(1)));

        List<Film> list3 = filmService.getPopularFilms(1, null, 1999);
        Assertions.assertEquals(list3, List.of(filmService.getFilmForId(1)));
    }
}