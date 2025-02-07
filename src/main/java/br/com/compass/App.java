package br.com.compass;

import br.com.compass.controller.AccountController;
import br.com.compass.controller.UserController;
import br.com.compass.model.repository.UserRepository;
import br.com.compass.service.AccountService;
import br.com.compass.service.AuthService;
import br.com.compass.service.UserService;
import br.com.compass.service.validation.CPFValidator;
import br.com.compass.service.validation.PasswordValidator;
import br.com.compass.util.HibernateUtil;
import br.com.compass.view.UserView;

import java.util.Locale;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        Scanner scanner = new Scanner(System.in);

        UserRepository userRepository = new UserRepository();
        CPFValidator cpfValidator = new CPFValidator();
        PasswordValidator passwordValidator = new PasswordValidator();
        AccountService accountService = new AccountService();

        AuthService authService = new AuthService(userRepository);
        UserService userService = new UserService(userRepository, cpfValidator, passwordValidator);
        AccountController accountController = new AccountController(accountService);
        UserController userController = new UserController(authService, userService, accountController);
        UserView userView = new UserView(userController, scanner);

        try {
            userView.showUserMenu();
        } finally {
            scanner.close();
            HibernateUtil.closeEntityManagerFactory();
            System.out.println("Application closed.");
        }
    }
}