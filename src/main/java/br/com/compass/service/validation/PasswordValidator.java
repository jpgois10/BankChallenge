package br.com.compass.service.validation;

public class PasswordValidator {

    public static void validate(String password) {
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
    }
}
