package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmIsNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserIsNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.*;
import java.sql.Date;
import java.util.*;

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
            PreparedStatement stmt = connection.prepareStatement(sqlQuery,  new String[]{"FILM_ID"});
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
                .build();
    }
}
