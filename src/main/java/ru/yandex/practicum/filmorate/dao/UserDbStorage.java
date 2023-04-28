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

    public UserDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }


    @Override
    public List<Optional<User>> getAllUsers() {
            String sqlQuery = "select USER_ID, EMAIL, LOGIN, BIRTHDAY, USERNAME, FRIENDSHIP  from USERS";
            return jdbcTemplate.query(sqlQuery, this::findUserById);
    }

    @Override
    public User createUser(User user) {
        if(user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if(user.getFriendship() == null) {
            user.setFriendship(" ");
        }
        String sqlQuery = "insert into USERS ( EMAIL, LOGIN, BIRTHDAY, USERNAME, FRIENDSHIP) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"USER_ID"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setDate(3, Date.valueOf(user.getBirthday()));
            stmt.setString(4, user.getName());
            stmt.setString(5, String.valueOf(user.getFriendship()));

            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if(getUserForId(user.getId()) == null)  {
            throw new UserIsNotFoundException("Пользователя такого нету((");
        }
        String sqlQuery = "update USERS set " +
                "USERNAME = ?, LOGIN = ?, EMAIL = ?, BIRTHDAY = ?, FRIENDSHIP = ?  " +
                "where USER_ID = ?";
        jdbcTemplate.update(sqlQuery,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                user.getFriendship(),
                user.getId());
        return user;
    }

    private String getFriend(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getString("FRIEND_ID");
    }

    @Override
    public Optional<User> addFriend(int userId, int friendId) {
        if(getUserForId(userId) == null || getUserForId(friendId) == null) {
            throw new UserIsNotFoundException("Пользователя такого нету((");
        }
        String sqlQuery = "insert into USER_FRIEND (USER_ID, FRIEND_ID) " +
                "values (?, ?) ";
        jdbcTemplate.update(sqlQuery,userId, friendId);

        String sqlFriends = "select FRIEND_ID from USER_FRIEND" + " where USER_ID = ?";
        List<String> list = jdbcTemplate.query(sqlFriends, this::getFriend, userId);

        String sqlFriendship = "update USERS set " + "FRIENDSHIP = ? " + " where USER_ID = ?";
        jdbcTemplate.update(sqlFriendship, String.join(",", list), userId);

        return getUserForId(userId);
    }



    @Override
    public User deleteFriend(int userId, int friendId) {
        String sqlQuery = "delete from USER_FRIEND where USER_ID = ? and FRIEND_ID = ?";

        User user = getUserForId(userId).get();
        List<String> list = Arrays.stream(user.getFriendship()
                        .split(","))
                        .collect(Collectors.toList());
        list.remove(String.valueOf(friendId));
        user.setFriendship(String.join(",", list));
        updateUser(user);
        jdbcTemplate.update(sqlQuery, userId, friendId);
        return user;
    }

    @Override
    public Optional<User> getUserForId(int id) {
            String sqlQuery = "select USER_ID, EMAIL, LOGIN, BIRTHDAY, USERNAME, FRIENDSHIP " +
                    "from USERS where USER_ID = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::findUserById, id);
    }

    private Optional<User> findUserById(ResultSet resultSet, int rowNum) throws SQLException {
        return Optional.of(User.builder()
                .id(resultSet.getInt("USER_ID"))
                .email(resultSet.getString("EMAIL"))
                .login(resultSet.getString("LOGIN"))
                .birthday(resultSet.getDate("BIRTHDAY").toLocalDate())
                .name(resultSet.getString("USERNAME"))
                .friendship(resultSet.getString("FRIENDSHIP"))
                .build());
    }

    @Override
    public List<Optional<User>> getFriendsUserForId(Integer id) {
        User user = getUserForId(id).get();

        if(user.getFriendship().isBlank()) {
            return List.of();
        }
        List<Integer> listInt = Arrays.asList(user.getFriendship()
                .split(","))
                .stream()
                .map(s -> Integer.parseInt(s))
                .collect(Collectors.toList());

        List<Optional<User>> list = new ArrayList<>();

        for (Integer integer : listInt) {
            list.add(getUserForId(integer));
        }
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

    @Override
    public Map<Integer, User> getMapUsers() {
        return null;
    }
}
