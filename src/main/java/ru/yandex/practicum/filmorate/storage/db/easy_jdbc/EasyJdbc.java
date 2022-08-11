package ru.yandex.practicum.filmorate.storage.db.easy_jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.yandex.practicum.filmorate.error.QueryBuilderError;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EasyJdbc<T> {
    private final JdbcTemplate jdbc;
    private String table;
    private String where = "TRUE";
    private String fields = "id";
    private String order = "";
    private String group = "";
    private RowMapper<T> mapper = null;
    private boolean isUpdateQuery;

    public List<Object> getqParameters() {
        return qParameters;
    }

    private List<Object> qParameters = List.of();
    private Function<EasyJdbc<T>, EasyResult<T>> queryBuilderFunction;
    private Map<String, String> values;


    public EasyJdbc(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public EasyJdbc<T> table(String table) {
        this.table = table;
        return this;
    }

    public EasyJdbc<T> fields(String fields) {
        this.fields = fields;
        return this;
    }

    public EasyJdbc<T> where(String where) {
        this.where = where;
        return this;
    }

    public EasyJdbc<T> order(String order) {
        this.order = order;
        return this;
    }

    public EasyJdbc<T> group(String group) {
        this.group = group;
        return this;
    }

    public EasyJdbc<T> values(Map<String, Object> values) {

        this.values = new HashMap<>();
        values
                .forEach((key, value) -> this.values.put(key, String.valueOf(value)));
        return this;
    }

    public EasyJdbc<T> parameters(List<Object> qParameters) {
        this.qParameters = qParameters;
        return this;
    }

    public EasyResult<T> execute() {
        return this.queryBuilderFunction.apply(this);
    }

    public EasyJdbc<T> map(RowMapper<T> mapper) {
        this.mapper = mapper;
        return this;
    }

    public EasyJdbc<T> insert() {
        queryBuilderFunction = easyJdbc -> {

            List<String> fields = new ArrayList<>();
            List<Object> fieldValues = new ArrayList<>();
            values
                    .forEach((key, value) -> {
                        fields.add(key);

                        if (value.equals("NullObject{}")){
                            fieldValues.add(null);
                        } else {
                            fieldValues.add(value);
                        }
                    });

            List<String> placeHoldersList = fields
                    .stream()
                    .map(k -> "?")
                    .collect(Collectors.toList());
            String keys = "( " + String.join(",", fields) + " )";
            String placeHolders = " ( " + String.join(",", placeHoldersList) + " ) ";

            fieldValues.addAll(easyJdbc.getqParameters());
            easyJdbc.parameters(fieldValues);

            StringBuilder query = new StringBuilder();
            query
                    .append(" INSERT ").append(System.lineSeparator())
                    .append(" INTO ").append(easyJdbc.table).append(System.lineSeparator())
                    .append(keys).append(System.lineSeparator())
                    .append(" VALUES ").append(placeHolders).append(System.lineSeparator())
                    .append(" ; ");
            System.out.println(query.toString());
            System.out.println(fieldValues);
            return easyJdbc.exec(query.toString());
        };
        return this;
    }

    public EasyJdbc<T> update() {
        queryBuilderFunction = easyJdbc -> {
            List<String> fields = new ArrayList<>();
            List<Object> fieldValues = new ArrayList<>();
            values
                    .forEach((key, value) -> {
                        fields.add(key);

                        if (value.equals("NullObject{}")){
                            fieldValues.add(null);
                        } else {
                            fieldValues.add(value);
                        }
                    });
            List<String> listKeyValuePairs = fields
                    .stream()
                    .map(field -> field + " = ? ")
                    .collect(Collectors.toList());
            String joinedValues = String.join(",", listKeyValuePairs);

            fieldValues.addAll(easyJdbc.getqParameters());
            easyJdbc.parameters(fieldValues);

            StringBuilder query = new StringBuilder();
            query
                    .append(" UPDATE ").append(easyJdbc.table).append(System.lineSeparator())
                    .append(" SET ").append(joinedValues).append(System.lineSeparator())
                    .append(" WHERE ").append(easyJdbc.where).append(System.lineSeparator())
                    .append(" ; ");

            return easyJdbc.exec(query.toString());
        };
        return this;
    }

    public EasyJdbc<T> delete() {
        queryBuilderFunction = easyJdbc -> {
            StringBuilder query = new StringBuilder();
            query
                    .append(" DELETE ").append(System.lineSeparator())
                    .append(" FROM ").append(easyJdbc.table).append(System.lineSeparator())
                    .append(" WHERE ").append(easyJdbc.where).append(System.lineSeparator())
                    .append(" ; ");
            return easyJdbc.exec(query.toString());
        };
        return this;
    }

    public EasyJdbc<T> select() {
        queryBuilderFunction = easyJdbc -> {
            StringBuilder query = new StringBuilder();
            query
                    .append(" SELECT ").append(easyJdbc.fields).append(System.lineSeparator())
                    .append(" FROM ").append(easyJdbc.table).append(System.lineSeparator())
                    .append(" WHERE ").append(easyJdbc.where).append(System.lineSeparator());
            if (!easyJdbc.group.isBlank()) {
                query
                        .append(" GROUP BY ").append(easyJdbc.group).append(System.lineSeparator());
            }
            if (!easyJdbc.order.isBlank()) {
                query
                        .append(" ORDER BY ").append(easyJdbc.order);
            }
            query.append(" ; ");
            return new EasyResult<>(query.toString(), easyJdbc.mapper, jdbc, qParameters);
        };
        return this;
    }

    private EasyResult<T> exec(String query) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(query, new String[]{"id"});
            int index = 1;
            for (Object parameter : qParameters) {
                polySet(stmt, parameter, index++);
            }
            return stmt;
        }, keyHolder);

        return new EasyResult<>(keyHolder.getKey());
    }

    public static PreparedStatement polySet(PreparedStatement stmt, Object parameter, int index) throws SQLException {
        System.out.println("parameter = " + parameter);
        if (parameter instanceof String) {
            stmt.setString(index, (String) parameter);
        } else if (parameter instanceof Long){
            stmt.setLong(index, (Long) parameter);
        } else if (parameter instanceof Boolean){
            stmt.setBoolean(index, (Boolean) parameter);
        } else if (parameter instanceof Byte){
            stmt.setByte(index, (Byte) parameter);
        } else if (parameter instanceof Short){
            stmt.setShort(index, (Short) parameter);
        } else if (parameter instanceof Integer){
            stmt.setInt(index, (Integer) parameter);
        } else if (parameter instanceof Float){
            stmt.setFloat(index, (Float) parameter);
        } else if (parameter instanceof Double){
            stmt.setDouble(index, (Double) parameter);
        } else if (parameter instanceof BigDecimal){
            stmt.setBigDecimal(index, (BigDecimal) parameter);
        } else if (parameter == null){
            stmt.setNull(index, Types.NULL);
        } else {
            throw  new QueryBuilderError("тип параметра " + parameter + " не поддерживается");
        }

        return stmt;
    }

}
