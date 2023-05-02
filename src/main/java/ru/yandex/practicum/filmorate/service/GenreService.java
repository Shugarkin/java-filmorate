package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
public class GenreService {
    private GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> getGenreList() {
        return genreStorage.getGenreList();
    }

    public Genre getGenre(int id) {
        return genreStorage.getGenre(id);
    }

    public void addGenre(Film film) {
        genreStorage.addGenre(film);
    }

    public void deleteGenre(int id) {
        genreStorage.deleteGenre(id);
    }

    public void load(List<Film> films) {
        genreStorage.load(films);
    }
}
