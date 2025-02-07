package br.com.compass.service;

import br.com.compass.exception.InvalidAccountException;
import br.com.compass.model.entity.Account;
import br.com.compass.model.entity.User;
import br.com.compass.model.entity.enums.AccountType;
import br.com.compass.exception.DuplicateAccountException;
import br.com.compass.exception.UserNotFoundException;
import br.com.compass.model.repository.UserRepository;
import br.com.compass.service.validation.CPFValidator;
import br.com.compass.service.validation.PasswordValidator;
import jakarta.persistence.EntityTransaction;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserService(UserRepository userRepository, CPFValidator cpfValidator, PasswordValidator passwordValidator) {
        this.userRepository = userRepository;
    }

    public Optional<User> findUserByCpf(String cpf) {
        return userRepository.findByCpf(cpf);
    }

    public void registerUser(User user) {
        CPFValidator.validate(user.getCpf());
        PasswordValidator.validate(user.getPassword());
        if (userRepository.findByCpf(user.getCpf()).isPresent()) {
            throw new IllegalArgumentException("CPF already registered.");
        }
        userRepository.save(user);
        System.out.println("User successfully registered!");
    }

    public void updateUser(User user) {
        if (userRepository.findById(User.class, user.getId()).isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        userRepository.save(user);
    }

    public User getUserById(Integer userId) {
        return userRepository.findById(User.class, userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public Optional<User> getUserByCpf(String cpf) {
        return userRepository.findByCpf(cpf);
    }

    public Account createAccount(User user, AccountType accountType) throws DuplicateAccountException {
        Set<AccountType> existingAccountTypes = getUserAccountTypes(user.getCpf());
        if (existingAccountTypes.contains(accountType)) {
            throw new DuplicateAccountException("You already have a " + accountType + " account.");
        }

        Account account = new Account(user, accountType);
        user.addAccount(account);
        userRepository.save(user);
        return account;
    }

    public Set<AccountType> getAvailableAccountTypes(String cpf) {
        Optional<User> userOptional = userRepository.findByCpf(cpf);

        if (userOptional.isEmpty()) {
            return Set.of(AccountType.values());
        }
        User user = userOptional.get();
        Set<AccountType> existingAccountTypes = user.getAccounts().stream()
                .map(Account::getAccountType)
                .collect(Collectors.toSet());

        return Arrays.stream(AccountType.values())
                .filter(type -> !existingAccountTypes.contains(type))
                .collect(Collectors.toSet());
    }

    public Set<AccountType> getUserAccountTypes(String cpf) {
        User user = userRepository.findByCpf(cpf)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        if (user.getAccounts() == null || user.getAccounts().isEmpty()) {
            throw new InvalidAccountException("User does not have any accounts.");
        }

        return user.getAccounts().stream()
                .map(Account::getAccountType)
                .collect(Collectors.toSet());
    }

    public EntityTransaction beginTransaction() {
        return userRepository.beginTransaction();
    }

    public void commitTransaction(EntityTransaction transaction) {
        userRepository.commitTransaction(transaction);
    }

    public void rollbackTransaction(EntityTransaction transaction) {
        userRepository.rollbackTransaction(transaction);
    }


    public void close() {
        userRepository.close();
    }
}