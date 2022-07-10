package ru.yandex.practicum.filmorate.validation.checkers;

import ru.yandex.practicum.filmorate.validation.ValidationRule;

public class Positive extends ValidationRule {

    @Override
    protected boolean checkValue() {
        if (!(valueBox.getValue() instanceof Integer)) {
            return false;
        }
        Integer testedValue = (Integer) valueBox.getValue();
        message = String.format("Поле %s должно быть положительным (передано значение \"%d\")",
                valueBox.getRusFieldName(), testedValue);

        return testedValue > 0;
    }
}
