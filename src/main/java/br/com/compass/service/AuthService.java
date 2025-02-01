package br.com.compass.service;

import br.com.compass.entity.User;
import br.com.compass.repository.UserRepository;
import br.com.compass.service.validation.CPFValidator;
import br.com.compass.service.validation.PasswordValidator;

public class AuthService {

    private CPFValidator cpfValidator;
    private PasswordValidator passwordValidator;
    private UserRepository userRepository;

    public AuthService(CPFValidator cpfValidator, PasswordValidator passwordValidator, UserRepository userRepository) {
        this.cpfValidator = cpfValidator;
        this.passwordValidator = passwordValidator;
        this.userRepository = userRepository;
    }

    public void registerUser(User user) {
        validateUser(user.getCpf(), user.getPassword());
        userRepository.save(user);
        System.out.println("User successfully registered!");
    }

    public void loginUser(String cpf, String password) {
        User user = userRepository.findByCpf(cpf);
        if (user == null) {
            System.out.println("User not found!");
            return;
        }

        passwordValidator.validate(password);
        if (user.getPassword().equals(password)) {
            System.out.println("Login bem-sucedido!");
        } else {
            System.out.println("Senha incorreta!");
        }
    }

    private void validateUser(User user, String password) {
        cpfValidator.validate(user.getCpf());
        passwordValidator.validate(password);
    }
}
