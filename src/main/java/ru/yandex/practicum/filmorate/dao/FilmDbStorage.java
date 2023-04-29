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

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public List<Optional<Film>> getAllFilms() {
            String sqlQuery = "select FILMS.FILM_ID, FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID, GENRE_FILM.GENRE_ID " +
                    "from FILMS left join GENRE_FILM on FILMS.FILM_ID = GENRE_FILM.FILM_ID ";
            return jdbcTemplate.query(sqlQuery, this::findFilm);
    }

    @Override
    public Optional<Film> createFilms(Film film) {
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

        if(film.getGenres() != null) {
            addGenre(film);
        }

        String sqlGenreId = "select GENRE.GENRE_ID from GENRE_FILM" +
                " join GENRE on GENRE_FILM.GENRE_ID = GENRE.GENRE_ID " +
                " where FILM_ID = ?";
        List<Genre> genre = jdbcTemplate.query(sqlGenreId, this::findGenreId, film.getId())
                .stream()
                .map(s -> getGenre(s))
                .collect(Collectors.toList());
        film.setGenres(genre);
        return Optional.of(film);
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        if(getFilmForId(film.getId()) == null) {
            throw new FilmIsNotFoundException("Нет такого фильма");
        }

        if(film.getGenres() != null) {
            addGenre(film);
        }

        String sqlGenreId = "select GENRE_FILM.GENRE_ID from GENRE_FILM join GENRE on GENRE_FILM.GENRE_ID = GENRE.GENRE_ID" +
                " where FILM_ID = ?";
        List<Genre> genre = jdbcTemplate.query(sqlGenreId, this::findGenreId, film.getId())
                .stream()
                .map(s -> getGenre(s))
                .collect(Collectors.toList());;
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
        return Optional.of(film);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        String sqlLike = "delete from LIKE_VAULT where FILM_ID = ? and USER_ID = ?";
        jdbcTemplate.update(sqlLike, filmId, userId);
    }

    @Override
    public Optional<Film> getFilmForId(int id) {
            String sqlQuery = "select FILM_ID, FILM_NAME, " +
                    "DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID from FILMS where FILM_ID = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::findFilm, id);
    }

    @Override
    public List<Genre> getGenreList () {
        String genre = "select * from GENRE ";
        return jdbcTemplate.query(genre, this::findGenre);
    }

    @Override
    public Genre getGenre (int id) {
        String genre = "select * from GENRE where GENRE_ID = ?";
        return jdbcTemplate.queryForObject(genre, this::findGenre, id);
    }

    @Override
    public List<Mpa> getMpaList() {
        String sqlMpa = "select * from MPA ";
        return jdbcTemplate.query(sqlMpa,this::findMpa);
    }

    @Override
    public Mpa getMpaById(Integer id) {
        String sqlMpa = "select * from MPA where MPA_ID = ?";
        return jdbcTemplate.queryForObject(sqlMpa, this::findMpa, id);
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        String sqlLike = "insert into LIKE_VAULT (FILM_ID, USER_ID)"
                + " values(?,?)";
        jdbcTemplate.update(sqlLike, filmId, userId);
        getFilmForId(filmId);
    }

    @Override
    public List<Optional<Film>> getPopularFilms(Integer end) {
        String sqlFilms = "select FILMS.FILM_ID " +
                    "from LIKE_VAULT right JOIN FILMS ON LIKE_VAULT.FILM_ID = FILMS.FILM_ID " +
                    "GROUP BY FILMS.FILM_ID " +
                    "ORDER BY COUNT(USER_ID) " +
                    "DESC limit ?;";
        List<String> filmId = jdbcTemplate.query(sqlFilms, this::getFilmId, end);

        List<Optional<Film>> filmList = filmId.stream()
                .map(a -> Integer.parseInt(a))
                .map(s -> getFilmForId(s))
                .collect(Collectors.toList());

        return filmList;
    }

    private Integer findGenreId (ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("GENRE_ID");
    }

    private void addGenre(Film film) {
        String a = "delete from GENRE_FILM where FILM_ID = ?";
        jdbcTemplate.update(a, film.getId());


        String addGenre = "insert into GENRE_FILM (FILM_ID, GENRE_ID) values(?,?)";

        Set<Integer> genres = film.getGenres()
                .stream()
                .map(s -> s.getId())
                .collect(Collectors.toSet());
        for (Integer genre : genres) {
            jdbcTemplate.update(addGenre, film.getId(), genre);
        }
    }

    private Optional<Film> findFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = getMpaById(Integer.parseInt(resultSet.getString("MPA_ID")));

        int filmId = resultSet.getInt("FILM_ID");
        String sqlGenreId = "select GENRE.GENRE_ID from GENRE_FILM join GENRE on GENRE_FILM.GENRE_ID = GENRE.GENRE_ID" +
                " where FILM_ID = ?";
        List<Genre> genre = jdbcTemplate.query(sqlGenreId, this::findGenreId, filmId)
                .stream()
                .map(s -> getGenre(s))
                .collect(Collectors.toList());

        return Optional.of(Film.builder()
                .id(resultSet.getInt("FILM_ID"))
                .name(resultSet.getString("FILM_NAME"))
                .description(resultSet.getString("DESCRIPTION"))
                .releaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate())
                .duration(resultSet.getLong("DURATION"))
                .mpa(mpa)
                .genres(genre)
                .build());
    }

    private Genre findGenre (ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("GENRE_ID"))
                .name(resultSet.getString("GENRE_NAME"))
                .build();
    }

    private String getFilmId(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getString("FILM_ID");
    }

    private Mpa findMpa(ResultSet resultSet, int i) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("MPA_ID"))
                .name(resultSet.getString("MPA_NAME"))
                .build();
    }
}
