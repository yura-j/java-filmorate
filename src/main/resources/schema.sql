DROP TABLE IF EXISTS users_likes_films;
DROP TABLE IF EXISTS film_has_genres;
DROP TABLE IF EXISTS users_friendship;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS mpa_rating;

CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  email varchar,
  login varchar,
  name varchar,
  birthday date
);

CREATE TABLE films (
  id SERIAL PRIMARY KEY,
  name varchar,
  description varchar,
  release_date date,
  duration int,
  mpa_id int8
);

CREATE TABLE genres (
  id SERIAL PRIMARY KEY,
  name varchar
);

CREATE TABLE film_has_genres (
  id SERIAL PRIMARY KEY,
  film_id int8,
  genre_id int8
);

CREATE TABLE mpa_rating (
  id SERIAL PRIMARY KEY,
  name varchar,
  description varchar
);

CREATE TABLE users_likes_films (
  id SERIAL PRIMARY KEY,
  user_id int8,
  film_id int8
);

CREATE TABLE users_friendship (
  id SERIAL PRIMARY KEY,
  user_id int8,
  friend_id int8
);

ALTER TABLE films ADD FOREIGN KEY (mpa_id) REFERENCES mpa_rating (id) ON DELETE CASCADE;

ALTER TABLE film_has_genres ADD FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE;

ALTER TABLE film_has_genres ADD FOREIGN KEY (genre_id) REFERENCES genres (id) ON DELETE CASCADE;

ALTER TABLE users_likes_films ADD FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE users_likes_films ADD FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE;

ALTER TABLE users_friendship ADD FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE users_friendship ADD FOREIGN KEY (friend_id) REFERENCES users (id) ON DELETE CASCADE;

INSERT INTO genres (name)
VALUES ('Комедия'), 
	('Драма'),
	('Мультфильм'),
	('Триллер'),
        ('Документальный'),
        ('Боевик');

INSERT INTO mpa_rating (name, description)
VALUES ('G', 'у фильма нет возрастных ограничений'), 
	('PG', 'детям рекомендуется смотреть фильм с родителями'),
	('PG-13', 'детям до 13 лет просмотр не желателен'),
	('R', 'лицам до 17 лет просматривать фильм можно только в присутствии взрослого'),
        ('NC-17', 'лицам до 18 лет просмотр запрещён');