package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

public interface ReviewStorage {

    void addReview(Review review);

    void updateReview(Review review);

    void deleteReview(int reviewId);

    void getReview(int reviewId);

    void getFilmReview(int filmId, int count);

}
