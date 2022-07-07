package ru.yandex.practicum.filmorate.validation.checkers;

import ru.yandex.practicum.filmorate.validation.ValidationRule;

import java.util.regex.Pattern;

public class HaveNoSpaces extends ValidationRule {

    @Override
    protected boolean checkValue() {
        if (!(valueBox.getValue() instanceof String)) {
            return false;
        }
        String testedValue = (String) valueBox.getValue();
        message = String.format("Поле %s не должно содержать пробельных символов (передано %s)",
                valueBox.getRusFieldName(), testedValue);
        return !Pattern.compile("[\\s]+").matcher(testedValue).find();
    }
}
