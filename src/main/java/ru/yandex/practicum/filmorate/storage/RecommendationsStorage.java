package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface RecommendationsStorage {
    List<Integer> getRecommendation(int userId);
}
