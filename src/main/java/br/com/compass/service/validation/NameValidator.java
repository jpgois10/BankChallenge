package br.com.compass.service.validation;

public class NameValidator {
    public static void validate(String name) {
        if (name == null || name.length() < 2) {
            throw new IllegalArgumentException("Name must be at least 2 characters long.");
        }
    }
}