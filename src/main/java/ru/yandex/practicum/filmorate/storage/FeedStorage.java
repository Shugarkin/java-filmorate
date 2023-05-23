package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;

import java.util.List;

public interface FeedStorage {
    void addFeed(Integer userId, Integer entityId, EventType eventType, Operation operation);

    List<Feed> getFeed(Integer id);
}
