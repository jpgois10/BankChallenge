package br.com.compass;

import br.com.compass.entity.Account;
import br.com.compass.entity.Transaction;
import br.com.compass.entity.User;
import br.com.compass.entity.enums.AccountType;
import br.com.compass.entity.enums.TransactionType;
import br.com.compass.util.HibernateUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DatabaseTest {

    public static void main(String[] args) {
        String userName = "João Silva";
        LocalDate userBirthDate = LocalDate.parse("15/04/1990", DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String userCpf = "12345678901";
        String userPhone = "81988889999";
        String userPassword = "123456";

        EntityManager em = null;
        EntityTransaction transaction = null;

        try {
            // Obtendo o EntityManager
            em = HibernateUtil.getEntityManager();
            transaction = em.getTransaction();

            // Verificando se o usuário já existe
            User existingUser = em.createQuery("SELECT u FROM User u WHERE u.cpf = :cpf", User.class)
                    .setParameter("cpf", userCpf)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            User user;

            if (existingUser == null) {
                user = new User(userName, userBirthDate, userCpf, userPhone, userPassword);

                Account account = new Account(user, AccountType.CHECKING);
                user.addAccount(account);

//                Transaction transaction1 = new Transaction(account, TransactionType.DEPOSIT, BigDecimal.valueOf(1000), null);
//                account.addTransaction(transaction1);

                transaction.begin();
                em.persist(user);
                transaction.commit();

                System.out.println("Novo usuário, conta e transação criados com sucesso!");
            } else {
                user = existingUser;
                System.out.println("Usuário já existe no banco de dados: " + user.getName());
            }

        } catch (Exception e) {
            // Caso ocorra algum erro, faz rollback para garantir a consistência dos dados
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (em != null) {
                HibernateUtil.closeEntityManager(em);
            }
        }
    }
}
