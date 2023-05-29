package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    Review addReview(Review review);

    Review updateReview(Review review);

    void deleteReviewById(int reviewId);

    Review getReviewById(int reviewId);

    Review addLikeReview(Integer reviewId, Integer userId);

    Review deleteLikeReview(Integer reviewId, Integer userId);

    Review deleteDislikeReview(Integer reviewId, Integer userId);

    List<Review> getAllReviewsByFilm(int filmId, int count);

    Review addDislikeReview(Integer reviewId, Integer userId);

    List<Review> getAllReviews(Integer count);

    Integer getUser(Integer idReview);

    Integer getFilm(Integer idReview);
}
