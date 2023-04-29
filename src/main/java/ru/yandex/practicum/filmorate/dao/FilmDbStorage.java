package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmIsNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    private final GenreStorage genreStorage;

    private final MpaStorage mpaStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage, MpaStorage mpaStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    @Override
    public List<Film> getAllFilms() {
            String sqlQuery = "select FILMS.FILM_ID, FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID, GENRE_FILM.GENRE_ID " +
                    "from FILMS left join GENRE_FILM on FILMS.FILM_ID = GENRE_FILM.FILM_ID ";
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

        if (film.getGenres() != null) {
            genreStorage.addGenre(film);
        }

        String sqlGenreId = "select GENRE.GENRE_ID from GENRE_FILM" +
                " join GENRE on GENRE_FILM.GENRE_ID = GENRE.GENRE_ID " +
                " where FILM_ID = ?";
        LinkedHashSet<Genre> genre =  new LinkedHashSet<>(jdbcTemplate.query(sqlGenreId, this::findGenreId, film.getId())
                .stream()
                .map(s -> genreStorage.getGenre(s))
                .collect(Collectors.toList()));
        film.setGenres(genre);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (getFilmForId(film.getId()) == null) {
            throw new FilmIsNotFoundException("Нет такого фильма");
        }

        if (film.getGenres() != null) {
            genreStorage.addGenre(film);
        }

        String sqlGenreId = "select GENRE_FILM.GENRE_ID from GENRE_FILM join GENRE on GENRE_FILM.GENRE_ID = GENRE.GENRE_ID" +
                " where FILM_ID = ?";
        LinkedHashSet<Genre> genre = new LinkedHashSet<>(jdbcTemplate.query(sqlGenreId, this::findGenreId, film.getId())
                .stream()
                .map(s -> genreStorage.getGenre(s))
                .collect(Collectors.toList()));;
        film.setGenres(genre);

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
    public Optional<Film> getFilmForId(int id) {
            String sqlQuery = "select FILM_ID, FILM_NAME, " +
                    "DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID from FILMS where FILM_ID = ?";
            return Optional.of(jdbcTemplate.queryForObject(sqlQuery, this::findFilm, id));
    }



    private Film findFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = mpaStorage.getMpaById(Integer.parseInt(resultSet.getString("MPA_ID")));

        int filmId = resultSet.getInt("FILM_ID");
        String sqlGenreId = "select GENRE.GENRE_ID from GENRE_FILM join GENRE on GENRE_FILM.GENRE_ID = GENRE.GENRE_ID" +
                " where FILM_ID = ?";
        LinkedHashSet<Genre> genre = new LinkedHashSet<>(jdbcTemplate.query(sqlGenreId, this::findGenreId, filmId)
                .stream()
                .map(s -> genreStorage.getGenre(s))
                .collect(Collectors.toList()));

        return Film.builder()
                .id(resultSet.getInt("FILM_ID"))
                .name(resultSet.getString("FILM_NAME"))
                .description(resultSet.getString("DESCRIPTION"))
                .releaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate())
                .duration(resultSet.getLong("DURATION"))
                .mpa(mpa)
                .genres(genre)
                .build();
    }

    private Integer findGenreId(ResultSet resultSet, int a) throws SQLException {
        return resultSet.getInt("GENRE_ID");
    }
}
