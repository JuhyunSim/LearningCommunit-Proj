package com.zerobase.challenge.customAnnotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class StartDateValidator implements ConstraintValidator<StartDate, LocalDate> {

    @Override
    public void initialize(StartDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDate startDate, ConstraintValidatorContext context) {
        if (startDate == null) {
            return true; // @NotNull 이 null case를 다룸
        }
        return !startDate.isBefore(LocalDate.now());
    }
}