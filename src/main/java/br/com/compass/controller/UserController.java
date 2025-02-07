package br.com.compass.controller;

import br.com.compass.entity.Account;
import br.com.compass.entity.User;
import br.com.compass.entity.enums.AccountType;
import br.com.compass.exception.DuplicateAccountException;
import br.com.compass.exception.IncorrectPasswordException;
import br.com.compass.exception.UserNotFoundException;
import br.com.compass.service.AuthService;
import br.com.compass.service.UserService;

import java.time.LocalDate;
import java.util.Set;

public class UserController {
    private final AuthService authService;
    private final UserService userService;
    private final AccountController accountController;

    public UserController(AuthService authService, UserService userService, AccountController accountController) {
        this.authService = authService;
        this.userService = userService;
        this.accountController = accountController;
    }

    public void registerUser(String name, String birthDateStr, String cpf, String phoneNumber, String password) {
        LocalDate birthDate = LocalDate.parse(birthDateStr, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        User user = new User(name, birthDate, cpf, phoneNumber, password);
        userService.registerUser(user);
    }


    public User loginUser(String cpf, String password) throws UserNotFoundException, IncorrectPasswordException {
        return authService.loginUser(cpf, password);
    }

    public Account createAccount(User user, AccountType accountType) throws DuplicateAccountException {
        return userService.createAccount(user, accountType);
    }

    public Set<AccountType> getUserAccountTypes(String cpf) {
        return userService.getUserAccountTypes(cpf);
    }

    public AccountController getAccountController() {
        return accountController;
    }
}