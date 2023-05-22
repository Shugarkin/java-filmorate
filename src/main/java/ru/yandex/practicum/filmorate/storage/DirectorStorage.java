package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DirectorStorage {

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(int id);

    Director getDirectorById(int id);

    List<Director> getListAllDirectors();

    boolean isDirectorExists(int id);

    void addDirector(int filmId, int directorId);

    void updateDirectorInFilm(Film film);
}
