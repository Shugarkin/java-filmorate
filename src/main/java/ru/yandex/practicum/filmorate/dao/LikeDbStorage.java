package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;

@Component
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;


    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
    }

    @Override
    public List<Film> getPopularFilms(Integer end) {
        String sqlFilms = "select * " +
                " from LIKE_VAULT right JOIN FILMS ON LIKE_VAULT.FILM_ID = FILMS.FILM_ID " +
                " join MPA on FILMS.MPA_ID = MPA.MPA_ID " +
                " GROUP BY FILMS.FILM_ID " +
                " ORDER BY COUNT(USER_ID) " +
                " DESC limit ?;";
        return jdbcTemplate.query(sqlFilms, this::getFilmId, end);
    }

    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        String filmRows = "SELECT F.*, MPA_NAME FROM FILMS F" +
                " JOIN MPA ON F.MPA_ID = MPA.MPA_ID " +
                " JOIN LIKE_VAULT LV1" +
                " ON F.FILM_ID = LV1.FILM_ID" +
                " JOIN LIKE_VAULT LV2" +
                " ON F.FILM_ID = LV2.FILM_ID" +
                " WHERE LV1.USER_ID = ? AND LV2.USER_ID = ?";
        return jdbcTemplate.query(filmRows, this::getFilmId, userId, friendId);
    }

    private Film getFilmId(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("FILMS.FILM_ID"))
                .name(resultSet.getString("FILM_NAME"))
                .description(resultSet.getString("DESCRIPTION"))
                .releaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate())
                .duration(resultSet.getLong("DURATION"))
                .mpa(Mpa.builder()
                        .id(resultSet.getInt("MPA_ID"))
                        .name(resultSet.getString("MPA_NAME")).build())
                .genres(new LinkedHashSet<>())
                .build();
    }
}