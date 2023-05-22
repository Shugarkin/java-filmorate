package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public List<Review> getAllReviewsByFilm(@RequestParam(required = false) Integer filmId,
                                            @RequestParam(defaultValue = "10", required = false) Integer count) { //получение всех отсортированных отзывов
        if (count <= 0) {
            throw new IllegalArgumentException();
        }
        if (filmId == null) {
            return reviewService.getAllReviews();
        }
        return reviewService.getAllReviewsByFilm(filmId, count);
    }

    @PostMapping
    public Review createReview(@Valid @RequestBody Review review) { //создание ревью
        return reviewService.createReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) { //обновление ревью
        return reviewService.updateReview(review);
    }

    @PutMapping("/{id}/like/{userId}")
    public Review addLikeReview(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        return reviewService.addLikeReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public Review addDislikeReview(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        return reviewService.addDislikeReview(id, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReviewById(@PathVariable("id") Integer id) {
        reviewService.deleteReviewById(id);
        return ResponseEntity.ok("Отзыв удален");
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable("id") int id) {
        return reviewService.getReviewById(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Review> deleteLikeReview(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        reviewService.deleteLikeReview(id, userId);
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ResponseEntity<Review> deleteDislikeReview(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        reviewService.deleteDislikeReview(id, userId);
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }
}


