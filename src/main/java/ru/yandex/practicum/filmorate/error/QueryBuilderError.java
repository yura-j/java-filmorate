package ru.yandex.practicum.filmorate.error;

public class QueryBuilderError extends RuntimeException{
    public QueryBuilderError(String message) {
        super(message);
    }
}
