package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private int userNextId = 1;
    private Map<Integer, User> users = new HashMap<>();
    public List<User> getAllUsers() {
        log.info("Коллекция пользователей получена, текущее количество {}", users.size());
        return new ArrayList<>(users.values());
    }
    public User createUser(User user) {
        userCheck(user);
        log.info("Новый пользователь добавлен");
        user.setId(userNextId);
        users.put(userNextId++, user);
        return user;
    }

    public User updateUser(User user) {
        userCheck(user);
        userCheckId(user);
        if(users.containsKey(user.getId())) {
            log.info("Пользователь с id={} успешно заменен", user.getId());
            users.put(user.getId(), user);
            return user;
        }
        log.info("Попытка изменить пользователя по не существующему id");
        throw new ValidationException("Пользователя с данным id нет");
    }

    private void userCheck(User user) {
        if(user.getName() == null || user.getName().isBlank()) {
            log.info("Имя пользователя не было указанно, по этому использован его логин");
            user.setName(user.getLogin());
        }
    }
    private void userCheckId(User user) {
        if(user.getId() < 0) {
            log.info("Попытка добавить пользователя с id меньше нуля");
            throw new ValidationException("id не может быть меньше 0");
        }
    }
}
