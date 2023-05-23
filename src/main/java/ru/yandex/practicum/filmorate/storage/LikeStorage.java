package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface LikeStorage {

    void deleteLike(Integer filmId, Integer userId);

    void addLike(Integer filmId, Integer userId);

    List<Film> getPopularFilms(Integer end, Integer genreId, Integer year);

}
