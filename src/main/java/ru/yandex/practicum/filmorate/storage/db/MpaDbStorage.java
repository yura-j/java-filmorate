package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.db.easy_jdbc.EasyJdbc;

import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("MpaDbStorage")
public class MpaDbStorage implements MpaStorage {
    public static final String TABLE_NAME = "mpa_rating";
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> get() {
        return new EasyJdbc<Mpa>(jdbcTemplate)
                .select()
                .table(TABLE_NAME)
                .fields("mpa_rating.id, mpa_rating.name, mpa_rating.description")
                .map((rs, index) -> new Mpa(rs.getLong("mpa_rating.id"), rs.getString("mpa_rating.description"), rs.getString("mpa_rating.name")))
                .execute()
                .many();
    }

    @Override
    public Optional<Mpa> getById(Long id) {
        return Optional.ofNullable(new EasyJdbc<Mpa>(jdbcTemplate)
                .select()
                .table(TABLE_NAME)
                .fields("mpa_rating.id, mpa_rating.name, mpa_rating.description")
                .where("mpa_rating.id = ?")
                .parameters(List.of(id))
                .map((rs, index) -> new Mpa(rs.getLong("mpa_rating.id"), rs.getString("mpa_rating.description"), rs.getString("mpa_rating.name")))
                .execute()
                .one());
    }
}
