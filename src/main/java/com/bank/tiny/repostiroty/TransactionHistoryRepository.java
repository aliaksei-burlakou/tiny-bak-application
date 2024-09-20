package com.bank.tiny.repostiroty;

import com.bank.tiny.domain.Transaction;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TransactionHistoryRepository {

    private final Map<String, List<Transaction>> userTransactions = new HashMap<>();

    /**
     * Add transaction to history.
     * @param username user
     * @param transaction {@link Transaction} instance
     */
    public void addTransaction(String username, Transaction transaction) {
        List<Transaction> transactions = userTransactions.computeIfAbsent(username, k -> new ArrayList<>());
        transactions.add(transaction);
    }

    /**
     * Gets transaction history.
     * @param username user
     * @return transaction history
     */
    public List<Transaction> getTransactions(String username) {
        return userTransactions.computeIfAbsent(username, k -> new ArrayList<>());
    }
}
