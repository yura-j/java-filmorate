package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.db.easy_jdbc.EasyJdbc;

import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("GenreDbStorage")
public class GenreDbStorage implements GenreStorage {

    public static final String TABLE_NAME = "genres";
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<Genre> get() {
        return new EasyJdbc<Genre>(jdbcTemplate)
                .select()
                .table(TABLE_NAME)
                .fields("genres.id, genres.name")
                .map((rs, index) -> new Genre(rs.getLong("genres.id"), rs.getString("genres.name")))
                .execute()
                .many();
    }

    @Override
    public Optional<Genre> getById(Long id) {
        return Optional.ofNullable(new EasyJdbc<Genre>(jdbcTemplate)
                .select()
                .table(TABLE_NAME)
                .fields("genres.id, genres.name")
                .where("genres.id = ?")
                .parameters(List.of(id))
                .map((rs, index) -> new Genre(rs.getLong("genres.id"), rs.getString("genres.name")))
                .execute()
                .one());
    }
}
