package br.com.compass.service;

import br.com.compass.entity.User;
import br.com.compass.repository.UserRepository;
import br.com.compass.service.validation.CPFValidator;
import br.com.compass.service.validation.PasswordValidator;

import java.util.Optional;

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
        cpfValidator.validate(user.getCpf());
        passwordValidator.validate(user.getPassword());
        userRepository.save(user);
        System.out.println("User successfully registered!");
    }

    public Optional<User> loginUser(String cpf, String password) {
        Optional<User> user = userRepository.findByCpf(cpf);
        if (user.isEmpty()) {
            System.out.println("User not found!");
            return Optional.empty();
        }

        passwordValidator.validate(password);
        if (user.get().getPassword().equals(password)) {
            System.out.println("Successful login!");
            return user;
        } else {
            System.out.println("Incorrect password!");
            return Optional.empty();
        }
    }

    public void close() {
        userRepository.close();
    }
}