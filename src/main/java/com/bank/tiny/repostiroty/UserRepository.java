package com.bank.tiny.repostiroty;

import com.bank.tiny.domain.BankUser;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepository {

    private final Map<String, BankUser> users = new HashMap<>();

    /**
     * Creates bank user.
     * @param bankUser bank user.
     */
    public void createUser(BankUser bankUser) {
        users.put(bankUser.getUsername(), bankUser);
    }

    /**
     * Gets bank user by username.
     * @param username username
     * @return bank user or {@code null} if user does not exist
     */
    public BankUser find(String username) {
        return users.get(username);
    }
}
