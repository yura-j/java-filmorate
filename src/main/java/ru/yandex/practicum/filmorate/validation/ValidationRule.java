package ru.yandex.practicum.filmorate.validation;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.error.ValidationException;

@Getter
@Setter
public abstract class ValidationRule {
    protected ValidationValueBox valueBox;
    protected String message = "Данные не корректны";
    private boolean isSetThrowingException;

    protected abstract boolean checkValue();

    public boolean validate() {
        boolean check = checkValue();
        if (isSetThrowingException && !check) {
            throw new ValidationException(message);
        }
        return check;
    }
}

