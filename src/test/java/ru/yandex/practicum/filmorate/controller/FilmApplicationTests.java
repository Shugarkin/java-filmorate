package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import org.assertj.core.api.Assertions;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmApplicationTests {
    private final FilmStorage filmStorage;

    @Test
    public void contexTest() {
        Assertions.assertThat(filmStorage).isNotNull();
    }

    @Test
    public void filmTest() {

        Optional<Film> newFilm = filmStorage.createFilms(Film.builder() //создали фильм
                .name("nisi eiusmod")
                .description("adipisicing")
                .duration(100)
                .releaseDate(LocalDate.of(1999, 01, 12))
                .mpa(Mpa.builder().id(1).build())
                .build());


        Optional<Film> filmOptional = filmStorage.getFilmForId(1); //получение фильма по id

        Assertions.assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        Assertions.assertThat(film).hasFieldOrPropertyWithValue("id", 1)
                );

        List<Optional<Film>> filmList = filmStorage.getAllFilms(); // получение все фильмов

        for (Optional<Film> filmInList : filmList) {
            Assertions.assertThat(filmInList)
                    .isPresent()
                    .hasValueSatisfying(film ->
                            Assertions.assertThat(film).hasFieldOrPropertyWithValue("id", 1)
                    );
        }

        Optional<Film> updateFilm = filmStorage.updateFilm(Film.builder() //обновили фильм
                .id(1)
                .name("new nisi eiusmod")
                .description("new adipisicing")
                .duration(100)
                .releaseDate(LocalDate.of(1999, 01, 12))
                .mpa(Mpa.builder().id(1).build())
                .build());

        Assertions.assertThat(updateFilm)
                .isPresent()
                .hasValueSatisfying(film ->
                        Assertions.assertThat(film).hasFieldOrPropertyWithValue("name", "new nisi eiusmod")
                );

        List<Optional<Film>> popularFilms = filmStorage.getPopularFilms(1);

        for (Optional<Film> popularFilm : popularFilms) {
            Assertions.assertThat(popularFilm)
                    .isPresent()
                    .hasValueSatisfying(film ->
                            Assertions.assertThat(film).hasFieldOrPropertyWithValue("name", "new nisi eiusmod")
                    );
        }
    }
}