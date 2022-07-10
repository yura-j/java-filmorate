package ru.yandex.practicum.filmorate.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ValidationValueBox {
    private Object value;
    private String fieldName;
    private String rusFieldName;
}
