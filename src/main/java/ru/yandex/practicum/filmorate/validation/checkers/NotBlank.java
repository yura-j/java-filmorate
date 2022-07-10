package ru.yandex.practicum.filmorate.validation.checkers;

import ru.yandex.practicum.filmorate.validation.ValidationRule;

public class NotBlank extends ValidationRule {

    @Override
    protected boolean checkValue() {
        if (!(valueBox.getValue() instanceof String)) {
            return false;
        }
        message = String.format("Поле %s не должно быть пустым (передано значение \"%s\")",
                valueBox.getRusFieldName(), valueBox.getValue());

        return !((String) valueBox.getValue()).isBlank();
    }
}
