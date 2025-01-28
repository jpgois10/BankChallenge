package br.com.compass.service;

import br.com.compass.entity.Account;
import br.com.compass.entity.Transaction;
import java.math.BigDecimal;

public class TransactionService {

    public void processDeposit(Transaction transaction) {
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) < 0.00) {
            throw new IllegalStateException("Deposit amount must be greater than zero");
        }
        Account account = transaction.getAccount();
        account.setBalance(account.getBalance().add(transaction.getAmount()));
        account.addTransaction(transaction);
    }

    public void processWithdrawal(Transaction transaction) {
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) < 0.00) {
            throw new IllegalStateException("Withdrawal amount must be greater than zero");
        }
        Account account = transaction.getAccount();
        if (account.getBalance().compareTo(transaction.getAmount()) < 0.00) {
            throw new IllegalStateException("Insufficient funds");
        }
        account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        account.addTransaction(transaction);
    }

    public void processTransfer(Transaction transaction) {
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) < 0.00) {
            throw new IllegalStateException("Transfer amount must be greater than zero");
        }
        Account account = transaction.getAccount();
        Account transferDestinationAccount = transaction.getTransferDestinationAccount();
        if (account.getBalance().compareTo(transaction.getAmount()) <= 0.00) {
            throw new IllegalStateException("Insufficient funds for transfer");
        }
        account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        transferDestinationAccount.setBalance(transferDestinationAccount.getBalance().add(transaction.getAmount()));
        account.addTransaction(transaction);
    }
}
