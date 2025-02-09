package br.com.compass.repository;

import br.com.compass.model.entity.Account;
import br.com.compass.model.entity.Transaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class TransactionRepository extends RepositoryFactory<Transaction> {
    public TransactionRepository() {
        super();
    }

    public List<Transaction> findByAccount(Account account) {
        TypedQuery<Transaction> query = getEntityManager().createQuery(
                "SELECT t FROM Transaction t WHERE t.account = :account ORDER BY t.transactionDate DESC", Transaction.class);
        query.setParameter("account", account);
        return query.getResultList();
    }
}