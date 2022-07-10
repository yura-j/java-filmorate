package ru.yandex.practicum.filmorate.validation.checkers;

import ru.yandex.practicum.filmorate.validation.ValidationRule;

public class NotNull extends ValidationRule {

    @Override
    protected boolean checkValue() {
        message = String.format("Поле %s должно быть заполнено (передано значение Null)", valueBox.getRusFieldName());
        return valueBox.getValue() != null;
    }
}
