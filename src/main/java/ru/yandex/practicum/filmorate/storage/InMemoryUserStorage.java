package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserIsNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ValidationException;
import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private int userNextId = 1;

    private Map<Integer, User> users = new HashMap<>();

    public List<User> getAllUsers() {
        log.info("Коллекция пользователей получена, текущее количество {}.", users.size());
        return new ArrayList<>(users.values());
    }

    public User createUser(User user) {
        userCheck(user);
        log.info("Новый пользователь добавлен.");
        user.setId(userNextId);
        user.setFriendVault(new TreeSet<>());
        users.put(userNextId++, user);
        return user;
    }

    public User updateUser(User user) {
        userCheck(user);
        userCheckId(user);
        if (users.containsKey(user.getId())) {
            log.info("Пользователь с id={} успешно заменен", user.getId());
            user.setFriendVault(new TreeSet<>());
            users.put(user.getId(), user);
            return user;
        }
        log.info("Попытка изменить пользователя по не существующему id.");
        throw new ValidationException("Пользователя с данным id нет.");
    }

    public User getUserForId(int id) {
        if (users.containsKey(id)) {
            log.info("Пользователь получен по id.");
            return users.get(id);
        }
        throw new UserIsNotFoundException("Пользователь не найден.");
    }

    public List<User> getFriendsUserForId(Integer id) {
        if (!users.containsKey(id)) {
            throw new UserIsNotFoundException("Пользователь не найден.");
        }
        Set<Integer> userNumberList = users.get(id).getFriendVault();
        List<User> userList = new ArrayList<>();
        for (Integer integer : users.keySet()) {
            if (userNumberList.contains(integer)) {
                userList.add(users.get(integer));
            }
        }
        log.info("Получен список друзей пользователя.");
        return userList;
    }

    private void userCheck(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя пользователя не было указанно, по этому использован его логин.");
            user.setName(user.getLogin());
        }
    }

    private void userCheckId(User user) {
        if (user.getId() < 0) {
            log.info("Попытка добавить пользователя с id меньше нуля");
            throw new ValidationException("id не может быть меньше 0");
        }
    }

    public Map<Integer, User> getUsers() {
        return users;
    }
}
