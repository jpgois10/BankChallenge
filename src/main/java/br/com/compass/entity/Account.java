package br.com.compass.entity;

import br.com.compass.entity.enums.AccountType;
import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Accounts",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "account_type"}
        ))
public class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "account_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balance;

    @Column(name = "account_number", nullable = false, unique = true, length = 13)
    private String accountNumber;

    @OneToMany(mappedBy = "account")
    private List<Transaction> transactions = new ArrayList<>();

    public Account() {
    }

    public Account(User user, AccountType accountType) {
        this.user = user;
        this.accountType = accountType;
        balance = BigDecimal.ZERO;
        accountNumber = generateAccountNumber(user.getCpf(), accountType);
    }

    private String generateAccountNumber(String cpf, AccountType type) {
        return cpf + type.getCode();
    }

    public Integer getId() {
        return id;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0.00) {
            throw new IllegalArgumentException("Balance cannot be null or negative");
        }
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Account account)) return false;
        return Objects.equals(getId(), account.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", user=" + user +
                ", accountType=" + accountType +
                ", balance=" + balance +
                ", accountNumber='" + accountNumber + '\'' +
                '}';
    }
}
