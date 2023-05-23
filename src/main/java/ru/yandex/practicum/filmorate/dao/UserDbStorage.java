package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserIsNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAllUsers() {
        String sqlQuery = "select * from USERS";
        return jdbcTemplate.query(sqlQuery, this::findUserById);
    }

    @Override
    public User createUser(User user) {
        String sqlQuery = "insert into USERS ( EMAIL, LOGIN, BIRTHDAY, USERNAME) " +
                "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"USER_ID"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setDate(3, Date.valueOf(user.getBirthday()));
            stmt.setString(4, user.getName());

            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (getUserForId(user.getId()) == null) {
            throw new UserIsNotFoundException("Пользователя такого нету((");
        }
        String sqlQuery = "update USERS set " +
                "USERNAME = ?, LOGIN = ?, EMAIL = ?, BIRTHDAY = ?  " +
                "where USER_ID = ?";
        jdbcTemplate.update(sqlQuery,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public User getUserForId(int id) {
        String sqlQuery = "select USER_ID, EMAIL, LOGIN, BIRTHDAY, USERNAME " +
                "from USERS where USER_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::findUserById, id);
        } catch (RuntimeException e) {
            throw new UserIsNotFoundException("Пользователь не найден");
        }
    }

    @Override
    public void deleteUserById(int id) {
        String sqlQuery = "delete from USERS where USER_ID = ?";
        try {
            jdbcTemplate.update(sqlQuery, id);
        } catch (RuntimeException e) {
            throw new UserIsNotFoundException("Пользователь не найден");
        }
    }

    private User findUserById(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("USER_ID"))
                .email(resultSet.getString("EMAIL"))
                .login(resultSet.getString("LOGIN"))
                .birthday(resultSet.getDate("BIRTHDAY").toLocalDate())
                .name(resultSet.getString("USERNAME"))
                .build();
    }

}
