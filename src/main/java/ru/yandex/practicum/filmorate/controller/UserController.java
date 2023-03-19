package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private int userNextId = 1;
    private Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Коллекция пользователей получена, текущее количество {}", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        userCheck(user);
        log.info("Новый пользователь добавлен");
        user.setId(userNextId);
        users.put(userNextId++, user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        userCheck(user);
        for (Integer integer : users.keySet()) {
            if(integer == user.getId()) {
                log.info("Пользователь с id={} успешно заменен", user.getId());
                users.put(user.getId(), user);
                return user;
            }
        }
        log.info("Попытка изменить пользователя по не существующему id");
        throw new ValidationException("Пользователя с данным id нет");
    }

    private void userCheck(User user) {
        if(user.getId() < 0) {
            log.info("Попытка добавить пользователя с id меньше нуля");
            throw new ValidationException("id не может быть меньше 0");
        }
        if(user.getName() == null || user.getName().isBlank() || user.getName().isEmpty()) {
            log.info("Имя пользователя не было указанно, по этому использован его логин");
            user.setName(user.getLogin());
        }
    }
}