package br.com.compass.service.validation;

import br.com.compass.entity.enums.AccountType;
import br.com.compass.exception.DuplicateAccountException;
import br.com.compass.service.UserService;

import java.util.Set;

public class AccountTypeValidator {
    public static void validate(UserService userService, String cpf, int accountTypeCode) {
        Set<AccountType> existingAccountTypes = userService.getUserAccountTypes(cpf);
        AccountType accountType = AccountType.fromCode(accountTypeCode);

        if (accountType == null) {
            throw new IllegalArgumentException("Invalid account type code.");
        }

        if (existingAccountTypes.contains(accountType)) {
            throw new DuplicateAccountException("You already have a " + accountType + " account.");
        }
    }
}