package br.com.compass.controller;

import br.com.compass.entity.User;
import br.com.compass.exception.IncorrectPasswordException;
import br.com.compass.exception.UserNotFoundException;
import br.com.compass.service.AuthService;
import br.com.compass.service.UserService;

import java.time.LocalDate;

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

    public AccountController getAccountController() {
        return accountController;
    }
}