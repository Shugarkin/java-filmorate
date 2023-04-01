package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class UserValidatorTest {

    private Validator validator;
    private UserController userController;

    private InMemoryUserStorage inMemoryUserStorage;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        inMemoryUserStorage = new InMemoryUserStorage();
        userService = new UserService(inMemoryUserStorage);
        userController = new UserController(inMemoryUserStorage, userService);
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

        User user7 = User.builder() //проверка отсутствия имени
                .email("test@user.ru")
                .login("testUser")
                .birthday(LocalDate.of(1993, 03, 25))
                .build();
        userController.createUser(user7);
        Assertions.assertEquals(user7.getName(), user7.getLogin());

        userController.getUserForId(1); // проверка на геттер
        Assertions.assertEquals(user7, userController.getUserForId(1));

        User user8 = User.builder()
                .email("test@user.ru")
                .login("testUser")
                .name("TestUser")
                .birthday(LocalDate.of(1993, 03, 25))
                .build();
        userController.createUser(user8);

        userController.userAddFriend(1, 2);
        boolean answer = user7.getFriendVault().isEmpty();
        Assertions.assertEquals(answer, false); //проверка на добавление в друзья

        userController.userDeleteFriend(1,2);
        boolean answer1 = user7.getFriendVault().isEmpty();
        Assertions.assertEquals(answer1, true); //проверка на удаление из друзей

        userController.userAddFriend(1, 2);
        boolean answer2 = userController.getListFriend(1, 2).isEmpty();
        Assertions.assertEquals(answer2, true); //проверка на общих друзей

        List<User> list = userController.getFriendsUserForId(1);
        Assertions.assertEquals(list, List.of(user8)); //проверка на получение друзей пользователя

    }
}
