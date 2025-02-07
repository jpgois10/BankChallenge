package br.com.compass.model.entity;

import br.com.compass.model.entity.enums.AccountType;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "cpf")
})
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(unique = true, nullable = false, length = 11)
    private String cpf;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Account> accounts = new HashSet<>();

    public User() {
    }

    public User(String name, LocalDate birthDate, String cpf, String phoneNumber, String password) {
        this.name = name;
        this.birthDate = birthDate;
        this.cpf = cpf;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Account> getAccounts() {
        return Collections.unmodifiableSet(accounts);
    }

    public Account createAccount(AccountType type) {
        boolean hasAccountType = accounts.stream()
                .anyMatch(account -> account.getAccountType() == type);

        if (hasAccountType) {
            throw new IllegalStateException(
                    "User already has a " + type + " account");
        }

        Account account = new Account(this, type);
        accounts.add(account);
        return account;
    }

    public Account getAccount(AccountType type) {
        return accounts.stream()
                .filter(account -> account.getAccountType() == type)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "User does not have a " + type + " account"));
    }

//    public void addAccount(Account account) {
//        accounts.add(account);
//        account.setUser(this);
//    }
    public void addAccount(Account account) {
        if (this.accounts == null) {
            this.accounts = new HashSet<>();
        }
        this.accounts.add(account);
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthDate=" + birthDate +
                ", cpf='" + cpf + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}