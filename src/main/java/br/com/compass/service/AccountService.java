package br.com.compass.service;

import br.com.compass.entity.Account;
import br.com.compass.entity.User;
import br.com.compass.entity.enums.AccountType;
import br.com.compass.repository.AccountRepository;

import java.util.Optional;

public class AccountService {

    private AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(User user, AccountType accountType) {
        return user.createAccount(accountType);
    }

    public Account getAccount(User user, AccountType accountType) {
        return user.getAccount(accountType);
    }

    public Optional<Account> findAccountByAccountNumber(String accountNumber) {
        return accountRepository.findAccountByAccountNumber(accountNumber);
    }

    public void deleteAccount(Account account) {
        accountRepository.delete(account);
    }
}
