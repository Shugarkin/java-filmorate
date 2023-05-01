package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserApplicationTests {

    @Autowired
    private final UserStorage userStorage;

    private final FriendshipStorage friendshipStorage;

    @Test
    public void createUsers() {
        User newUser1 = userStorage.createUser(User.builder() //создание пользователя
                .name("dolore")
                .login("dolore")
                .email("msaddaail@mail.ru")
                .birthday(LocalDate.of(1999, 01, 20))
                .build());
        Assertions.assertNotNull(newUser1);
    }

    @Test
    public void getUser() {
        User newUser = userStorage.createUser(User.builder() //создание пользователя
                .name("dolore")
                .login("ne12w dosdqdqlore")
                .email("nedsadsadwmail@mail.ru")
                .birthday(LocalDate.of(1999, 01, 20))
                .build());
        Assertions.assertNotNull(newUser);

        User userOptional = userStorage.getUserForId(8); //получение по id
        Assertions.assertEquals(userOptional, newUser);
    }

    @Test
    public void getUserList() {
        User newUser = userStorage.createUser(User.builder() //создание пользователя
                .name("dolowqewewqedsadre")
                .login("nerrrrore")
                .email("sdasdamail@mail.ru")
                .birthday(LocalDate.of(1999, 01, 20))
                .build());
        Assertions.assertNotNull(newUser);

        List<User> userList = userStorage.getAllUsers(); //получение списка пользователей
        Assertions.assertNotNull(userList);

    }

    @Test
    public void updeteUser() {
        User newUser = userStorage.createUser(User.builder() //создание пользователя
                .name("dolore")
                .login("nfffffffew dolore")
                .email("newmailfffffff@mail.ru")
                .birthday(LocalDate.of(1999, 01, 20))
                .build());
        Assertions.assertNotNull(newUser);

        User userUpdate = userStorage.updateUser(User.builder() //изменение пользователя
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
        User newUser = userStorage.createUser(User.builder() //создание пользователя
                .name("dolore")
                .login("new dolore")
                .email("newmail@mail.ru")
                .birthday(LocalDate.of(1999, 01, 20))
                .build());
        Assertions.assertNotNull(newUser);

        User userFriend = userStorage.createUser(User.builder() //пользователь 2
                .name("dolore Friend")
                .login("dolornd")
                .email("mlsfasa@mail.ru")
                .birthday(LocalDate.of(1999, 01,21))
                .build());

        friendshipStorage.addFriend(1, 2); //добавление в друзья
        List<User> friendList = friendshipStorage.getFriendsUserForId(1); //получение друзей пользователя по id
        Assertions.assertNotNull(friendList);
    }

    @Test
    public void deleteFriend() {
        User newUser = userStorage.createUser(User.builder() //создание пользователя
                .name("dolore")
                .login("nesdaqweww dolore")
                .email("neuygfvcwmail@mail.ru")
                .birthday(LocalDate.of(1999, 01, 20))
                .build());
        Assertions.assertNotNull(newUser);

        User userFriend = userStorage.createUser(User.builder() //пользователь 2
                .name("dolore Friend")
                .login("dd")
                .email("m@mail.ru")
                .birthday(LocalDate.of(1999, 01,21))
                .build());

        friendshipStorage.deleteFriend(1, 2);
        List<User> friendList = friendshipStorage.getFriendsUserForId(1);
        Assertions.assertNotEquals(List.of(userFriend), friendList);
    }

    @Test
    public void listCommonFriend() {
        User newUser = userStorage.createUser(User.builder() //создание пользователя
                .name("dolore")
                .login("new dolore")
                .email("newmail@mail.ru")
                .birthday(LocalDate.of(1999, 01, 20))
                .build());
        Assertions.assertNotNull(newUser);

        User userFriend2 = userStorage.createUser(User.builder() //пользователь 3
                .name("dolore Friend")
                .login("dqwerolore Friend")
                .email("qwertmail@mail.ru")
                .birthday(LocalDate.of(1999, 01,21))
                .build());

        User userFriend = userStorage.createUser(User.builder() //пользователь 2
                .name("dolore Friend")
                .login("dolore Friend")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1999, 01,21))
                .build());

        friendshipStorage.addFriend(1, 2); //добавлив друзья 2го к 1му
        friendshipStorage.addFriend(3, 2); //добавлив друзья 2го к 3му

        List<User> listFriend = friendshipStorage.getListFriend(1, 3); //проверяем что 2й в листе друзей у 1го и 3го

        Assertions.assertEquals(listFriend, List.of(userFriend2));
    }
}
