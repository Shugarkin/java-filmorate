drop table IF EXISTS MPA CASCADE;
drop table IF EXISTS FILMS CASCADE;
drop table IF EXISTS GENRE_FILM CASCADE;
drop table IF EXISTS USERS CASCADE;
drop table IF EXISTS USER_FRIEND CASCADE;
drop table IF EXISTS LIKE_VAULT CASCADE;
drop table IF EXISTS DIRECTORS CASCADE;
drop table IF EXISTS FILM_DIRECTOR CASCADE;
drop table IF EXISTS FEED CASCADE;
drop table IF EXISTS review CASCADE;
drop table IF EXISTS REVIEW_LIKES CASCADE;
drop domain IF EXISTS EVENT_TYPE_ENUM CASCADE;


create table IF NOT EXISTS MPA
(
    MPA_ID   INTEGER primary key,
    MPA_NAME CHARACTER VARYING not null
);

create table IF NOT EXISTS FILMS
(
    FILM_ID      INTEGER auto_increment,
    FILM_NAME    CHARACTER VARYING not null,
    DESCRIPTION  CHARACTER VARYING not null,
    RELEASE_DATE TIMESTAMP         not null,
    DURATION     INTEGER           not null,
    MPA_ID       INTEGER,
    constraint FILMS_PK
    PRIMARY KEY  (FILM_ID),
    constraint FILMS_RATING_MPA_ID_FK
    foreign key (MPA_ID) references MPA(MPA_ID)
    );

create table IF NOT EXISTS GENRE
(
    GENRE_ID   INTEGER           not null primary key,
    GENRE_NAME CHARACTER VARYING not null
);

create table IF NOT EXISTS GENRE_FILM
(
    GENRE_FILM_ID INTEGER auto_increment,
    FILM_ID  INTEGER not null,
    GENRE_ID INTEGER not null,
    constraint GENRE_FILM_PK
    PRIMARY KEY (GENRE_FILM_ID),
    constraint GENRE_FILM_FILMS_FILM_ID_FK
    foreign key (FILM_ID) references FILMS(FILM_ID) ON DELETE CASCADE,
    constraint GENRE_GENRE_FILM_GENRE_ID_FK
    foreign key (GENRE_ID) references GENRE(GENRE_ID) ON DELETE CASCADE
    );

create table IF NOT EXISTS DIRECTORS
(
    DIRECTOR_ID   INTEGER auto_increment,
    DIRECTOR_NAME CHARACTER VARYING not null,
    constraint DIRECTORS_PK
    PRIMARY KEY (DIRECTOR_ID)
);

create table IF NOT EXISTS FILM_DIRECTOR
(
    DIRECTOR_FILM_ID INTEGER auto_increment,
    FILM_ID  INTEGER not null references FILMS(FILM_ID) ON DELETE CASCADE,
    DIRECTOR_ID INTEGER not null references DIRECTORS(DIRECTOR_ID) ON DELETE CASCADE,
    constraint FILM_DIRECTOR_PK
    PRIMARY KEY (DIRECTOR_FILM_ID),
    constraint FILM_DIRECTOR_FK
    foreign key (FILM_ID) references FILMS(FILM_ID) on delete cascade,
    constraint FILM_DIRECTOR_FK_1
    foreign key (DIRECTOR_ID) references DIRECTORS(DIRECTOR_ID) on delete cascade
    );

create table IF NOT EXISTS USERS
(
    USER_ID INTEGER  auto_increment,
    EMAIL    CHARACTER VARYING not null,
    LOGIN    CHARACTER VARYING not null,
    BIRTHDAY TIMESTAMP         not null,
    USERNAME CHARACTER VARYING not null,
    constraint USER_PK
    PRIMARY KEY  (USER_ID)
    );

create table IF NOT EXISTS USER_FRIEND
(
    FRIENDSHIP_ID INTEGER auto_increment,
    USER_ID   INTEGER not null,
    FRIEND_ID INTEGER not null,
    constraint USER_FRIEND_PK
    PRIMARY KEY (FRIENDSHIP_ID),
    constraint USER_FRIEND_USERS_FRIENDSHIP_ID_FK
    foreign key (USER_ID) references USERS ON DELETE CASCADE,
    constraint USER_FRIEND_FRIEND_ID_FRIENDSHIP_ID_FK
    foreign key (FRIEND_ID) references USERS ON DELETE CASCADE
    );

create table IF NOT EXISTS LIKE_VAULT
(
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    constraint LIKE_VAULT_PK
    PRIMARY KEY (USER_ID, FILM_ID),
    constraint LIKE_VAULT_FILMS_FILM_ID_FK
    foreign key (FILM_ID) references FILMS(FILM_ID) ON DELETE CASCADE,
    constraint LIKE_VAULT_USERS_USER_ID_FK
    foreign key (USER_ID) references USERS ON DELETE CASCADE
);


create table IF NOT EXISTS review
(
    reviewId INTEGER  auto_increment primary key,
    content_review  CHARACTER VARYING not null,
    is_Positive  CHARACTER VARYING not null,
    user_id INTEGER NOT NULL REFERENCES USERS (USER_ID),
    film_id INTEGER NOT NULL REFERENCES FILMS (FILM_ID),
    useful INTEGER
    );

create table if not exists REVIEW_LIKES
(
    user_id     int references USERS(user_id) ON DELETE CASCADE,
    review_id   int references review(reviewId) ON DELETE CASCADE,
    useful      INTEGER NOT NULL,
    constraint LIKE_REVIEW_PK PRIMARY KEY (user_id, review_id),
    constraint LIKE_REVIEW_REVIEW_ID_FK    foreign key (review_id) references review(reviewId) ON DELETE CASCADE,
    constraint LIKE_REVIEW_USER_ID_FK    foreign key (user_id) references USERS(USER_ID) ON DELETE CASCADE
    );




create type EVENT_TYPE_ENUM as enum ('created', 'approved', 'finshed');

create table IF NOT EXISTS FEED
(
    EVENT_ID    INTEGER auto_increment,
    USER_ID     INTEGER    not null,
    ENTITY_ID INTEGER    not null,
    EVENT_TYPE  CHARACTER VARYING    not null,
    OPERATION   CHARACTER VARYING    not null,
    TIMESTAMP_FEED BIGINT  not null,
    constraint FEED_PK
    PRIMARY KEY (EVENT_ID),
    constraint FEED_USERS_FK
    foreign key (USER_ID) references USERS(USER_ID) ON DELETE CASCADE
);

create unique index if not exists USER_EMAIL_UINDEX on USERS (email);
create unique index if not exists USER_LOGIN_UINDEX on USERS (login);








