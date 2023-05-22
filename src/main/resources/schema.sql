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
    foreign key (FILM_ID) references FILMS(FILM_ID),
    constraint GENRE_GENRE_FILM_GENRE_ID_FK
    foreign key (GENRE_ID) references GENRE(GENRE_ID)
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
    foreign key (USER_ID) references USERS,
    constraint USER_FRIEND_FRIEND_ID_FRIENDSHIP_ID_FK
    foreign key (FRIEND_ID) references USERS
    );

create table IF NOT EXISTS LIKE_VAULT
(
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    constraint LIKE_VAULT_PK
    PRIMARY KEY (USER_ID, FILM_ID),
    constraint LIKE_VAULT_FILMS_FILM_ID_FK
    foreign key (FILM_ID) references FILMS(FILM_ID),
    constraint LIKE_VAULT_USERS_USER_ID_FK
    foreign key (USER_ID) references USERS
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
    FILM_ID  INTEGER not null,
    DIRECTOR_ID INTEGER not null,
    constraint FILM_DIRECTOR_PK
    PRIMARY KEY (FILM_ID, DIRECTOR_ID),
    constraint FILM_DIRECTOR_FK
    foreign key (FILM_ID) references FILMS on delete cascade,
    constraint FILM_DIRECTOR_FK_1
    foreign key (DIRECTOR_ID) references DIRECTORS on delete cascade
    );

create unique index if not exists USER_EMAIL_UINDEX on USERS (email);
create unique index if not exists USER_LOGIN_UINDEX on USERS (login);








