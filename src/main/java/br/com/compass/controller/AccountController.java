package br.com.compass.controller;

import br.com.compass.model.entity.Account;
import br.com.compass.model.entity.Transaction;
import br.com.compass.exception.InvalidAccountException;
import br.com.compass.service.AccountService;

import java.math.BigDecimal;
import java.util.List;

public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    public void deposit(Account account, BigDecimal amount) {
        accountService.deposit(account, amount);
    }

    public void withdraw(Account account, BigDecimal amount) {
        accountService.withdraw(account, amount);
    }

    public void transfer(Account sourceAccount, String destinationAccountNumber, BigDecimal amount) throws InvalidAccountException {
        Account destinationAccount = accountService.findAccountByNumber(destinationAccountNumber);
        accountService.transfer(sourceAccount, destinationAccount, amount);
    }

    public List<Transaction> getStatement(Account account) {
        return accountService.getStatement(account);
    }
}