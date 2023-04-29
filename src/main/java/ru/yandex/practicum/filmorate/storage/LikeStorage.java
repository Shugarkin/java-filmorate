package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface LikeStorage {

    public void deleteLike(Integer filmId, Integer userId);

    public void addLike(Integer filmId, Integer userId);

    public List<Film> getPopularFilms(Integer end);


}
