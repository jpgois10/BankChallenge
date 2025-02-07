package br.com.compass.service;

import br.com.compass.model.entity.Account;
import br.com.compass.model.entity.Transaction;
import br.com.compass.model.entity.User;
import br.com.compass.model.entity.enums.TransactionType;
import br.com.compass.exception.InsufficientFundsException;
import br.com.compass.exception.InvalidAccountException;
import br.com.compass.exception.InvalidTransactionException;
import br.com.compass.model.repository.AccountRepository;
import br.com.compass.model.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class AccountService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountService() {
        this.accountRepository = new AccountRepository();
        this.transactionRepository = new TransactionRepository();
    }

    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public void createAccount(User user, Account account) {
        accountRepository.save(account);
    }

    public BigDecimal checkBalance(Account account) {
        return account.getBalance();
    }

    public void deposit(Account account, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0.00) {
            throw new InvalidTransactionException("The deposit value must be greater than zero");
        }

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction(account, TransactionType.DEPOSIT, amount);
        transactionRepository.save(transaction);
    }

    public void withdraw(Account account, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0.00) {
            throw new InvalidTransactionException("The withdrawal value must be greater than zero");
        }
        if (account.getBalance().compareTo(amount) < 0.00) {
            throw new InsufficientFundsException("Insufficient balance for withdrawal");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction(account, TransactionType.WITHDRAWAL, amount);
        transactionRepository.save(transaction);
    }

    public Account findAccountByNumber(String accountNumber) throws InvalidAccountException {
        Optional<Account> accountOptional = accountRepository.findByAccountNumber(accountNumber);
        if (accountOptional.isEmpty()) {
            throw new InvalidAccountException("Account not found with number: " + accountNumber);
        }
        return accountOptional.get();
    }

    public void transfer(Account sourceAccount, Account destinationAccount, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0.00) {
            throw new InvalidTransactionException("The transfer value must be greater than zero");
        }
        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient balance to complete the transfer.");
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