package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@SpringBootTest
public class UserControllerTest {

    private UserController userController;

    @BeforeEach
    public void makeController() {
        userController = new UserController();
    }

    @Test
    public void userControllerTest() {
        boolean answer = userController.getAllUsers().isEmpty();
        Assertions.assertEquals(answer, true, "Список не пуст");

        User user = User.builder()
                .email("test@user.ru")
                .login("testUser")
                .name("testUser")
                .birthday(LocalDate.of(1993, 03, 25))
                .build();

        userController.createUser(user);
        Assertions.assertEquals(userController.getAllUsers().get(0), user);

        try {
            userController.createUser(User.builder()
                    .email("test@user.ru")
                    .login("test User")
                    .name("testUser")
                    .birthday(LocalDate.of(1993, 03, 25))
                    .build());
        } catch (ValidationException e) {
            Assertions.assertEquals(e.getMessage(), "Логин не может содержать пробелы");
        }

        try {
            userController.createUser(userController.createUser(User.builder()
                    .email("test@user.ru")
                    .login("testUser")
                    .name("testUser")
                    .birthday(LocalDate.of(2055, 12, 12))
                    .build()));
        } catch (ValidationException e) {
            Assertions.assertEquals(e.getMessage(), "Марти Макфлай ты ли это?");
        }

        User user1 = User.builder()
                .email("test@user.ru")
                .login("testUser")
                .birthday(LocalDate.of(1993, 03, 25))
                .build();
        userController.createUser(user1);
        Assertions.assertEquals(user1.getName(), user1.getLogin());


        User user2 = User.builder()
                .id(2)
                .email("test@user.ru")
                .login("testUser")
                .name("testUser")
                .birthday(LocalDate.of(2000,03,25))
                .build();

        userController.updateUser(user2);
        Assertions.assertEquals(userController.getAllUsers().get(1), user2);

        try {
            userController.createUser(User.builder()
                    .id(1)
                    .email("test@user.ru")
                    .login("test User")
                    .name("testUser")
                    .birthday(LocalDate.of(1993, 03, 25))
                    .build());
        } catch (ValidationException e) {
            Assertions.assertEquals(e.getMessage(), "Логин не может содержать пробелы");
        }

        try {
            userController.createUser(userController.createUser(User.builder()
                    .id(1)
                    .email("test@user.ru")
                    .login("testUser")
                    .name("testUser")
                    .birthday(LocalDate.of(2055, 12, 12))
                    .build()));
        } catch (ValidationException e) {
            Assertions.assertEquals(e.getMessage(), "Марти Макфлай ты ли это?");
        }

        User user3 = User.builder()
                .id(1)
                .email("test@user.ru")
                .login("testUser")
                .birthday(LocalDate.of(1993, 03, 25))
                .build();
        userController.createUser(user3);
        Assertions.assertEquals(user3.getName(), user3.getLogin());
        }
    }

