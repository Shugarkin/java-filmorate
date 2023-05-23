package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;

@Service
@Slf4j
public class ReviewService {

    private final ReviewStorage reviewStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage) {
        this.reviewStorage = reviewStorage;
    }

    public Review createReview(Review review) {
        review.setUseful(0);
        reviewStorage.addReview(review);
        return review;
    }

    public List<Review> getAllReviewsByFilm(Integer filmId, Integer count) {
        return reviewStorage.getAllReviewsByFilm(filmId, count);
    }

    public List<Review> getAllReviews() {
        return reviewStorage.getAllReviews();
    }

    public Review updateReview(Review review) {
        reviewStorage.updateReview(review);
        return review;
    }

    public void deleteReviewById(Integer reviewId) {
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
