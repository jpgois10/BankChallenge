package br.com.compass.entity;

import br.com.compass.entity.enums.TransactionType;
import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "Transactions")
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "transfer_destination_account_id")
    private Account transferDestinationAccount;

    public Transaction() {
    }

    public Transaction(Account account, TransactionType transactionType, BigDecimal amount, Account transferDestinationAccount) {
        this.account = account;
        this.transactionType = transactionType;
        this.amount = amount;
        this.transferDestinationAccount = transferDestinationAccount;
    }

    public Integer getId() {
        return id;
    }


    public Account getAccount() {
        return account;
    }


    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0.00) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (transactionType == TransactionType.WITHDRAWAL && account.getBalance().compareTo(amount) < 0.00) {
            throw new IllegalStateException("Insufficient balance for withdrawal");
        }

        this.amount = amount;
    }


    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Account getTransferDestinationAccount() {
        return transferDestinationAccount;
    }

    public void setTransferDestinationAccount(Account transferDestinationAccount) {
        if (this.transactionType != TransactionType.TRANSFER) {
            throw new IllegalStateException("Transfer destination account should only be set for transfer transactions");
        }
        this.transferDestinationAccount = transferDestinationAccount;
    }

    public void processDeposit() {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Deposit amount must be greater than zero");
        }
        account.setBalance(account.getBalance().add(amount));
    }

    public void processWithdrawal() {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Withdrawal amount must be greater than zero");
        }
        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        account.setBalance(account.getBalance().subtract(amount));
    }

    public void processTransfer() {
        if (amount.compareTo(BigDecimal.ZERO) <= 0.00) {
            throw new IllegalStateException("Transfer amount must be greater than zero");
        }
        if (account.getBalance().compareTo(amount) < 0.00) {
            throw new IllegalStateException("Insufficient funds for transfer");
        }
        account.setBalance(account.getBalance().subtract(amount));
        transferDestinationAccount.setBalance(transferDestinationAccount.getBalance().add(amount));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction transaction)) return false;
        return Objects.equals(getId(), transaction.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Transaction{")
                .append("id=").append(id)
                .append(", account=").append(account.getAccountNumber())
                .append(", transactionType=").append(transactionType)
                .append(", amount=").append(amount)
                .append(", transactionDate=").append(transactionDate);

        if (transactionType == TransactionType.TRANSFER && transferDestinationAccount != null) {
            sb.append(", transferDestinationAccount=").append(transferDestinationAccount.getAccountNumber());
        }

        sb.append('}');
        return sb.toString();
    }

}
