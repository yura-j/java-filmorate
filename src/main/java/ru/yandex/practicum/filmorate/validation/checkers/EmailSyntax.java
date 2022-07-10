package ru.yandex.practicum.filmorate.validation.checkers;

import ru.yandex.practicum.filmorate.validation.ValidationRule;

public class EmailSyntax extends ValidationRule {

    @Override
    protected boolean checkValue() {
        if (!(valueBox.getValue() instanceof String)) {
            return false;
        }
        String testedValue = (String) valueBox.getValue();
        message = String.format("Поле %s должно содержать @ (передано %s)",
                valueBox.getRusFieldName(), testedValue);

        return testedValue.contains("@");
    }
}
