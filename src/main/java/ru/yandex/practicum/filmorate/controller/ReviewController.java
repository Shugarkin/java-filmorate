package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/reviews")
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public List<Review> getAllReviewsByFilm(@RequestParam(required = false) Integer filmId,
                                            @RequestParam(defaultValue = "10") @Positive Integer count) {
        if (filmId == null) {
            return reviewService.getAllReviews(count);
        }
        return reviewService.getAllReviewsByFilm(filmId, count);
    }

    @PostMapping
    public Review createReview(@Valid @RequestBody Review review) {
        log.debug("Review film ID = {} was added", review.getFilmId());
        return reviewService.createReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
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
    public String deleteReviewById(@PathVariable("id") Integer id) {
        reviewService.deleteReviewById(id);
        return String.format("Review ID = {} was deleted", id);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable("id") int id) {
        return reviewService.getReviewById(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public String deleteLikeReview(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        reviewService.deleteLikeReview(id, userId);
        return "Like was delete";
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public String deleteDislikeReview(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        reviewService.deleteDislikeReview(id, userId);
        return "Dislike was delete";
    }
}


