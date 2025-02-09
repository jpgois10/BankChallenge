package br.com.compass.view;

import br.com.compass.controller.UserController;
import br.com.compass.exception.InvalidUserDataException;
import br.com.compass.model.entity.Account;
import br.com.compass.model.entity.User;
import br.com.compass.model.entity.enums.AccountType;
import br.com.compass.exception.DuplicateAccountException;
import br.com.compass.exception.IncorrectPasswordException;
import br.com.compass.exception.UserNotFoundException;
import br.com.compass.repository.UserRepository;
import br.com.compass.service.validation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static br.com.compass.service.validation.CPFValidator.validate;

public class UserView {
    private final UserController userController;
    private final Scanner scanner;

    public UserView(UserController userController, Scanner scanner) {
        this.userController = userController;
        this.scanner = scanner;
    }

    public void showUserMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n========= Main Menu =========");
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
                    System.out.println("No accounts found for this user. Please create an account first.");
                    return;
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
                    return;
                } else {
                    System.out.println("Invalid choice.");
                }
                break;
            } catch (UserNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Please try again.");
            } catch (IncorrectPasswordException e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Please try again.");
            } catch (IllegalArgumentException e) {
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

        User user = new User(name, birthDate, cpf, phoneNumber, password);


        System.out.println("\nChoose the account type:");
        Arrays.stream(AccountType.values())
                .sorted(Comparator.comparingInt(AccountType::getCode))
                .forEach(type -> System.out.println(type.getCode() + " - " + type));

        int accountTypeCode = getValidAccountTypeCode();
        AccountType accountType = AccountType.fromCode(accountTypeCode);

        try {
            userController.registerUserAndCreateAccount(user, accountType);
            System.out.println("\nAccount opened successfully!");
            System.out.println("Name: " + user.getName());
            System.out.println("Account Number: " + user.getAccounts().iterator().next().getAccountNumber());
            System.out.println("Account Type: " + accountType);
            System.out.println("Initial Balance: " + String.format("%.2f", user.getAccounts().iterator().next().getBalance()));
        } catch (DuplicateAccountException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (InvalidUserDataException e) {
            System.out.println("User and account creation failed. No data was saved.");
        }
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
            System.out.print("Birth Date (DD/MM/YYYY): ");
            String birthDateStr = scanner.nextLine();
            try {
                return BirthDateValidator.validate(birthDateStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private String getValidCPF() {
        UserRepository userRepository = new UserRepository();

        while (true) {
            System.out.print("CPF (11 numbers): ");
            String cpf = scanner.nextLine();
            try {
                validate(cpf);

                if (userRepository.findByCpf(cpf).isPresent()) {
                    System.out.println("Error: CPF already registered.");
                    continue;
                }
                return cpf;
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private String getValidPhoneNumber() {
        while (true) {
            System.out.print("Phone Number (11 numbers): ");
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

    private Set<AccountType> getAvailableAccountTypes(String cpf) {
        Set<AccountType> existingAccountTypes = userController.getUserAccountTypes(cpf);


        return Arrays.stream(AccountType.values())
                .filter(type -> !existingAccountTypes.contains(type))
                .collect(Collectors.toSet());
    }

    private int getValidAccountTypeCode() {
        while (true) {
            System.out.print("Enter the account type code: ");
            try {
                int accountTypeCode = Integer.parseInt(scanner.nextLine());
                if (Arrays.stream(AccountType.values()).anyMatch(type -> type.getCode() == accountTypeCode)) {
                    return accountTypeCode;
                }
                System.out.println("Invalid account type. Please choose from the available options.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

//    private int getValidAccountTypeCode(Set<AccountType> availableAccountTypes) {
//        while (true) {
//            System.out.print("Enter the account type code: ");
//            try {
//                int accountTypeCode = Integer.parseInt(scanner.nextLine());
//                if (availableAccountTypes.stream().anyMatch(type -> type.getCode() == accountTypeCode)) {
//                    return accountTypeCode;
//                }
//                System.out.println("Invalid account type. Please choose from the available options.");
//            } catch (NumberFormatException e) {
//                System.out.println("Invalid input. Please enter a number.");
//            }
//        }
//    }
}