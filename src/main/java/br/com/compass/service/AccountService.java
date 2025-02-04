package br.com.compass.service;

import br.com.compass.entity.Account;
import br.com.compass.entity.Transaction;
import br.com.compass.entity.enums.TransactionType;
import br.com.compass.repository.AccountRepository;
import br.com.compass.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.List;

public class AccountService {
    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;

    public AccountService() {
        this.accountRepository = new AccountRepository();
        this.transactionRepository = new TransactionRepository();
    }

    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public BigDecimal checkBalance(Account account) {
        return account.getBalance();
    }

    public void deposit(Account account, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0.00) {
            throw new IllegalArgumentException("The deposit value must be greater than zero");
        }

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction(account, TransactionType.DEPOSIT, amount);
        transactionRepository.save(transaction);
    }

    public void withdraw(Account account, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0.00) {
            throw new IllegalArgumentException("The withdrawal value must be greater than zero");
        }
        if (account.getBalance().compareTo(amount) < 0.00) {
            throw new IllegalArgumentException("Insufficient balance for withdrawal");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction(account, TransactionType.WITHDRAWAL, amount);
        transactionRepository.save(transaction);
    }

    public void transfer(Account sourceAccount, Account destinationAccount, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0.00) {
            throw new IllegalArgumentException("The transfer value must be greater than zero");
        }
        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance to complete the transfer.");
        }

        withdraw(sourceAccount, amount);
        deposit(destinationAccount, amount);

        Transaction transaction = new Transaction(sourceAccount, TransactionType.TRANSFER, amount, destinationAccount);
        transactionRepository.save(transaction);

    }

    public List<Transaction> getStatement(Account account) {
        return transactionRepository.findByAccount(account);
    }

    public void close() {
        accountRepository.close();
        transactionRepository.close();
    }
}