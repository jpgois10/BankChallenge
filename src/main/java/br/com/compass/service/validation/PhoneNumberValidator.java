package br.com.compass.service.validation;

import java.util.regex.Pattern;

public class PhoneNumberValidator {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{11}$");

    public static void validate(String phoneNumber) {
        if (!PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
    }
}
