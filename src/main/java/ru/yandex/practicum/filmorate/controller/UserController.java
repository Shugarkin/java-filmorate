package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private LocalDate dateNow = LocalDate.now();
    private int userNextId = 1;
    private Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Коллекция пользователей получена, текущее количество {}", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if(user.getLogin().contains(" ")) {
            log.info("Попытка добавить пользователя с пустым login или с пробелами");
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if(user.getBirthday().isAfter(dateNow)) {
            log.info("Попытка добавить пользователя датой рождения в будущем");
            throw new ValidationException("Марти Макфлай ты ли это?");
        }
        if(user.getName() == null || user.getName().isBlank() || user.getName().isEmpty()) {
            log.info("Имя пользователя не было указанно, по этому использован его логин");
            user.setName(user.getLogin());
        }
        log.info("Новый пользователь добавлен");
        user.setId(userNextId);
        users.put(userNextId++, user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if(user.getName() == null || user.getName().isBlank()) {
            log.info("Имя пользователя не было указанно, по этому использован его логин");
            user.setName(user.getLogin());
        }
        if(user.getLogin().contains(" ")) {
            log.info("Попытка добавить пользователя с пустым login или с пробелами");
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if(user.getId() < 0) {
            log.info("Попытка добавить пользователя с id меньше нуля");
            throw new ValidationException("id не может быть меньше 0");
        }
        if(user.getBirthday().isAfter(dateNow)) {
            log.info("Попытка добавить пользователя датой рождения в будущем");
            throw new ValidationException("Марти Макфлай ты ли это?");
        }
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
}
