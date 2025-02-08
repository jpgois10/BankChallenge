package br.com.compass.model.repository;

import br.com.compass.model.entity.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;
import java.util.Optional;

public abstract class RepositoryFactory<T> {
    private final EntityManager entityManager;

    public RepositoryFactory() {
        this.entityManager = Persistence.createEntityManagerFactory("bankChallengePU").createEntityManager();
    }

    public void save(T entity) {
        EntityTransaction transaction = entityManager.getTransaction();
        boolean isNewTransaction = !transaction.isActive();

        try {
            if (isNewTransaction) {
                transaction.begin();
            }
            if (entityManager.contains(entity)) {
                entityManager.merge(entity);
            } else {
                entityManager.persist(entity);
            }
            if (isNewTransaction) {
                transaction.commit();
            }
        } catch (RuntimeException e) {
            if (isNewTransaction) {
                transaction.rollback();
            }
            throw new RuntimeException("Failed to save entity: " + e.getMessage(), e);
        }
    }

    public void update(Account account) {
        EntityTransaction transaction = null;
        try {
            transaction = beginTransaction();
            getEntityManager().merge(account);
            commitTransaction(transaction);
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                rollbackTransaction(transaction);
            }
            throw new RuntimeException("Failed to update account: " + e.getMessage(), e);
        }
    }

    public Optional<T> findById(Class<T> entityClass, Integer id) {
        T entity = entityManager.find(entityClass, id);
        return Optional.ofNullable(entity);
    }

    public List<T> findAll(Class<T> entityClass) {
        return entityManager.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass)
                .getResultList();
    }

    public void delete(T entity) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e;
        }
    }

    public EntityTransaction beginTransaction() {
        EntityTransaction transaction = getEntityManager().getTransaction();
        transaction.begin();
        return transaction;
    }

    public void commitTransaction(EntityTransaction transaction) {
        transaction.commit();
    }

    public void rollbackTransaction(EntityTransaction transaction) {
        if (transaction.isActive()) {
            transaction.rollback();
        }
    }

    public void close() {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
