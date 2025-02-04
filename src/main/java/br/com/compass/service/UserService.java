package br.com.compass.service;

import br.com.compass.entity.User;
import br.com.compass.repository.UserRepository;
import br.com.compass.service.validation.CPFValidator;
import br.com.compass.service.validation.PasswordValidator;

public class UserService {
    private UserRepository userRepository;
    private CPFValidator cpfValidator;
    private PasswordValidator passwordValidator;

    public UserService(UserRepository userRepository, CPFValidator cpfValidator, PasswordValidator passwordValidator) {
        this.userRepository = userRepository;
        this.cpfValidator = cpfValidator;
        this.passwordValidator = passwordValidator;
    }

    public void registerUser(User user) {
        cpfValidator.validate(user.getCpf());
        passwordValidator.validate(user.getPassword());
        userRepository.save(user);
        System.out.println("User successfully registered!");
    }

    public void updateUser(User user) {
        if (userRepository.findById(User.class, user.getId()).isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        userRepository.save(user);
    }

    public User getUserById(Integer userId) {
        return userRepository.findById(User.class, userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public void close() {
        userRepository.close();
    }
}