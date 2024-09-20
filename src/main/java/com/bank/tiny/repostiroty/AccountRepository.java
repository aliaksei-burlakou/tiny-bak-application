package com.bank.tiny.repostiroty;

import com.bank.tiny.domain.Account;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for working with bank accounts. Allows to deposit, withdraw or transfer money to another account,
 * as well as deactivate an account.
 */
@Service
public class AccountRepository {

    private final Map<String, Account> accounts = new HashMap<>();

    /**
     * Creates bank account for the specified user.
     * @param username user for whom the account is created
     */
    public void createAccount(String username) {
        accounts.put(username, new Account());
    }

    /**
     * Gets bank account for the specified user.
     * @param username user
     */
    public Account find(String username) {
        return accounts.get(username);
    }

    /**
     * Get current balance for user.
     * @param username user
     * @return balance for user
     */
    public BigDecimal getBalance(String username) {
        return accounts.get(username).getAmount();
    }
}
