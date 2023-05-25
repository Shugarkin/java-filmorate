package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.RecommendationsStorage;

import java.util.List;

@Service
@Slf4j
public class RecommendationService {

    private RecommendationsStorage recommendationsStorage;

    @Autowired
    public RecommendationService(RecommendationsStorage recommendationsStorage) {
        this.recommendationsStorage = recommendationsStorage;
    }

    public List<Integer> getRecommendation(Integer userId) {
        return recommendationsStorage.getRecommendation(userId);
    }
}
