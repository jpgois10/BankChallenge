package br.com.compass.repository;

import br.com.compass.entity.Account;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.Optional;

public class AccountRepository extends RepoFactory<Account> {

    private EntityManager entityManager;

    public AccountRepository(EntityManager entityManager) {
        super();
        this.entityManager = entityManager;
    }
    
    public Optional<Account> findAccountByAccountNumber(String accountNumber) {
        String queryStr = "SELECT a FROM Account a WHERE a.accountNumber = :accountNumber";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("accountNumber", accountNumber);

        try {
            Account account = (Account) query.getSingleResult();
            return Optional.of(account);
        } catch (Exception e) {
            return Optional.empty(); // Caso não encontre, retorna um Optional vazio
        }
    }
}
