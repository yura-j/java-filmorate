package ru.yandex.practicum.filmorate.storage.db.easy_jdbc;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.error.QueryBuilderError;

import java.util.List;

public class EasyResult<T> {
    private RowMapper<T> mapper;
    private JdbcTemplate jdbc;
    private String query;
    private Number affectedId;
    private List<Object> qParameters;

    public EasyResult(String query, RowMapper<T> mapper, JdbcTemplate jdbc, List<Object> qParameters) {
        this.query = query;
        this.mapper = mapper;
        this.jdbc = jdbc;
        this.qParameters = qParameters;
    }

    public EasyResult(Number id) {
        this.affectedId = id;
    }

    public T one() {
        if (jdbc == null) {
            throw new QueryBuilderError("one для cud пока не поддерживается");
        }
        try {
            if (qParameters.size() > 0) {
                return jdbc.queryForObject(query, mapper, qParameters.toArray());
            } else {
                return jdbc.queryForObject(query, mapper);
            }
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    public List<T> many() {
        if (jdbc == null) {
            throw new QueryBuilderError("many для cud пока не поддерживается");
        }
        if (qParameters.size() > 0) {
            return jdbc.query(query, mapper, qParameters.toArray());
        } else {
            return jdbc.query(query, mapper);
        }
    }

    public Number id() {
        return this.affectedId;
    }
}
