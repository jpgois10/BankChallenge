package br.com.compass.service.validation;

import java.util.regex.Pattern;

public class CPFValidator {

    private static final Pattern CPF_PATTERN = Pattern.compile("^[0-9]{11}$");

    public void validate(String cpf) {
        if (!CPF_PATTERN.matcher(cpf).matches()) {
            throw new IllegalArgumentException("Invalid CPF format");
        }
    }
}
