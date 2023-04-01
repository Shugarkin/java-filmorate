package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserIsNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.*;

@Service
@Slf4j
public class UserService {

    private InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public User userAddFriend(int userId, int friendId) { //метод добавления в друзья
        Map<Integer, User> users = inMemoryUserStorage.getUsers();
        if (users.containsKey(userId) && users.containsKey(friendId)) {
            users.get(userId).getFriendVault().add(friendId);
            users.get(friendId).getFriendVault().add(userId);
            User user = users.get(userId);
            log.info("Пользователь с id {} добавил пользователя с id {} в друзья. ", userId, friendId);
            return user;
        }
        throw new UserIsNotFoundException("Пользователь с данным id не найден.");
    }

    public User userDeleteFriend(int userId, int friendId) { //метод удаления из друзей
        Map<Integer, User> users = inMemoryUserStorage.getUsers();
        if (users.containsKey(userId) && users.containsKey(friendId)) {
            User user = users.get(userId);
            if (!user.getFriendVault().contains(friendId)) {
                return user;
            }
            users.get(userId).getFriendVault().remove(friendId);
            users.get(friendId).getFriendVault().remove(userId);
            log.info("Пользователь с id {} удалил из друзей пользователя с id {}. ", userId, friendId);
            return user;
        }
        throw new UserIsNotFoundException("Пользователь с данным id не найден.");
    }

    public List<User> getListFriend(int userId, int friendId) { //метод получиния списка друзей
        Map<Integer, User> users = inMemoryUserStorage.getUsers();
        if (users.containsKey(userId) && users.containsKey(friendId)) {
            Set<Integer> listUser = new TreeSet<>(users.get(userId).getFriendVault());
            Set<Integer> listFriend = new TreeSet<>(users.get(friendId).getFriendVault());
            listUser.retainAll(listFriend);
            List<User> mutualFriends = new ArrayList<>();
            for (Integer key : users.keySet()) {
                if (listUser.contains(key)) {
                    mutualFriends.add(users.get(key));
                }
            }
            log.info("Показанны общие друзья пользователей с id {} и id {}. ", userId, friendId);
            return mutualFriends;
        }
        throw new UserIsNotFoundException("Пользователь с данным id не найден.");
    }
}
