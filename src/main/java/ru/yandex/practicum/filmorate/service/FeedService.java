package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.util.List;

@Service
public class FeedService {

    private FeedStorage feedStorage;

    @Autowired
    public FeedService(FeedStorage feedStorage) {
        this.feedStorage = feedStorage;
    }

    public List<Feed> getFeed(Integer id) {
        return feedStorage.getFeed(id);
    }

    public void addFeed(Integer userId, Integer entityId, EventType eventType, Operation operation) {
        feedStorage.addFeed(userId, entityId, eventType, operation);
    }
}
