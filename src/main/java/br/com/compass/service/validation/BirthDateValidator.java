package br.com.compass.service.validation;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class BirthDateValidator {
    public static LocalDate validate(String birthDateStr) {
        try {
            LocalDate birthDate = LocalDate.parse(birthDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            LocalDate today = LocalDate.now();
            int age = Period.between(birthDate, today).getYears();

            if (age < 18) {
                throw new IllegalArgumentException("You must be at least 18 years old to open an account.");
            }

            return birthDate;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use dd/MM/yyyy.");
        }
    }
}