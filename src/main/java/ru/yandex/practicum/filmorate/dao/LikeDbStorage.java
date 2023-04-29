package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Qualifier("LikeDbStorage")
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    private final FilmStorage filmStorage;

    public LikeDbStorage(JdbcTemplate jdbcTemplate, FilmStorage filmStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmStorage = filmStorage;
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        String sqlLike = "delete from LIKE_VAULT where FILM_ID = ? and USER_ID = ?";
        jdbcTemplate.update(sqlLike, filmId, userId);
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        String sqlLike = "insert into LIKE_VAULT (FILM_ID, USER_ID)"
                + " values(?,?)";
        jdbcTemplate.update(sqlLike, filmId, userId);
        filmStorage.getFilmForId(filmId);
    }

    @Override
    public List<Film> getPopularFilms(Integer end) {
        String sqlFilms = "select FILMS.FILM_ID " +
                "from LIKE_VAULT right JOIN FILMS ON LIKE_VAULT.FILM_ID = FILMS.FILM_ID " +
                "GROUP BY FILMS.FILM_ID " +
                "ORDER BY COUNT(USER_ID) " +
                "DESC limit ?;";
        List<String> filmId = jdbcTemplate.query(sqlFilms, this::getFilmId, end);

        List<Film> filmList = filmId.stream()
                .map(a -> Integer.parseInt(a))
                .map(s -> filmStorage.getFilmForId(s))
                .map(s -> s.get())
                .collect(Collectors.toList());

        return filmList;
    }

    private String getFilmId(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getString("FILM_ID");
    }
}
