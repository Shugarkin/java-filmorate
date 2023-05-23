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
        String sqlFilms = "SELECT f.film_id, f.film_name, f.description, f.release_date," +
                " f.duration, f.mpa_id, m.mpa_name FROM films AS f JOIN mpa AS m " +
                "ON f.mpa_id = m.mpa_id LEFT JOIN (SELECT film_id, COUNT(user_id) AS likes_count " +
                "FROM LIKE_VAULT GROUP BY film_id ORDER BY likes_count) AS popular " +
                "ON f.film_id = popular.film_id ORDER BY popular.likes_count DESC limit ?";
        List<Film> filmId = jdbcTemplate.query(sqlFilms, this::getFilmId, end);

        return filmId;
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
