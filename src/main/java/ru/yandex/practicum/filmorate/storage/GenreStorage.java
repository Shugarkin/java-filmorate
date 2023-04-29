package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.util.List;

public interface GenreStorage {

    public List<Genre> getGenreList();

    public Genre getGenre(int id);

    public void addGenre(Film film);

}
