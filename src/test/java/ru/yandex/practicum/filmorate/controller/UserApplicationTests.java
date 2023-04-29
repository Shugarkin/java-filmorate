package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
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

    @Test
    public void contexTest() {
        Assertions.assertThat(userStorage).isNotNull();
    }
    @Test
    public void users() {
        Optional<User> newUser = userStorage.createUser(User.builder() //создание пользователя
                .name("dolore")
                .login("dolore")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1999, 01,20))
                .build());
        Assertions.assertThat(newUser)
                .isPresent()
                .hasValueSatisfying(user ->
                        Assertions.assertThat(user).hasFieldOrPropertyWithValue("id", 1));

        Optional<User> userOptional = userStorage.getUserForId(1); //получение по id

        Assertions.assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        Assertions.assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );

        List<Optional<User>> userList = userStorage.getAllUsers(); //получение списка пользователей

        for (Optional<User> userOptional1 : userList) {
            Assertions.assertThat(userOptional1)
                    .isPresent()
                    .hasValueSatisfying(user ->
                            Assertions.assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                    );
        }

        Optional<User> userUpdate = userStorage.updateUser(User.builder() //изменение пользователя
                .id(1)
                .name("dolore")
                .login("new")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1999, 01,20))
                .build());

        Assertions.assertThat(userUpdate)
                .isPresent()
                .hasValueSatisfying(user ->
                        Assertions.assertThat(user).hasFieldOrPropertyWithValue("login", "new")
                );

        Optional<User> userFriend = userStorage.createUser(User.builder() //пользователь 2
                .name("dolore Friend")
                .login("dolore Friend")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1999, 01,21))
                .build());

        userStorage.addFriend(1, 2); //добавление в друзья

        List<Optional<User>> friendList = userStorage.getFriendsUserForId(1); //получение друзей пользователя по id

        for (Optional<User> userOptional1 : friendList) {
            Assertions.assertThat(userOptional1)
                    .isPresent()
                    .hasValueSatisfying(user ->
                            Assertions.assertThat(user).hasFieldOrPropertyWithValue("id", 2)
                    );
        }

        userStorage.deleteFriend(1, 2); //удалили из друзей

        List<Optional<User>> friendList1 = userStorage.getFriendsUserForId(1); //проверили что список друзей пуст

        for (Optional<User> userOptional2 : friendList1) {
            Assertions.assertThat(userOptional2)
                    .isPresent()
                    .hasValueSatisfying(user1 ->
                            Assertions.assertThat(user1).hasFieldOrPropertyWithValue("id", null)
                    );
        }

        Optional<User> userFriend2 = userStorage.createUser(User.builder() //пользователь 3
                .name("dolore Friend")
                .login("dolore Friend")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1999, 01,21))
                .build());

        userStorage.addFriend(1, 2); //добавлив друзья 2го к 1му
        userStorage.addFriend(3, 2); //добавлив друзья 2го к 3му

        List<Optional<User>> listFriend = userStorage.getListFriend(1, 3); //проверяем что 2й в листе друзей у 1го и 3го

        for (Optional<User> userOptional2 : listFriend) {
            Assertions.assertThat(userOptional2)
                    .isPresent()
                    .hasValueSatisfying(user1 ->
                            Assertions.assertThat(user1).hasFieldOrPropertyWithValue("id", 2)
                    );
        }
    }
} 