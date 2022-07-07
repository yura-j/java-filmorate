package ru.yandex.practicum.filmorate.validation;

public class Validator {
    boolean isOneErrorFail;
    boolean isThrowException;

    public Validator setIsOneErrorFail(boolean oneErrorFail) {
        this.isOneErrorFail = oneErrorFail;
        return this;
    }

    public Validator setThrowException(boolean isThrowException) {
        this.isOneErrorFail = isThrowException;
        return this;
    }

    public boolean validate(IValid testedObject) {
        boolean check = true;
        StringBuilder messageBuilder = new StringBuilder();
        for (ValidationChain chain : testedObject.rules()) {
            for (ValidationRule rule : chain.getValidations()) {
                rule.setSetThrowingException(isOneErrorFail);
                boolean isValid = rule.validate();
                check = check && isValid;
                if (isOneErrorFail && !isValid) {
                    messageBuilder.append(rule.getMessage()).append("\n");
                }
            }
        }
        if (isThrowException && !check) {
            throw new ValidationException(messageBuilder.toString());
        }
        return check;
    }
}
