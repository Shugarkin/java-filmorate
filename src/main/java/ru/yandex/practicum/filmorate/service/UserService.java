package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.UserIsNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@Slf4j
public class UserService {

    private UserStorage userDbStorage;

    @Autowired
    public UserService(UserDbStorage userDbStorage) {
        this.userDbStorage = userDbStorage;

    }

    public void userAddFriend(int userId, int friendId) { //метод добавления в друзья
        userDbStorage.addFriend(userId, friendId);
    }

    public void userDeleteFriend(int userId, int friendId) { //метод удаления из друзей
        userDbStorage.deleteFriend(userId, friendId);
    }

    public List<Optional<User>> getListFriend(int userId, int friendId) { //метод получения списка друзей
        return userDbStorage.getListFriend(userId, friendId);
    }

    public List<Optional<User>> getAllUsers() {
        return userDbStorage.getAllUsers();
    }

    public Optional<User> createUser(User user) {
        return userDbStorage.createUser(user);
    }

    public Optional<User> updateUser(User user) {
        return userDbStorage.updateUser(user);
    }

    public User getUserForId(int id) {
        return userDbStorage.getUserForId(id).orElseThrow(() -> new UserIsNotFoundException("При получении id пришел null"));
    }

    public List<Optional<User>> getFriendsUserForId(Integer id) {
        return userDbStorage.getFriendsUserForId(id);
    }

}
