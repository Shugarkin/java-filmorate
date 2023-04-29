package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserIsNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("UserDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Optional<User>> getAllUsers() {
            String sqlQuery = "select * from USERS";
            return jdbcTemplate.query(sqlQuery, this::findUserById);
    }

    @Override
    public Optional<User> createUser(User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
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
        return Optional.of(user);
    }

    @Override
    public Optional<User> updateUser(User user) {
        if (getUserForId(user.getId()) == null)  {
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
        return Optional.of(user);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        if (getUserForId(userId) == null || getUserForId(friendId) == null) {
            throw new UserIsNotFoundException("Пользователя такого нету((");
        }
        String sqlQuery = "insert into USER_FRIEND (USER_ID, FRIEND_ID) " +
                "values (?, ?) ";
        jdbcTemplate.update(sqlQuery,userId, friendId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        String sqlQuery = "delete from USER_FRIEND where USER_ID = ? and FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public Optional<User> getUserForId(int id) {
        String sqlQuery = "select USER_ID, EMAIL, LOGIN, BIRTHDAY, USERNAME " +
                "from USERS where USER_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::findUserById, id);
    }

    @Override
    public List<Optional<User>> getFriendsUserForId(Integer id) {
        String friend = "select FRIEND_ID from USER_FRIEND where USER_ID = ?";
        List<String> intList = jdbcTemplate.query(friend,this::getFriend, id);

        List<Optional<User>> list = intList.stream()
                .map(s -> Integer.parseInt(s))
                .map(this::getUserForId)
                .collect(Collectors.toList());

        return list;
    }

    @Override
    public List<Optional<User>> getListFriend(int userId, int friendId) {
        String sqlQueryUser = "select FRIEND_ID from USER_FRIEND " + "where USER_ID = ?";
        List<String> userList = jdbcTemplate.query(sqlQueryUser, this::getFriend, userId);

        String sqlQueryFriend = "select FRIEND_ID from USER_FRIEND " + "where USER_ID = ?";
        List<String> friendList = jdbcTemplate.query(sqlQueryFriend, this::getFriend, friendId);

        List<Optional<User>> list = userList.stream()
                .filter(friendList::contains)
                .map(s -> Integer.parseInt(s))
                .map(this::getUserForId)
                .collect(Collectors.toList());
        return list;
    }

    private Optional<User> findUserById(ResultSet resultSet, int rowNum) throws SQLException {
        return Optional.of(User.builder()
                .id(resultSet.getInt("USER_ID"))
                .email(resultSet.getString("EMAIL"))
                .login(resultSet.getString("LOGIN"))
                .birthday(resultSet.getDate("BIRTHDAY").toLocalDate())
                .name(resultSet.getString("USERNAME"))
                .build());
    }

    private String getFriend(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getString("FRIEND_ID");
    }
}
