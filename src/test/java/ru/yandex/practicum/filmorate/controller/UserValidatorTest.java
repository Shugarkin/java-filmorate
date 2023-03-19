package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

public class UserValidatorTest {

    private Validator validator;
    private UserController userController;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        userController = new UserController();
    }

    @Test
    public void userValidationTest() {
        User user = User.builder() //должен пройти проверку
                .email("test@user.ru")
                .login("testUser")
                .name("testUser")
                .birthday(LocalDate.of(1993, 03, 25))
                .build();


        Set<ConstraintViolation<User>> violation = validator.validate(user);
        Assertions.assertTrue(violation.isEmpty());

        User user1 = User.builder() //неверный email
                .email("testuser.ru")
                .login("testUser")
                .name("testUser")
                .birthday(LocalDate.of(1993, 03, 25))
                .build();

        Set<ConstraintViolation<User>> violation1 = validator.validate(user1);
        Assertions.assertFalse(violation1.isEmpty());

        User user2 = User.builder() //отсутствие email-а
                .login("testUser")
                .name("testUser")
                .birthday(LocalDate.of(1993, 03, 25))
                .build();

        Set<ConstraintViolation<User>> violation2 = validator.validate(user2);
        Assertions.assertFalse(violation2.isEmpty());

        User user3 = User.builder() //логин с пробелом
                .email("test@user.ru")
                .login("test User")
                .name("testUser")
                .birthday(LocalDate.of(1993, 03, 25))
                .build();

        Set<ConstraintViolation<User>> violation3 = validator.validate(user3);
        Assertions.assertFalse(violation3.isEmpty());

        User user4 = User.builder() //отсутствие логина
                .email("test@user.ru")
                .name("testUser")
                .birthday(LocalDate.of(1993, 03, 25))
                .build();

        Set<ConstraintViolation<User>> violation4 = validator.validate(user4);
        Assertions.assertFalse(violation4.isEmpty());

        User user5 = User.builder() //отсутствие даты рождения
                .login("testUser")
                .email("test@user.ru")
                .name("testUser")
                .build();

        Set<ConstraintViolation<User>> violation5 = validator.validate(user5);
        Assertions.assertFalse(violation5.isEmpty());

        User user6 = User.builder() //дата рождения в будущем
                .login("testUser")
                .email("test@user.ru")
                .name("testUser")
                .birthday(LocalDate.of(2077, 03, 25))
                .build();

        Set<ConstraintViolation<User>> violation6 = validator.validate(user6);
        Assertions.assertFalse(violation6.isEmpty());

        User user7 = User.builder()
                .email("test@user.ru")
                .login("testUser")
                .birthday(LocalDate.of(1993, 03, 25))
                .build();
        userController.createUser(user7);
        Assertions.assertEquals(user1.getName(), user1.getLogin());
    }
}
