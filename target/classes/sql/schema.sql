CREATE DATABASE IF NOT EXISTS bank_challenge;
USE bank_challenge;

CREATE TABLE IF NOT EXISTS Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL CHECK (LENGTH(name) >= 2),
    birth_date DATE NOT NULL,
    cpf CHAR(11) UNIQUE NOT NULL,
    phone_number CHAR(15) NOT NULL,
    password CHAR(6) NOT NULL CHECK (password NOT REGEXP '^([0-9])\\1{5}$')
    );

CREATE TABLE IF NOT EXISTS Accounts (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    account_type ENUM('checking', 'salary', 'savings') NOT NULL,
    balance DECIMAL(10,2) DEFAULT 0.00 CHECK (balance >= 0),
    account_number CHAR(13) UNIQUE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    UNIQUE(user_id, account_type)
    );

CREATE TABLE IF NOT EXISTS Transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    transaction_type ENUM('withdrawal', 'deposit', 'transfer') NOT NULL,
    amount DECIMAL(10,2) NOT NULL CHECK (amount > 0),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    transfer_destination_account_id INT NULL,
    FOREIGN KEY (account_id) REFERENCES Accounts(account_id) ON DELETE CASCADE,
    FOREIGN KEY (transfer_destination_account_id) REFERENCES Accounts(account_id)
    );
