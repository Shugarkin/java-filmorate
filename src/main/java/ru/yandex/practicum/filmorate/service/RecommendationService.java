package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.RecommendationsStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final RecommendationsStorage recommendationsStorage;

    public List<Integer> getRecommendation(Integer userId) {
        return recommendationsStorage.getRecommendation(userId);
    }
}
