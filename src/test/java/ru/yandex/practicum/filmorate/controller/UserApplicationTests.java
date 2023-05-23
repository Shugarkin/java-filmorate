package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserApplicationTests {

    @Autowired
    private final UserService userService;

    @Test
    public void createUsers() {
        User newUser1 = userService.createUser(User.builder() //создание пользователя
                .name("dolore")
                .login("dolore")
                .email("msaddaail@mail.ru")
                .birthday(LocalDate.of(1999, 01, 20))
                .build());
        Assertions.assertNotNull(newUser1);
    }

    @Test
    public void getUser() {
        User newUser = userService.createUser(User.builder() //создание пользователя
                .name("dolore")
                .login("ne12w dosdqdqlore")
                .email("nedsadsadwmail@mail.ru")
                .birthday(LocalDate.of(1999, 01, 20))
                .build());
        Assertions.assertNotNull(newUser);

        User userOptional = userService.getUserForId(8); //получение по id
        Assertions.assertEquals(userOptional, newUser);
    }

    @Test
    public void getUserList() {
        User newUser = userService.createUser(User.builder() //создание пользователя
                .name("dolowqewewqedsadre")
                .login("nerrrrore")
                .email("sdasdamail@mail.ru")
                .birthday(LocalDate.of(1999, 01, 20))
                .build());
        Assertions.assertNotNull(newUser);

        List<User> userList = userService.getAllUsers(); //получение списка пользователей
        Assertions.assertNotNull(userList);

    }

    @Test
    public void updeteUser() {
        User newUser = userService.createUser(User.builder() //создание пользователя
                .name("dolore")
                .login("nfffffffew dolore")
                .email("newmailfffffff@mail.ru")
                .birthday(LocalDate.of(1999, 01, 20))
                .build());
        Assertions.assertNotNull(newUser);

        User userUpdate = userService.updateUser(User.builder() //изменение пользователя
                .id(1)
                .name("asdfg")
                .login("new")
                .email("asdfghj@mail.ru")
                .birthday(LocalDate.of(1999, 01,20))
                .build());

        Assertions.assertNotEquals(userUpdate, newUser);
    }

    @Test
    public void addFriendAndGetForID() {
        User newUser = userService.createUser(User.builder() //создание пользователя
                .name("dolore")
                .login("new dooooolore")
                .email("newmaaaaaaail@mail.ru")
                .birthday(LocalDate.of(1999, 01, 20))
                .build());
        Assertions.assertNotNull(newUser);

        User userFriend = userService.createUser(User.builder() //пользователь 2
                .name("dolore Friend")
                .login("dolornd")
                .email("mlsfffffffasa@mail.ru")
                .birthday(LocalDate.of(1999, 01,21))
                .build());

        userService.userAddFriend(1, 2); //добавление в друзья
        List<User> friendList = userService.getFriendsUserForId(1); //получение друзей пользователя по id
        Assertions.assertNotNull(friendList);
    }

    @Test
    public void deleteFriend() {
        User newUser = userService.createUser(User.builder() //создание пользователя
                .name("dolore")
                .login("nesdaqweww dolore")
                .email("neuygfvcwmail@mail.ru")
                .birthday(LocalDate.of(1999, 01, 20))
                .build());
        Assertions.assertNotNull(newUser);

        User userFriend = userService.createUser(User.builder() //пользователь 2
                .name("dolore Friend")
                .login("dd")
                .email("m@mail.ru")
                .birthday(LocalDate.of(1999, 01,21))
                .build());

        userService.userDeleteFriend(1, 2);
        List<User> friendList = userService.getFriendsUserForId(1);
        Assertions.assertNotEquals(List.of(userFriend), friendList);
    }

    @Test
    public void listCommonFriend() {
        User newUser = userService.createUser(User.builder() //создание пользователя
                .name("dolore")
                .login("new dolore")
                .email("newmail@mail.ru")
                .birthday(LocalDate.of(1999, 01, 20))
                .build());
        Assertions.assertNotNull(newUser);

        User userFriend2 = userService.createUser(User.builder() //пользователь 3
                .name("dolore Friend")
                .login("dqwerolore Friend")
                .email("qwertmail@mail.ru")
                .birthday(LocalDate.of(1999, 01,21))
                .build());

        User userFriend = userService.createUser(User.builder() //пользователь 2
                .name("dolore Friend")
                .login("dolore Friend")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1999, 01,21))
                .build());

        userService.userAddFriend(1, 2); //добавлив друзья 2го к 1му
        userService.userAddFriend(3, 2); //добавлив друзья 2го к 3му

        List<User> listFriend = userService.getListFriend(1, 3); //проверяем что 2й в листе друзей у 1го и 3го

        Assertions.assertEquals(listFriend, List.of(userFriend2));
    }

    @Test
    public void getFeed() {
       List<Feed> list = userService.getFeed(1);
       Assertions.assertNotNull(list);
    }
}
