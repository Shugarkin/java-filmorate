package ru.yandex.practicum.filmorate.storage;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.Map;

public interface FilmStorage {
    public List<Film> getAllFilms();

    public Film createFilms(Film film);

    public Film updateFilm(Film film);

    public Map<Integer, Film> getMapFilms();

    public void addLike(Integer filmId, Integer userId);

    public void deleteLike(Integer filmId, Integer userId);

    public Film getFilmForId(int id);
}
