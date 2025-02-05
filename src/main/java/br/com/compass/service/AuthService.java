package br.com.compass.service;

import br.com.compass.entity.User;
import br.com.compass.exception.IncorrectPasswordException;
import br.com.compass.exception.UserNotFoundException;
import br.com.compass.repository.UserRepository;
import br.com.compass.service.validation.CPFValidator;
import br.com.compass.service.validation.PasswordValidator;

import java.util.Optional;

public class AuthService {

    private CPFValidator cpfValidator;
    private PasswordValidator passwordValidator;
    private UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public User loginUser(String cpf, String password) {
        Optional<User> user = userRepository.findByCpf(cpf);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found!");
        }

        passwordValidator.validate(password);
        if (!user.get().getPassword().equals(password)) {
            throw new IncorrectPasswordException("Incorrect password!");
        }

        System.out.println("Successful login!");
        return user.get();
    }

    public void close() {
        userRepository.close();
    }
}