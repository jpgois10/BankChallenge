package br.com.compass.repository;

import br.com.compass.entity.Account;
import br.com.compass.entity.User;
import br.com.compass.entity.enums.AccountType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.Optional;

public class AccountRepository extends RepoFactory<Account> {

    public Optional<Account> findByAccountNumber(String accountNumber) {
        try {
            TypedQuery<Account> query = getEntityManager().createQuery(
                    "SELECT a FROM Account a WHERE a.accountNumber = :accountNumber", Account.class);
            query.setParameter("accountNumber", accountNumber);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Account> findByUserAndType(User user, AccountType type) {
        try {
            TypedQuery<Account> query = getEntityManager().createQuery(
                    "SELECT a FROM Account a WHERE a.user = :user AND a.accountType = :type", Account.class);
            query.setParameter("user", user);
            query.setParameter("type", type);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}