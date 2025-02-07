package br.com.compass.view;

import br.com.compass.controller.UserController;
import br.com.compass.entity.Account;
import br.com.compass.entity.User;
import br.com.compass.entity.enums.AccountType;
import br.com.compass.exception.DuplicateAccountException;
import br.com.compass.exception.IncorrectPasswordException;
import br.com.compass.exception.UserNotFoundException;
import br.com.compass.service.UserService;
import br.com.compass.service.validation.CPFValidator;
import br.com.compass.service.validation.PasswordValidator;
import br.com.compass.service.validation.PhoneNumberValidator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class UserView {
    private final UserController userController;
    private final Scanner scanner;

    public UserView(UserController userController) {
        this.userController = userController;
        this.scanner = new Scanner(System.in);
    }

    public void showUserMenu() {
        boolean running = true;

        while (running) {
            System.out.println("========= Main Menu =========");
            System.out.println("|| 1. Login                ||");
            System.out.println("|| 2. Account Opening      ||");
            System.out.println("|| 0. Exit                 ||");
            System.out.println("=============================");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    loginUser();
                    break;
                case 2:
                    openAccount();
                    break;
                case 0:
                    System.out.println("Exiting...");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private void loginUser() {
        System.out.println("\n=== Login ===");

        while (true) {
            System.out.print("CPF: ");
            String cpf = scanner.nextLine();

            System.out.print("Password: ");
            String password = scanner.nextLine();

            try {
                User loggedInUser = userController.loginUser(cpf, password);
                System.out.println("Login successful! Welcome, " + loggedInUser.getName() + ".");

                Set<Account> accounts = loggedInUser.getAccounts();
                if (accounts.isEmpty()) {
                    System.out.println("No accounts found. Creating a default Checking account...");
                    try {
                        Account account = userController.createAccount(loggedInUser, AccountType.CHECKING);
                        System.out.println("Account created successfully!");
                        System.out.println("Account Number: " + account.getAccountNumber());
                        accounts = loggedInUser.getAccounts();
                    } catch (DuplicateAccountException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }

                List<Account> accountList = new ArrayList<>(accounts);

                System.out.println("\n=== Choose an Account ===");
                for (int i = 0; i < accountList.size(); i++) {
                    Account account = accountList.get(i);
                    System.out.println((i + 1) + ". " + account.getAccountType() + " - " + account.getAccountNumber());
                }
                System.out.print("Choose an account: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                if (choice > 0 && choice <= accountList.size()) {
                    Account account = accountList.get(choice - 1);
                    AccountView accountView = new AccountView(userController.getAccountController());
                    accountView.showBankMenu(account);
                } else {
                    System.out.println("Invalid choice.");
                }

                break;
            } catch (UserNotFoundException | IncorrectPasswordException e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Please try again.");
            }
        }
    }

    private void openAccount() {
        System.out.println("\n=== Account Opening ===");

        String name = getValidName();
        LocalDate birthDate = getValidBirthDate();
        String cpf = getValidCPF();
        String phoneNumber = getValidPhoneNumber();
        String password = getValidPassword();

        Set<AccountType> availableAccountTypes = getAvailableAccountTypes(cpf);
        if (availableAccountTypes.isEmpty()) {
            System.out.println("You already have all types of accounts.");
            return;
        }

        System.out.println("Choose the account type:");
        availableAccountTypes.forEach(type -> System.out.println(type.getCode() + " - " + type));
        int accountTypeCode = getValidAccountTypeCode(availableAccountTypes);
        AccountType accountType = AccountType.fromCode(accountTypeCode);

        try {
            User user = new User(name, birthDate, cpf, phoneNumber, password);
            Account account = userController.createAccount(user, accountType);
            System.out.println("\nAccount opened successfully!");
            System.out.println("Name: " + user.getName());
            System.out.println("Account Number: " + account.getAccountNumber());
            System.out.println("Account Type: " + account.getAccountType());
            System.out.println("Initial Balance: " + account.getBalance());
        } catch (DuplicateAccountException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private Set<AccountType> getAvailableAccountTypes(String cpf) {
        Set<AccountType> existingAccountTypes = userController.getUserAccountTypes(cpf);
        return Set.of(AccountType.values()).stream()
                .filter(type -> !existingAccountTypes.contains(type))
                .collect(Collectors.toSet());
    }

    private String getValidName() {
        while (true) {
            System.out.print("Name: ");
            String name = scanner.nextLine();
            try {
                NameValidator.validate(name);
                return name;
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private LocalDate getValidBirthDate() {
        while (true) {
            System.out.print("Birth Date (dd/MM/yyyy): ");
            String birthDateStr = scanner.nextLine();
            try {
                return BirthDateValidator.validate(birthDateStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private String getValidCPF() {
        while (true) {
            System.out.print("CPF: ");
            String cpf = scanner.nextLine();
            try {
                CPFValidator.validate(cpf);
                return cpf;
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private String getValidPhoneNumber() {
        while (true) {
            System.out.print("Phone Number: ");
            String phoneNumber = scanner.nextLine();
            try {
                PhoneNumberValidator.validate(phoneNumber);
                return phoneNumber;
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private String getValidPassword() {
        while (true) {
            System.out.print("Password (6 digits): ");
            String password = scanner.nextLine();
            try {
                PasswordValidator.validate(password);
                return password;
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private int getValidAccountTypeCode(Set<AccountType> availableAccountTypes) {
        while (true) {
            System.out.print("Enter the account type code: ");
            try {
                int accountTypeCode = Integer.parseInt(scanner.nextLine());
                if (availableAccountTypes.stream().anyMatch(type -> type.getCode() == accountTypeCode)) {
                    return accountTypeCode;
                }
                System.out.println("Invalid account type. Please choose from the available options.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}