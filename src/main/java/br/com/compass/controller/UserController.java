package br.com.compass.controller;

import br.com.compass.model.entity.Account;
import br.com.compass.model.entity.User;
import br.com.compass.model.entity.enums.AccountType;
import br.com.compass.exception.DuplicateAccountException;
import br.com.compass.exception.IncorrectPasswordException;
import br.com.compass.exception.UserNotFoundException;
import br.com.compass.service.AccountService;
import br.com.compass.service.AuthService;
import br.com.compass.service.UserService;
import br.com.compass.service.validation.AccountTypeValidator;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UserController {
    private  AuthService authService;
    private  UserService userService;
    private  AccountService accountService;
    private  AccountController accountController;

    public UserController(AuthService authService, UserService userService, AccountService accountService, AccountController accountController) {
        this.authService = authService;
        this.userService = userService;
        this.accountService = accountService;
        this.accountController = accountController;
    }

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public Optional<User> findUserByCpf(String cpf) {
        return userService.findUserByCpf(cpf);
    }

    public Set<AccountType> getAvailableAccountTypes(String cpf) {
        return userService.getAvailableAccountTypes(cpf);
    }

    public void registerUser(User user) {
        userService.registerUser(user);
    }


    public User loginUser(String cpf, String password) throws UserNotFoundException, IncorrectPasswordException {
        return authService.loginUser(cpf, password);
    }

    public Account createAccount(User user, AccountType accountType) throws DuplicateAccountException {
        AccountTypeValidator.validate(userService, user.getCpf(), accountType.getCode());
        return userService.createAccount(user, accountType);
    }

    public Set<AccountType> getUserAccountTypes(String cpf) {
        return userService.getUserAccountTypes(cpf);
    }

    public AccountController getAccountController() {
        return accountController;
    }

    public void registerUserAndCreateAccount(User user, AccountType accountType) {
        EntityTransaction transaction = null;
        try {
            transaction = userService.beginTransaction();

            userService.registerUser(user);

            Account account = new Account(user, accountType);

            user.addAccount(account);

//            user.getAccounts().add(account);
//            accountService.createAccount(user, account);

            userService.commitTransaction(transaction);
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                userService.rollbackTransaction(transaction);
            }
            throw new RuntimeException("Failed to register user and create account: " + e.getMessage(), e);
        }
    }

}