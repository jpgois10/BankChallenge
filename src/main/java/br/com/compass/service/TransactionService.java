package br.com.compass.service;

import br.com.compass.entity.Account;
import br.com.compass.entity.Transaction;
import br.com.compass.repository.TransactionRepository;

import java.util.List;

public class TransactionService {
    private TransactionRepository transactionRepository;

    public TransactionService() {
        this.transactionRepository = new TransactionRepository();
    }

    public List<Transaction> getStatement(Account account) {
        return transactionRepository.findByAccount(account);
    }

    public void close() {
        transactionRepository.close();
    }
}