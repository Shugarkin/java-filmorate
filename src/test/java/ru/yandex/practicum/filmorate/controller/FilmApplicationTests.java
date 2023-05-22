package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import org.junit.jupiter.api.Assertions;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmApplicationTests {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;
    private final DirectorStorage directorStorage;
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
        Film filmOptional = filmStorage.getFilmForId(1); //получение фильма по id
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

    @Test
    public void shouldGetFilmsByDirectorSorted() {
        Film secondFilm = Film.builder()
                .name("Титаник")
                .description("Мужик выиграл билет в лучшую жизнь, но встретил девушку и умер")
                .releaseDate(LocalDate.of(1997, 12, 19))
                .duration(189)
                .mpa(Mpa.builder()
                        .id(3).build())
                .build();
        Film thirdFilm = Film.builder()
                .name("Я - легенда")
                .description("Мужик выживал в зомби-апокалипсисе несколько лет, но встретил женщину и умер")
                .releaseDate(LocalDate.of(2007, 06, 30))
                .duration(195)
                .mpa(Mpa.builder()
                        .id(3).build())
                .build();

        User firstUser = User.builder()
                .email("user1@gmail.com")
                .login("user1")
                .name("Tom")
                .birthday(LocalDate.of(1956, 7, 9))
                .build();
        User secondUser = User.builder()
                .email("user2@gmail.com")
                .login("user2")
                .name("Liam")
                .birthday(LocalDate.of(1952, 8, 7))
                .build();

        Director firstDirector = Director.builder().name("Сарик Андреасян").build();
        Film film1 = filmStorage.getFilmForId(1);
        Film film2 = filmStorage.createFilms(secondFilm);
        Film film3 = filmStorage.createFilms(thirdFilm);
        Director dir1 = directorStorage.createDirector(firstDirector);
        User user1 = userStorage.createUser(firstUser);
        User user2 = userStorage.createUser(secondUser);
        filmStorage.addDirectorToFilm(film1.getId(), dir1.getId());
        filmStorage.addDirectorToFilm(film2.getId(), dir1.getId());
        filmStorage.addDirectorToFilm(film3.getId(), dir1.getId());
        likeStorage.addLike(film1.getId(), user1.getId());
        likeStorage.addLike(film2.getId(), user1.getId());
        likeStorage.addLike(film2.getId(), user2.getId());
        //сортируем по лайкам:
        List<Film> filmsSortByLikes = filmStorage.getFilmsByDirectorSortedByLikes(dir1.getId());
        Assertions.assertNotNull(filmsSortByLikes);
        Assertions.assertEquals(filmsSortByLikes.get(0).getName(), film2.getName());
        Assertions.assertEquals(filmsSortByLikes.get(1).getName(), film1.getName());
        Assertions.assertEquals(filmsSortByLikes.get(2).getName(), film3.getName());
        //сортиурем по году
        List<Film> filmsSortByYear = filmStorage.getFilmsByDirectorSortedByYears(dir1.getId());
        Assertions.assertEquals(filmsSortByYear.get(0).getName(), film2.getName());
        Assertions.assertEquals(filmsSortByYear.get(1).getName(), film1.getName());
        Assertions.assertEquals(filmsSortByYear.get(2).getName(), film3.getName());
    }
}