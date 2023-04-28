package ru.yandex.practicum.filmorate.storage;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    public List<Optional<Film>> getAllFilms();

    public Film createFilms(Film film);

    public Film updateFilm(Film film);


    public Optional<Film> addLike(Integer filmId, Integer userId);

    public void deleteLike(Integer filmId, Integer userId);

    public Optional<Film> getFilmForId(int id);
}
