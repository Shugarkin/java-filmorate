package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserIsNotFoundException;
import ru.yandex.practicum.filmorate.storage.RecommendationsStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class RecommendationsDbStorage implements RecommendationsStorage {

    private final JdbcTemplate jdbcTemplate;

    public RecommendationsDbStorage(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Integer> getRecommendation(int userId) {
        List<Integer> list;
        try {
            list = getListRecommendation(userId);
        } catch (RuntimeException e) {
            throw new UserIsNotFoundException("У данного пользователя нет рекомендаций.");
        }
        return list;
    }

    private List<Integer> getListRecommendation(int userId) {
        String sql = "SELECT FILM_ID " + //нужны айди фильмов
                "FROM LIKE_VAULT " + //из хранилища лайков
                "WHERE USER_ID in " + //у которых юзер их числа тех
                "(SELECT user_id " + //юзеров
                "FROM LIKE_VAULT " + //из хранилища лайков
                "WHERE FILM_ID in " + //у которых фильмы
                "(SELECT film_id " + //вот эти
                "FROM LIKE_VAULT " + //да-да
                "WHERE USER_ID  = ?) AND NOT USER_ID = ? " + //такие же как у юзера которого передали, но без учета его самого
                "GROUP BY USER_ID " + //группировать
                "ORDER BY count(USER_ID) DESC " + //сортировать по количеству айди юзеров, ибо чем больше количество тем больше совпадений с получателем рекомендации
                "LIMIT 1)" + //нам нужен только один юзер
                " AND not FILM_ID IN (SELECT FILM_ID FROM LIKE_VAULT WHERE USER_ID = ?)"; //и убираем из результатов те фильмы, что были у пользователя,
                                                                                          // чтобы получить рекомендационные
        return jdbcTemplate.query(sql, this::findRecommendation, userId, userId, userId);
    }

    private Integer findRecommendation(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("FILM_ID");
    }


}
