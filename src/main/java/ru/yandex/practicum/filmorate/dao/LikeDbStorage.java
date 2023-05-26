package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
        return "select FILMS.film_id, FILMS.film_name, FILMS.description, FILMS.release_date, FILMS.duration, FILMS.mpa_id," +
                "MPA.mpa_name, DIRECTORS.DIRECTOR_ID, DIRECTORS.DIRECTOR_NAME " +
                " from LIKE_VAULT right JOIN FILMS ON LIKE_VAULT.FILM_ID = FILMS.FILM_ID " +
                "left JOIN FILM_DIRECTOR ON FILMS.FILM_ID = FILM_DIRECTOR.FILM_ID " +
                "left join DIRECTORS on FILM_DIRECTOR.DIRECTOR_ID = DIRECTORS.DIRECTOR_ID " +
                " join MPA on FILMS.MPA_ID = MPA.MPA_ID " +
                " right join GENRE_FILM on FILMS.FILM_ID = GENRE_FILM.FILM_ID " +
                " where GENRE_ID = ? " +
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

    private String getSqlWithYear() {
        return "select FILMS.film_id, FILMS.film_name, FILMS.description, FILMS.release_date, FILMS.duration, FILMS.mpa_id," +
                "MPA.mpa_name, DIRECTORS.DIRECTOR_ID, DIRECTORS.DIRECTOR_NAME " +
                " from LIKE_VAULT right JOIN FILMS ON LIKE_VAULT.FILM_ID = FILMS.FILM_ID " +
                "left JOIN FILM_DIRECTOR ON FILMS.FILM_ID = FILM_DIRECTOR.FILM_ID " +
                "left join DIRECTORS on FILM_DIRECTOR.DIRECTOR_ID = DIRECTORS.DIRECTOR_ID " +
                " join MPA on FILMS.MPA_ID = MPA.MPA_ID " +
                " where EXTRACT(YEAR FROM(FILMS.RELEASE_DATE)) = ?" +
                " GROUP BY FILMS.FILM_ID " +
                " ORDER BY COUNT(LIKE_VAULT.USER_ID) DESC " +
                " limit ?";
    }

    private String getSqlWithGenreAndYear() {
        return  "select FILMS.film_id, FILMS.film_name, FILMS.description, FILMS.release_date, FILMS.duration, FILMS.mpa_id," +
                "MPA.mpa_name, DIRECTORS.DIRECTOR_ID, DIRECTORS.DIRECTOR_NAME " +
                "from LIKE_VAULT " +
                "right JOIN FILMS ON LIKE_VAULT.FILM_ID = FILMS.FILM_ID " +
                "left JOIN FILM_DIRECTOR ON FILMS.FILM_ID = FILM_DIRECTOR.FILM_ID " +
                "left join DIRECTORS on FILM_DIRECTOR.DIRECTOR_ID = DIRECTORS.DIRECTOR_ID " +
                "join MPA on FILMS.MPA_ID = MPA.MPA_ID join GENRE_FILM on FILMS.FILM_ID = GENRE_FILM.FILM_ID " +
                "where GENRE_ID = ? and EXTRACT(YEAR FROM(FILMS.RELEASE_DATE)) = ? " +
                "GROUP BY FILMS.FILM_ID " +
                "ORDER BY COUNT(LIKE_VAULT.USER_ID) DESC " +
                "limit ?";
    }

    private String getSqlWithoutGenreAndYear() {
        return "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, f.mpa_id, m.mpa_name, dire.DIRECTOR_ID, dire.DIRECTOR_NAME " +
                "FROM films AS f JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "left JOIN FILM_DIRECTOR as fd ON f.FILM_ID = fd.FILM_ID " +
                "left join DIRECTORS as dire on fd.DIRECTOR_ID = dire.DIRECTOR_ID " +
                "LEFT JOIN (SELECT film_id, COUNT(user_id) AS likes_count FROM LIKE_VAULT GROUP BY film_id " +
                "ORDER BY likes_count) AS popular ON f.film_id = popular.film_id " +
                "ORDER BY popular.likes_count DESC limit ?";
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
                .directors(getListDirector(resultSet))
                .build();
    }
}

    private Set<Director> getListDirector(ResultSet rs) throws SQLException {
        Set<Director> set = new HashSet<>();

        Director d = Director.builder()
                .id(rs.getInt("director_id"))
                .name(rs.getString("director_name"))
                .build();

        if (d.getName() != null || d.getId() > 0) {
            set.add(d);
        }
        return set;
    }
}