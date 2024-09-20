package com.bank.tiny.service;

import com.bank.tiny.domain.Account;
import com.bank.tiny.domain.Transaction;
import com.bank.tiny.domain.TransactionType;
import com.bank.tiny.repostiroty.AccountRepository;
import com.bank.tiny.repostiroty.TransactionHistoryRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Service for working with bank accounts. Allows to deposit, withdraw or transfer money to another account,
 * as well as deactivate an account.
 */
@Service
public class AccountService {

    private final TransactionHistoryRepository transactionHistoryRepository;
    private final AccountRepository accountRepository;

    /**
     * Parametrized constructor.
     * @param transactionHistoryRepository {@link TransactionHistoryRepository} instance
     */
    public AccountService(AccountRepository accountRepository,
                          TransactionHistoryRepository transactionHistoryRepository) {
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * Creates bank account for the specified user.
     * @param username user for whom the account is created
     */
    public void createAccount(String username) {
        accountRepository.createAccount(username);
    }

    /**
     * Get current balance for user.
     * @param username user
     * @return balance for user
     */
    public BigDecimal getBalance(String username) {
        return accountRepository.getBalance(username);
    }

    /**
     * Deposits provided amount to bank account.
     * @param username user to whom the account is deposited
     * @param depositAmount deposit amount
     */
    public void deposit(String username, BigDecimal depositAmount) {
        Account account = accountRepository.find(username);
        account.setAmount(account.getAmount().add(depositAmount));
        transactionHistoryRepository.addTransaction(username, new Transaction(depositAmount, TransactionType.DEPOSIT, null));
    }

    /**
     * Withdraws provided amount from bank account. If the amount exceeds the account balance, only the account
     * balance is withdrawn.
     * @param username user whose money is being withdrawn
     * @param amount amount to withdraw
     * @return the actual withdrawn amount
     */
    public BigDecimal withdraw(String username, BigDecimal amount) {
        return withdraw(username, amount, null);
    }

    /**
     * Transfers money to another user account. If the amount exceeds the account balance, only the account
     * balance is transferred.
     * @param usernameFrom user whose money is being withdrawn
     * @param usernameTo user to whom the account is deposited
     * @param amount amount to withdraw/deposit
     * @return the actual withdrawn/deposited amount
     */
    public BigDecimal transfer(String usernameFrom, String usernameTo, BigDecimal amount) {
        BigDecimal result = withdraw(usernameFrom, amount, usernameTo);
        deposit(usernameTo, result, usernameFrom);
        return result;
    }

    /**
     * Deactivates account for the provided user.
     * @param username user
     */
    public void deactivate(String username) {
        accountRepository.find(username).setActive(false);
    }

    private void deposit(String username, BigDecimal depositAmount, String usernameFrom) {
        Account account = accountRepository.find(username);
        account.setAmount(account.getAmount().add(depositAmount));
        transactionHistoryRepository.addTransaction(username, new Transaction(depositAmount, TransactionType.DEPOSIT, usernameFrom));
    }

    private BigDecimal withdraw(String username, BigDecimal amount, String usernameTo) {
        Account account = accountRepository.find(username);
        BigDecimal currentAmount = account.getAmount();
        if (BigDecimal.ZERO.equals(currentAmount)) {
            return BigDecimal.ZERO;
        }
        if (currentAmount.compareTo(amount) <= 0) {
            account.setAmount(BigDecimal.ZERO);
            amount = currentAmount;
        } else {
            account.setAmount(currentAmount.subtract(amount));
        }
        transactionHistoryRepository.addTransaction(username, new Transaction(amount, TransactionType.WITHDRAW, usernameTo));
        return amount;
    }
}
