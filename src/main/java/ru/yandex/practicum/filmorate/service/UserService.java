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

    private FriendshipService friendshipService;

    @Autowired
    public UserService(UserDbStorage userDbStorage, FriendshipService friendshipService) {
        this.userDbStorage = userDbStorage;
        this.friendshipService = friendshipService;
    }

    public void userAddFriend(int userId, int friendId) { //метод добавления в друзья
        if (userDbStorage.getUserForId(userId) == null || userDbStorage.getUserForId(friendId) == null) {
            throw new UserIsNotFoundException("Пользователя такого нету((");
        }
        friendshipService.addFriend(userId, friendId);
    }

    public void userDeleteFriend(int userId, int friendId) { //метод удаления из друзей
        friendshipService.deleteFriend(userId, friendId);
    }

    public List<User> getListFriend(int userId, int friendId) { //метод получения списка друзей
        return friendshipService.getListFriend(userId, friendId);
    }

    public List<User> getAllUsers() {
        return userDbStorage.getAllUsers();
    }

    public User createUser(User user) {
        checkName(user);
        return userDbStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userDbStorage.updateUser(user);
    }

    public User getUserForId(int id) {
        return userDbStorage.getUserForId(id);
    }

    public List<User> getFriendsUserForId(Integer id) {
        userDbStorage.getUserForId(id); // проверка на существование пользователя
        return friendshipService.getFriendsUserForId(id);
    }

    private void checkName(User user) {
        if (user.getName().isBlank() || user.getName() == null) {
            user.setName(user.getLogin());
        }
    }

    public void userDeleteById(int userId) { //метод удаления пользователя по id
        if (userDbStorage.getUserForId(userId) == null) {
            throw new UserIsNotFoundException("Пользователя такого нету((");
        }
        userDbStorage.deleteUserById(userId);
    }

}
