package ru.yandex.practicum.filmorate.storage;
import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserStorage {

    public List<User> getAllUsers();

    public User createUser(User user);

    public User updateUser(User user);

    public void addFriend(int userId, int friendId);

    public Map<Integer, User> getMapUsers();

    public void deleteFriend(int userId, int friendId);

    public Optional<User> getUserForId(int id);

    public List<User> getFriendsUserForId(Integer id);

}
