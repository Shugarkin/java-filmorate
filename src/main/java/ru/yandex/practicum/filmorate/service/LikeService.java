package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.List;

@Service
public class LikeService {

    private LikeStorage likeStorage;

    private FeedService feedService;

    @Autowired
    public LikeService(LikeStorage likeStorage, FeedService feedService) {
        this.likeStorage = likeStorage;
        this.feedService = feedService;
    }

    public void deleteLike(Integer filmId, Integer userId) {
        likeStorage.deleteLike(filmId, userId);
        feedService.addFeed(userId, filmId, EventType.LIKE, Operation.REMOVE);
    }

    public void addLike(Integer filmId, Integer userId) {
        likeStorage.addLike(filmId, userId);
        feedService.addFeed(userId, filmId, EventType.LIKE, Operation.ADD);
    }

    public List<Film> getPopularFilms(Integer end, Integer genreId, Integer year) {
        return likeStorage.getPopularFilms(end, genreId, year);
    }

}
