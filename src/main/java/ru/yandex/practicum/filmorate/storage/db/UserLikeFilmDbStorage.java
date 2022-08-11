package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserLikeFilm;
import ru.yandex.practicum.filmorate.storage.UserLikeFilmStorage;
import ru.yandex.practicum.filmorate.storage.db.easy_jdbc.EasyJdbc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
@Qualifier("UserLikeFilmDbStorage")
public class UserLikeFilmDbStorage implements UserLikeFilmStorage {
    private final JdbcTemplate jdbcTemplate;
    public static final String LIKES_TABLE = "users_likes_films";
    public static final String LIKES_JOIN = "users_likes_films INNER JOIN users on users.id = users_likes_films.user_id" +
            " INNER JOIN films on films.id = users_likes_films.film_id ";

    @Autowired
    public UserLikeFilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<UserLikeFilm> get() {
        return new EasyJdbc<UserLikeFilm>(jdbcTemplate)
                .select()
                .table(LIKES_JOIN)
                .fields("users.id, users_likes_films.id, users.name, users.login, users.email, users.birthday, " +
                        "films.id, films.name, films.description, films.release_date, films.duration, films.mpa_id")
                .map(((rs, rowNum) -> new UserLikeFilm(
                        rs.getLong("users_likes_films.id")
                        , new Film(
                        rs.getLong("films.id"),
                        rs.getString("films.name"),
                        rs.getString("films.description"),
                        LocalDate.parse(rs.getString("films.release_date")),
                        rs.getInt("films.duration"),
                        rs.getLong("films.mpa_id"))
                        , new User(rs.getLong("users.id")
                        , rs.getString("users.email")
                        , rs.getString("users.login")
                        , rs.getString("users.name")
                        , LocalDate.parse(rs.getString("users.birthday")))
                )))
                .execute()
                .many();
    }

    @Override
    public UserLikeFilm getByFilmIdAndUserId(Long filmId, Long userId) {
        return new EasyJdbc<UserLikeFilm>(jdbcTemplate)
                .select()
                .table(UserLikeFilmDbStorage.LIKES_JOIN)
                .fields("users.id, users_likes_films.id, users.name, users.login, users.email, users.birthday, films.*")
                .where("users_likes_films.film_id = ? AND users_likes_films.user_id = ?")
                .parameters(List.of(filmId, userId))
                .map(((rs, rowNum) -> new UserLikeFilm(
                        rs.getLong("users_likes_films.id")
                        , new Film(
                            rs.getLong("films.id"),
                            rs.getString("films.name"),
                            rs.getString("films.description"),
                            LocalDate.parse(rs.getString("films.release_date")),
                            rs.getInt("films.duration"),
                            rs.getLong("films.mpa_id")
                         )
                        , new User(rs.getLong("users.id")
                        , rs.getString("users.email")
                        , rs.getString("login")
                        , rs.getString("name")
                        , LocalDate.parse(rs.getString("birthday"))
                ))))
                .execute()
                .one();
    }

    @Override
    public List<UserLikeFilm> getByFilm(Film film) {
        return new EasyJdbc<UserLikeFilm>(jdbcTemplate)
                .select()
                .table(UserLikeFilmDbStorage.LIKES_JOIN)
                .fields("users_likes_films.id, users.id, users.name, users.login, users.email, users.birthday")
                .where("users_likes_films.film_id = ? ")
                .parameters(List.of(film.getId()))
                .map(((rs, rowNum) -> new UserLikeFilm(
                        rs.getLong("users_likes_films.id")
                        , film
                        , new User(rs.getLong("users.id")
                        , rs.getString("users.email")
                        , rs.getString("users.login")
                        , rs.getString("users.name")
                        , LocalDate.parse(rs.getString("users.birthday"))
                ))))
                .execute()
                .many();
    }

    @Override
    public Long delete(Long id) {
        new EasyJdbc<UserLikeFilm>(jdbcTemplate)
            .delete()
            .table(LIKES_TABLE)
            .where("id = ? ")
            .parameters(List.of(id))
            .execute()
            .id();
        return id;
    }

    @Override
    public UserLikeFilm create(UserLikeFilm like) {
        Number createdUserLikeFilmId = new EasyJdbc<UserLikeFilm>(jdbcTemplate)
                .insert()
                .table(LIKES_TABLE)
                .values(Map.of(
                        "film_id", like.getFilm().getId(),
                        "user_id", like.getUser().getId()
                ))
                .execute()
                .id();
        if (createdUserLikeFilmId == null) {
            throw new RuntimeException("Не удалось создать запись");
        }
        return like;
    }
}
