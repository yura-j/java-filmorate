package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.error.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserLikeFilmStorage;
import ru.yandex.practicum.filmorate.storage.db.easy_jdbc.EasyJdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private UserLikeFilmStorage likeStorage;
    private final JdbcTemplate jdbcTemplate;
    public static final String TABLE_NAME = "films";
    public static final String FILM_GENRES_TABLE = "film_has_genres";
    public static final String MPA_TABLE = "mpa_rating";
    public static final String FILM_GENRES_JOIN = "film_has_genres INNER JOIN genres on genres.id = film_has_genres.genre_id";

    private final Map<String, List<?>> cache = new HashMap<>();

    @Autowired
    public FilmDbStorage(@Qualifier("UserLikeFilmDbStorage") UserLikeFilmStorage likeStorage, JdbcTemplate jdbcTemplate) {
        this.likeStorage = likeStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void resetCache() {
        cache.clear();
    }

    @Override
    public List<Film> getFilms() {

        List<Film> films = new EasyJdbc<Film>(jdbcTemplate)
                .select()
                .table(TABLE_NAME)
                .fields("id, name, description, release_date, duration, mpa_id")
                .map(this::map)
                .execute()
                .many();

        resetCache();
        films.forEach(this::initRelationsGreedy);
        resetCache();

        return films;
    }

    @Override
    public Film updateFilm(Film film) {
        Mpa mpa = film.getMpa();

        Number result = new EasyJdbc<Film>(jdbcTemplate)
                .update()
                .table(TABLE_NAME)
                .values(Map.of(
                        "name", film.getName(),
                        "description", film.getDescription(),
                        "release_date", film.getReleaseDate(),
                        "duration", film.getDuration(),
                        "mpa_id", mpa == null ? new NullObject() : mpa.getId()
                ))
                .where("id = ? ")
                .parameters(List.of(film.getId()))
                .execute()
                .id();

        if (result == null) {
            throw new NotFoundException("Пользователя с указанным идентификатором нет");
        }

        Number deletedIds = new EasyJdbc<Film>(jdbcTemplate)
                .delete()
                .table(FILM_GENRES_TABLE)
                .where("film_id = ? ")
                .parameters(List.of(film.getId()))
                .execute()
                .id();


        film.getGenres().forEach(genre -> {
            Number createdGenreId = new EasyJdbc<Genre>(jdbcTemplate)
                    .insert()
                    .table(FILM_GENRES_TABLE)
                    .values(Map.of(
                            "genre_id", genre.getId(),
                            "film_id", film.getId()
                    ))
                    .execute()
                    .id();
        });

        return getById(film.getId());
    }

    @Override
    public Film createFilm(Film film) {
        Mpa mpa = film.getMpa();

        Number createdFilmId = new EasyJdbc<Film>(jdbcTemplate)
                .insert()
                .table(TABLE_NAME)
                .values(Map.of(
                        "name", film.getName(),
                        "description", film.getDescription(),
                        "release_date", film.getReleaseDate(),
                        "duration", film.getDuration(),
                        "mpa_id", mpa == null ? new NullObject() : mpa.getId()
                ))
                .execute()
                .id();
        if (createdFilmId == null) {
            throw new RuntimeException("Не удалось создать запись");
        }

        if (film.getGenres().size() > 0) {
            film.getGenres().forEach(genre -> {
                new EasyJdbc<Genre>(jdbcTemplate)
                        .insert()
                        .table(FILM_GENRES_TABLE)
                        .values(Map.of(
                                "genre_id", genre.getId(),
                                "film_id", createdFilmId
                        ))
                        .execute()
                        .id();
            });
        }
        return getById(createdFilmId.longValue());
    }

    @Override
    public Film getById(Long id) {
        Film notInitializedFilm =
                new EasyJdbc<Film>(jdbcTemplate)
                        .select()
                        .table(TABLE_NAME)
                        .fields("id, name, description, release_date, duration, mpa_id")
                        .where("id = ? ")
                        .parameters(List.of(id))
                        .map(this::map)
                        .execute()
                        .one();
        if (notInitializedFilm == null) {
            throw new NotFoundException("Не найдено");
        }
        return initRelationsLazy(notInitializedFilm);
    }

    public Film map(ResultSet rs, int rowNum) throws SQLException {
        return new Film(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                LocalDate.parse(rs.getString("release_date")),
                rs.getInt("duration"),
                rs.getLong("mpa_id")
        );
    }

    public Film initRelationsLazy(Film film) {
        List<Genre> filmGenres = new EasyJdbc<Genre>(jdbcTemplate)
                .select()
                .table(FILM_GENRES_JOIN)
                .fields("genres.id, genres.name")
                .where("film_has_genres.film_id = ? ")
                .order("genres.id asc")
                .parameters(List.of(film.getId()))
                .map(((rs, rowNum) -> new Genre(rs.getLong("genres.id"), rs.getString("genres.name"))))
                .execute()
                .many();

        Mpa filmMpa = new EasyJdbc<Mpa>(jdbcTemplate)
                .select()
                .table(MPA_TABLE)
                .fields("id, description, name")
                .where("id = ? ")
                .parameters(List.of(film.getMpaId()))
                .map(((rs, rowNum) -> new Mpa(rs.getLong("mpa_rating.id"), rs.getString("mpa_rating.description"), rs.getString("mpa_rating.name"))))
                .execute()
                .one();
        List<UserLikeFilm> filmLikes = likeStorage.getByFilm(film);
        film.setGenres(new LinkedHashSet<>(filmGenres));
        film.setLikes(new LinkedHashSet<>(filmLikes));
        film.setMpa(filmMpa);
        return film;
    }

    public Film initRelationsGreedy(Film film) {

        List<FilmHasGenres> genres = cache.containsKey(FILM_GENRES_JOIN)
                ? (List<FilmHasGenres>) cache.get(FILM_GENRES_JOIN)
                : new EasyJdbc<FilmHasGenres>(jdbcTemplate)
                .select()
                .table(FILM_GENRES_JOIN)
                .fields("film_has_genres.id, film_has_genres.film_id, genres.id, genres.name, film_has_genres.genre_id")
                .order("genres.id asc")
                .map((rs, rowNum) -> new FilmHasGenres(
                        rs.getLong("film_has_genres.id"),
                        rs.getLong("film_has_genres.film_id"),
                        rs.getLong("film_has_genres.genre_id"),
                        new Genre(
                                rs.getLong("genres.id"),
                                rs.getString("genres.name"))
                ))
                .execute()
                .many();

        List<Mpa> mpa = cache.containsKey(MPA_TABLE)
                ? (List<Mpa>) cache.get(MPA_TABLE)
                : new EasyJdbc<Mpa>(jdbcTemplate)
                .select()
                .table(MPA_TABLE)
                .fields("id, description, name")
                .map(((rs, rowNum) -> new Mpa(rs.getLong("id"), rs.getString("description"), rs.getString("name"))))
                .execute()
                .many();

        List<UserLikeFilm> likes = cache.containsKey(UserLikeFilmDbStorage.LIKES_JOIN)
                ? (List<UserLikeFilm>) cache.get(UserLikeFilmDbStorage.LIKES_JOIN)
                : likeStorage.get();

        Mpa filmMpa = mpa
                .stream()
                .filter(mpaRating -> mpaRating.getId() == film.getMpaId())
                .findFirst()
                .orElse(null);
        List<Genre> filmGenres = genres
                .stream()
                .filter(genre -> genre.getFilmId() == film.getId())
                .map(filmHasGenre -> filmHasGenre.getGenre())
                .collect(Collectors.toList());
        List<UserLikeFilm> filmLikes = likes
                .stream()
                .filter(likeFilm -> likeFilm.getFilm().getId() == film.getId())
                .collect(Collectors.toList());

        film.setGenres(new LinkedHashSet<>(filmGenres));
        film.setLikes(new LinkedHashSet<>(filmLikes));
        film.setMpa(filmMpa);
        return film;
    }
}
