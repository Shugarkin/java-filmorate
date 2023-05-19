package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public List<Film> getPopularFilms(Integer end, Integer genreId, Integer year) {
        String sqlFilmId;
        List<Film> filmId;
        if (genreId == null && year == null) {
            sqlFilmId = getSqlWithoutGenreAndYear();
            filmId = jdbcTemplate.query(sqlFilmId, this::getFilmId, end);
        } else if (year == null) {
            sqlFilmId = getSqlWithGenre();
            filmId = jdbcTemplate.query(sqlFilmId, this::getFilmId, genreId, end);
        } else if (genreId == null) {
            sqlFilmId = getSqlWithYear();
            filmId = jdbcTemplate.query(sqlFilmId, this::getFilmId, year, end);
        } else {
            sqlFilmId = getSqlWithGenreAndYear();
            filmId = jdbcTemplate.query(sqlFilmId, this::getFilmId, genreId, year, end);
        }

        return filmId;
    }

    private String getSqlWithGenre() {
        return "select * " +
                " from LIKE_VAULT right JOIN FILMS ON LIKE_VAULT.FILM_ID = FILMS.FILM_ID " +
                " join MPA on FILMS.MPA_ID = MPA.MPA_ID " +
                " right join GENRE_FILM on FILMS.FILM_ID = GENRE_FILM.FILM_ID " +
                " where GENRE_ID = ? " +
                " GROUP BY FILMS.FILM_ID " +
                " ORDER BY COUNT(LIKE_VAULT.USER_ID) DESC " +
                " limit ?";
    }

    private String getSqlWithYear() {
        return "select * " +
                " from LIKE_VAULT right JOIN FILMS ON LIKE_VAULT.FILM_ID = FILMS.FILM_ID " +
                " join MPA on FILMS.MPA_ID = MPA.MPA_ID " +
                " where EXTRACT(YEAR FROM(FILMS.RELEASE_DATE)) = ?" +
                " GROUP BY FILMS.FILM_ID " +
                " ORDER BY COUNT(LIKE_VAULT.USER_ID) DESC " +
                " limit ?";
    }

    private String getSqlWithGenreAndYear() {
        return  "select * " +
                " from LIKE_VAULT right JOIN FILMS ON LIKE_VAULT.FILM_ID = FILMS.FILM_ID " +
                " join MPA on FILMS.MPA_ID = MPA.MPA_ID " +
                " join GENRE_FILM on FILMS.FILM_ID = GENRE_FILM.FILM_ID " +
                " where GENRE_ID = ? and EXTRACT(YEAR FROM(FILMS.RELEASE_DATE)) = ?" +
                " GROUP BY FILMS.FILM_ID " +
                " ORDER BY COUNT(LIKE_VAULT.USER_ID) DESC " +
                " limit ?";
    }

    private String getSqlWithoutGenreAndYear() {
        return  "select * " +
                " from LIKE_VAULT right JOIN FILMS ON LIKE_VAULT.FILM_ID = FILMS.FILM_ID " +
                " join MPA on FILMS.MPA_ID = MPA.MPA_ID " +
                " GROUP BY FILMS.FILM_ID " +
                " ORDER BY COUNT(USER_ID) " +
                " DESC limit ?;";
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
