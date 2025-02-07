package br.com.compass.entity.enums;

public enum AccountType {
    CHECKING(11, "Checking"),
    SALARY(22, "Salary"),
    SAVINGS(33, "Savings");

    private final int code;
    private final String description;

    AccountType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return description;
    }

    public static AccountType fromCode(int code) {
        for (AccountType type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }
}