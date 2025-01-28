package br.com.compass.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class HibernateUtil {

    private static final EntityManagerFactory emf;

    static {
        try {
            emf = Persistence.createEntityManagerFactory("bankChallengePU");
        } catch (Throwable e) {
            System.err.println("Initial EntityManagerFactory creation failed: " + e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void closeEntityManagerFactory() {
        if (emf.isOpen()) {
            emf.close();
        }
    }

    public static void closeEntityManager(EntityManager em) {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
}
