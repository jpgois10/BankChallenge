package br.com.compass.view;

import br.com.compass.controller.AccountController;
import br.com.compass.entity.Account;
import br.com.compass.entity.Transaction;
import br.com.compass.entity.enums.TransactionType;
import br.com.compass.exception.InsufficientFundsException;
import br.com.compass.exception.InvalidAccountException;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class AccountView {
    private final AccountController accountController;
    private final Scanner scanner;

    public AccountView(AccountController accountController) {
        this.accountController = accountController;
        this.scanner = new Scanner(System.in);
    }

    public void showBankMenu(Account account) {
        boolean running = true;

        while (running) {
            System.out.println("========= Bank Menu =========");
            System.out.println("|| 1. Deposit              ||");
            System.out.println("|| 2. Withdraw             ||");
            System.out.println("|| 3. Check Balance        ||");
            System.out.println("|| 4. Transfer             ||");
            System.out.println("|| 5. Bank Statement       ||");
            System.out.println("|| 0. Exit                 ||");
            System.out.println("=============================");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    deposit(account);
                    break;
                case 2:
                    withdraw(account);
                    break;
                case 3:
                    checkBalance(account);
                    break;
                case 4:
                    transfer(account);
                    break;
                case 5:
                    showBankStatement(account);
                    break;
                case 0:
                    System.out.println("Returning to Main Menu...");
                    running = false;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private void deposit(Account account) {
        System.out.print("Enter the deposit amount: ");
        BigDecimal amount = scanner.nextBigDecimal();
        scanner.nextLine();

        try {
            accountController.deposit(account, amount);
            System.out.println("Deposit successful!");
            System.out.println("Updated Balance: " + account.getBalance());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void withdraw(Account account) {
        System.out.print("Enter the withdrawal amount: ");
        BigDecimal amount = scanner.nextBigDecimal();
        scanner.nextLine();

        try {
            accountController.withdraw(account, amount);
            System.out.println("Withdrawal successful!");
            System.out.println("Updated Balance: " + account.getBalance());
        } catch (InsufficientFundsException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void checkBalance(Account account) {
        System.out.println("\n=== Account Balance ===");
        System.out.println("Current Balance: " + account.getBalance());
    }

    private void transfer(Account sourceAccount) {
        System.out.print("Enter the destination account number: ");
        String destinationAccountNumber = scanner.nextLine();

        System.out.print("Enter the transfer amount: ");
        BigDecimal amount = scanner.nextBigDecimal();
        scanner.nextLine();

        try {
            accountController.transfer(sourceAccount, destinationAccountNumber, amount);
            System.out.println("Transfer successful!");
            System.out.println("Updated Balance: " + sourceAccount.getBalance());
        } catch (InvalidAccountException | InsufficientFundsException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void showBankStatement(Account account) {
        System.out.println("\n=== Bank Statement ===");
        List<Transaction> transactions = accountController.getStatement(account);

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (Transaction transaction : transactions) {
            System.out.println("Date: " + transaction.getTransactionDate().format(formatter));
            System.out.println("Type: " + transaction.getTransactionType());
            if (transaction.getTransactionType() == TransactionType.TRANSFER) {
                System.out.println("Destination Account: " + transaction.getTransferDestinationAccount().getAccountNumber());
            }
            System.out.println("Amount: " + transaction.getAmount());
            System.out.println("-----------------------------");
        }
    }
}