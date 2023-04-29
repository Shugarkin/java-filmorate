# java-filmorate
Template repository for Filmorate project.

Диаграмма - "java-filmorate/resources/диаграмма для тз 11.png"

users

получение всех пользователей
"select * from USERS"

создание пользователя
"insert into USERS ( EMAIL, LOGIN, BIRTHDAY, USERNAME) " +
"values (?, ?, ?, ?)"

изменение пользователя
"update USERS set " +
"USERNAME = ?, LOGIN = ?, EMAIL = ?, BIRTHDAY = ?  " +
"where USER_ID = ?"

добавление в друзья
"insert into USER_FRIEND (USER_ID, FRIEND_ID) " +
"values (?, ?) "

удаление из друзей
"delete from USER_FRIEND where USER_ID = ? and FRIEND_ID = ?"

получение по айди
"select USER_ID, EMAIL, LOGIN, BIRTHDAY, USERNAME " +
"from USERS where USER_ID = ?"

получение друзей по айди
"select FRIEND_ID from USER_FRIEND where USER_ID = ?"


films

получение всех фильмов
"select FILMS.FILM_ID, FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID, GENRE_FILM.GENRE_ID " +
"from FILMS left join GENRE_FILM on FILMS.FILM_ID = GENRE_FILM.FILM_ID "

добавление фильма
"insert into films (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)"
+ " values(?,?,?,?,?)"

изменение фильма
"select GENRE_FILM.GENRE_ID from GENRE_FILM join GENRE on GENRE_FILM.GENRE_ID = GENRE.GENRE_ID" +
" where FILM_ID = ?"

добавление лайка
"insert into LIKE_VAULT (FILM_ID, USER_ID) values(?,?)"

удаление лайка
"delete from LIKE_VAULT where FILM_ID = ? and USER_ID = ?"

получение фильма по айди
"select FILM_ID, FILM_NAME, " +
"DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID from FILMS where FILM_ID = ?"

получить все жанры
"select * from GENRE "

получить жанр по айди
"select * from GENRE where GENRE_ID = ?"

получить рейтинги
"select * from MPA"

получить рейтинг
"select * from MPA where MPA_ID = ?"

получение популярных фильмов
"select FILMS.FILM_ID\n" +
"from LIKE_VAULT right JOIN FILMS ON LIKE_VAULT.FILM_ID = FILMS.FILM_ID  \n" +
"GROUP BY LIKE_VAULT.FILM_ID  \n" +
"ORDER BY COUNT(USER_ID) \n" +
"DESC limit 10;"

