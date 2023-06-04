package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmIsNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    int count = 1;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review addReview(Review review) {

        String sqlQuery = "insert into review (content_review, is_Positive, user_id, film_id, useful)"
                + " values(?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getUseful());
        review.setReviewId(count);
        count++;
        return review;
    }

    @Override
    public List<Review> getAllReviewsByFilm(int filmId, int count) {
        filmExistsById(filmId);
        String sqlQuery = "SELECT *\n" +
                "FROM review\n" +
                "WHERE film_id = ?\n" +
                "ORDER BY useful DESC\n" +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::findReview, filmId, count);
    }

    @Override
    public List<Review> getAllReviews(Integer count) {
        String sqlQuery = "SELECT *\n" +
                "FROM review\n" +
                "ORDER BY useful DESC\n" +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::findReview, count);
    }

    @Override
    public Review updateReview(Review review) {
        reviewExistsById(review.getReviewId());
        String sqlQuery = "update review set " +
                "content_review = ?, is_Positive = ?" +
                "where reviewId = ?";

        jdbcTemplate.update(sqlQuery,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
        return review;
    }

    @Override
    public void deleteReviewById(int reviewId) {
        jdbcTemplate.update("DELETE FROM review WHERE reviewId = ?", reviewId);
    }

    @Override
    public Review getReviewById(int reviewId) {
        reviewExistsById(reviewId);
        String sqlQuery = "SELECT *\n" +
                "FROM review\n" +
                "WHERE reviewId=?";
        return jdbcTemplate.query(sqlQuery, this::findReview, reviewId).iterator().next();
    }

    private void filmExistsById(Integer filmId) {
        SqlRowSet filmIdRows = jdbcTemplate.queryForRowSet("SELECT * FROM FILMS " +
                "WHERE FILM_ID = ?", filmId);
        if (!filmIdRows.next()) {
            throw new FilmIsNotFoundException(String.format("Фильма с ID = %d не существует. Проверьте ID.", filmId));
        }
    }

    private void reviewExistsById(Integer reviewId) {
        SqlRowSet reviewIdRows = jdbcTemplate.queryForRowSet("SELECT * FROM review " +
                "WHERE reviewId = ?", reviewId);
        if (!reviewIdRows.next()) {
            throw new FilmIsNotFoundException(String.format("Отзыва с ID = %d не существует. Проверьте ID.", reviewId));
        }
    }

    private void userExistsById(Integer userId) {
        SqlRowSet userIdRows = jdbcTemplate.queryForRowSet("SELECT * FROM USERS " +
                "WHERE USER_ID = ?", userId);
        if (!userIdRows.next()) {
            throw new FilmIsNotFoundException(String.format("Пользователя с ID = %d не существует. Проверьте ID.", userId));
        }
    }

    @Override
    public Review addLikeReview(Integer reviewId, Integer userId) {
        reviewExistsById(reviewId);
        userExistsById(userId);
        String sqlQuery = "INSERT INTO REVIEW_LIKES (user_id, review_id, useful) VALUES (?, ?, 1);" +
                " update review r set useful = (select sum(l.useful) from REVIEW_LIKES l where l.review_id = r.reviewId)  where reviewId = ?;";
        jdbcTemplate.update(sqlQuery,userId, reviewId, reviewId);
        return getReviewById(reviewId);
    }

    @Override
    public Review addDislikeReview(Integer reviewId, Integer userId) {
        reviewExistsById(reviewId);
        userExistsById(userId);
        String sqlQuery = "INSERT INTO REVIEW_LIKES (user_id, review_id, useful) VALUES (?, ?, -1);" +
                " update review r set useful = (select sum(l.useful) from REVIEW_LIKES l where l.review_id = r.reviewId)  where reviewId = ?;";
        jdbcTemplate.update(sqlQuery, userId, reviewId, reviewId);
        return getReviewById(reviewId);
    }

    @Override
    public Review deleteLikeReview(Integer reviewId, Integer userId) {
        reviewExistsById(reviewId);
        jdbcTemplate.update("DELETE FROM REVIEW_LIKES WHERE review_id = ? AND user_id = ?;\n" +
                        " update review r set useful = (select sum(l.useful) from REVIEW_LIKES l where l.review_id = r.reviewId)  where reviewId = ?;",
                reviewId, userId, reviewId);
        return getReviewById(reviewId);
    }

    @Override
    public Review deleteDislikeReview(Integer reviewId, Integer userId) {
        reviewExistsById(reviewId);
        jdbcTemplate.update("DELETE FROM REVIEW_LIKES WHERE review_id = ? AND user_id = ?;\n" +
                " update review r set useful = (select sum(l.useful) from REVIEW_LIKES l where l.review_id = r.reviewId)  where reviewId = ?;",
                reviewId, userId, reviewId);
        return getReviewById(reviewId);
    }

    private Review findReview(ResultSet resultSet, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getInt("reviewId"))
                .content(resultSet.getString("content_review"))
                .isPositive(resultSet.getBoolean("is_Positive"))
                .userId(resultSet.getInt("user_id"))
                .filmId(resultSet.getInt("film_id"))
                .useful(resultSet.getInt("useful"))
                .build();
    }
}
