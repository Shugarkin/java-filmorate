package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.LikeExtractor;
import ru.yandex.practicum.filmorate.storage.RecommendationsStorage;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RecommendationsDbStorage implements RecommendationsStorage {

    private final JdbcTemplate jdbcTemplate;

    public RecommendationsDbStorage(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Integer> getRecommendation(int userId) {
        LikeExtractor likeExtractor = new LikeExtractor();
        String sql = "SELECT *  FROM like_vault";
        Map<Integer, List<Integer>> usersFilms = jdbcTemplate.query(sql, likeExtractor); //получаем все фильмы
        List<Integer> user = usersFilms.remove(userId);//удаляем и сразу же сохраняем в лист пользователя id которого пришло
        List<Integer> listOfUsersWithCommon = new ArrayList<>(); //лист для id пользователей у которых есть совпадения с нашим
        for (Map.Entry<Integer, List<Integer>> entry : usersFilms.entrySet()) { //здесь идет перебор
            for (Integer integer : entry.getValue()) { //каждый фильм остальных пользователей
                for (Integer integer1 : user) { //сверяется с нашим
                    if (integer1 == integer) {
                        listOfUsersWithCommon.add(entry.getKey()); //и id пользователя с совпадениями добавляется в лист
                    }
                }
            }
        }

        if (!listOfUsersWithCommon.isEmpty()) { //если лист не пустой
            //здесь вообще магия. подсмотрел в интернете. в кратце: сколько раз пользователь повторялся в листе, столько у него значение
            Map<Integer, Long> frequency =
                    listOfUsersWithCommon.stream().collect(Collectors.groupingBy(
                            Function.identity(), Collectors.counting())); //мапа где ключ это userId а значение фильмы

            //тоже в интернете нашел. здесь находится юзер с максимальным значением из превидущей мапы
            //мапа в которой ключ это userId с максимальным значением повторения фильмов
            Map.Entry<Integer, Long> maxKey = Collections.max(frequency.entrySet(), Map.Entry.comparingByValue());

            //здесь их мапы всех фильмов берется тот юзер с которым больше пересечений
            // и у него удаляются те фильмы с которыми есть совпадения с нашим пользователем
            usersFilms.get(maxKey.getKey()).removeIf(s -> user.contains(s));
            return usersFilms.get(maxKey.getKey()); //соответственно получаем фильмы которые остались
        }
        return List.of();
    }


}
