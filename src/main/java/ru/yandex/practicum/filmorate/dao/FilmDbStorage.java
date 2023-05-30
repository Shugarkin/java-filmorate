package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmIsNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "select * " +
                "from MPA, FILMS " +
                " where MPA.MPA_ID = FILMS.MPA_ID ";
        return jdbcTemplate.query(sqlQuery, this::findFilm);
    }

    @Override
    public Film createFilms(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sqlQuery = "insert into films (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)"
                + " values(?,?,?,?,?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (getFilmForId(film.getId()) == null) {
            throw new FilmIsNotFoundException("Нет такого фильма");
        }

        String sqlQuery = "update FILMS set " +
                "FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ?  " +
                "where FILM_ID = ?";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        return film;
    }

    @Override
    public Film getFilmForId(int id) {
        String sqlQuery = "select * " +
                "from  MPA, FILMS  where FILMS.MPA_ID = MPA.MPA_ID and FILM_ID = ?";
        try {
            return jdbcTemplate.query(sqlQuery, this::findFilm, id).iterator().next();
        } catch (RuntimeException e) {
            throw new FilmIsNotFoundException("Не найден");
        }
    }

    @Override
    public void deleteFilmById(int id) {
        String sqlQuery = "delete from FILMS where FILM_ID = ?";
        try {
            jdbcTemplate.update(sqlQuery, id);
        } catch (RuntimeException e) {
            throw new FilmIsNotFoundException("Фильм не найден");
        }
    }

    @Override
    public List<Film> getFilmsByDirectorSortedByLikes(int directorId) {
        String sqlQuery = "select f.film_id, f.film_name, f.description, f.release_date, f.duration, " +
                "f.mpa_id, m.mpa_name " +
                "from films as f join mpa as m on f.mpa_id = m.mpa_id " +
                "left join like_vault as l on f.film_id = l.film_id " +
                "left join film_director as fd on f.film_id = fd.film_id " +
                "where fd.director_id = ? " +
                "group by f.film_id " +
                "order by count(l.film_id) desc";
        return jdbcTemplate.query(sqlQuery, this::findFilm, directorId);
    }

    @Override
    public List<Film> getFilmsByDirectorSortedByYears(int directorId) {
        String sqlQuery = "select f.film_id, f.film_name, f.description, f.release_date, f.duration, " +
                "f.mpa_id, m.mpa_name " +
                "from films as f join mpa as m on f.mpa_id = m.mpa_id " +
                "inner join film_director as fd on f.film_id = fd.film_id " +
                "where fd.director_id = ? " +
                "order by f.release_date";
        return jdbcTemplate.query(sqlQuery, this::findFilm, directorId);
    }

    @Override
    public void addDirectorToFilm(int filmId, int directorId) {
        String sqlQuery = "insert into film_director (film_id, director_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, directorId);
    }

    @Override
    public List<Film> findFilmsByDirector(String query) {
        String sqlQuery = "select FILMS.film_id, FILMS.film_name, FILMS.description, FILMS.release_date, FILMS.duration, FILMS.mpa_id, " +
                "MPA.mpa_name, DIRECTORS.DIRECTOR_ID, DIRECTORS.DIRECTOR_NAME " +
                "from LIKE_VAULT right JOIN FILMS ON LIKE_VAULT.FILM_ID = FILMS.FILM_ID " +
                "left JOIN FILM_DIRECTOR ON FILMS.FILM_ID = FILM_DIRECTOR.FILM_ID " +
                "left join DIRECTORS on FILM_DIRECTOR.DIRECTOR_ID = DIRECTORS.DIRECTOR_ID " +
                "join MPA on FILMS.MPA_ID = MPA.MPA_ID " +
                "where LOWER(DIRECTORS.DIRECTOR_NAME) like ? " +
                "GROUP BY FILMS.FILM_ID " +
                "ORDER BY COUNT(LIKE_VAULT.USER_ID) DESC";
        return jdbcTemplate.query(sqlQuery, this::findFilm, "%" + query + "%");
    }

    @Override
    public List<Film> findFilmsByTitle(String query) {
        String sql = "SELECT * " +
                "FROM FILMS " +
                "join MPA on FILMS.MPA_ID = MPA.MPA_ID " +
                "LEFT JOIN FILM_DIRECTOR FD on FILMS.FILM_ID = FD.FILM_ID " +
                "LEFT JOIN DIRECTORS D on FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                "LEFT JOIN LIKE_VAULT ON films.FILM_ID = LIKE_VAULT.FILM_ID " +
                "WHERE LOWER(FILMS.FILM_NAME) like ? " +
                "GROUP BY FILMS.FILM_ID, LIKE_VAULT.USER_ID " +
                "ORDER BY COUNT(LIKE_VAULT.USER_ID) DESC ";
        return jdbcTemplate.query(sql, this::findFilm, "%" + query + "%");
    }

    @Override
    public List<Film> findFilmsByDirectorTitle(String query) {
        String sql = "select FILMS.film_id, FILMS.film_name, FILMS.description, FILMS.release_date, FILMS.duration, FILMS.mpa_id, " +
                "MPA.mpa_name, DIRECTORS.DIRECTOR_ID, DIRECTORS.DIRECTOR_NAME " +
                "from LIKE_VAULT right JOIN FILMS ON LIKE_VAULT.FILM_ID = FILMS.FILM_ID " +
                "left JOIN FILM_DIRECTOR ON FILMS.FILM_ID = FILM_DIRECTOR.FILM_ID " +
                "left join DIRECTORS on FILM_DIRECTOR.DIRECTOR_ID = DIRECTORS.DIRECTOR_ID " +
                "join MPA on FILMS.MPA_ID = MPA.MPA_ID " +
                "where LOWER(FILMS.FILM_NAME) like ? or LOWER(DIRECTORS.DIRECTOR_NAME) like ? " +
                "GROUP BY FILMS.FILM_ID " +
                "ORDER BY COUNT(LIKE_VAULT.USER_ID) DESC";
        return jdbcTemplate.query(sql, this::findFilm, "%" + query + "%", "%" + query + "%");
    }

    private Film findFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("FILM_ID"))
                .name(resultSet.getString("FILM_NAME"))
                .description(resultSet.getString("DESCRIPTION"))
                .releaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate())
                .duration(resultSet.getLong("DURATION"))
                .mpa(Mpa.builder()
                        .id(resultSet.getInt("MPA_ID"))
                        .name(resultSet.getString("MPA_NAME")).build())
                .genres(new LinkedHashSet<>())
                .directors(new HashSet<>())
                .build();
    }

    @Override
    public List<Film> getListFilm(List<Integer> list) {
        String s = String.join(",", Collections.nCopies(list.size(), "?"));

        List<Film> listFilm = jdbcTemplate.query(
                String.format("select * from MPA, FILMS where FILMS.MPA_ID = MPA.MPA_ID and FILM_ID in (%s)", s),
                list.toArray(), this::findFilm);
        return listFilm;
    }


    @Override
    public Integer getFilm(Integer idReview) {
        String sql = "select film_id from review where reviewId = ?";
        return jdbcTemplate.queryForObject(sql, this::findFilmID, idReview);
    }

    private Integer findFilmID(ResultSet res, int rowNum) throws SQLException {
        return res.getInt("film_id");
    }
}
