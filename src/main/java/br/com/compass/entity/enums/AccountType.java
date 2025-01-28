package br.com.compass.entity.enums;

public enum AccountType {
    CHECKING(30),
    SALARY(40),
    SAVINGS(50);

    private final int code;

    AccountType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
