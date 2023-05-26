package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Service
public class DirectorService {
    private DirectorStorage directorStorage;

    @Autowired
    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    public Director updateDirector(Director director) {
        return directorStorage.updateDirector(director);
    }

    public void deleteDirector(int id) {
        directorStorage.deleteDirector(id);
    }

    public Director getDirectorById(int id) {
        return directorStorage.getDirectorById(id);
    }

    public List<Director> getListAllDirectors() {
        return directorStorage.getListAllDirectors();
    }

    public boolean isDirectorExists(int id) {
        return directorStorage.isDirectorExists(id);
    }

    public void addDirector(int filmId, int directorId) {
        directorStorage.addDirector(filmId, directorId);
    }

    public void updateDirectorInFilm(Film film) {
        directorStorage.updateDirectorInFilm(film);
    }

    public boolean isDirectorExists(String name) {
        return directorStorage.isDirectorExistsByName(name);
    }
}
