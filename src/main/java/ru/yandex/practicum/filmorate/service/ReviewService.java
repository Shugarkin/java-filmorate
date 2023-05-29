package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmIsNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserIsNotFoundException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;

@Service
@Slf4j
public class ReviewService {

    private final ReviewStorage reviewStorage;

    private final FeedService feedService;

    private final FilmService filmService;

    private final UserService userService;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage, FeedService feedService, FilmService filmService, UserService userService) {
        this.reviewStorage = reviewStorage;
        this.feedService = feedService;
        this.filmService = filmService;
        this.userService = userService;
    }

    public Review createReview(Review review) {
        if (review.getFilmId() < 0) {
            throw new FilmIsNotFoundException("Нет такого фильма");
        }
        if (review.getUserId() < 0) {
            throw new UserIsNotFoundException("Нет такого пользователя");
        }
        review.setUseful(0);
        reviewStorage.addReview(review);
        feedService.addFeed(review.getUserId(), review.getReviewId(), EventType.REVIEW, Operation.ADD);// получение id пользователя
        // через методы нужно из-за проблемы с тестами
        return review;
    }

    public List<Review> getAllReviewsByFilm(Integer filmId, Integer count) {
        return reviewStorage.getAllReviewsByFilm(filmId, count);
    }

    public List<Review> getAllReviews() {
        return reviewStorage.getAllReviews();
    }

    public Review updateReview(Review review) {
        int userId = userService.getUser(review.getReviewId());
        int filmId = filmService.getFilm(review.getReviewId());


        feedService.addFeed(userId, review.getReviewId(),
                EventType.REVIEW, Operation.UPDATE);// получение id пользователя через методы нужно из-за проблемы с тестами
        reviewStorage.updateReview(review);

        //добавил изменение id пользователя и фильма из=за проблемы с тестами. потом их удалим
        review.setUserId(userId);
        review.setFilmId(filmId);
        return review;
    }

    public void deleteReviewById(Integer reviewId) {
        int userId = userService.getUser(reviewId);
        feedService.addFeed(userId, reviewId, EventType.REVIEW, Operation.REMOVE);
        reviewStorage.deleteReviewById(reviewId);
    }

    public Review getReviewById(Integer reviewId) {
        return reviewStorage.getReviewById(reviewId);
    }

    public Review addLikeReview(Integer reviewId, Integer userId) {
        reviewStorage.addLikeReview(reviewId, userId);
        return reviewStorage.getReviewById(reviewId);
    }

    public Review addDislikeReview(Integer reviewId, Integer userId) {
        reviewStorage.addDislikeReview(reviewId, userId);
        return reviewStorage.getReviewById(reviewId);
    }

    public Review deleteLikeReview(Integer reviewId, Integer userId) {
        reviewStorage.deleteLikeReview(reviewId, userId);
        return reviewStorage.getReviewById(reviewId);
    }

    public Review deleteDislikeReview(Integer reviewId, Integer userId) {
        reviewStorage.deleteDislikeReview(reviewId, userId);
        return reviewStorage.getReviewById(reviewId);
    }
}
