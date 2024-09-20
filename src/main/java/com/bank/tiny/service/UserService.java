package com.bank.tiny.service;

import com.bank.tiny.repostiroty.UserRepository;
import com.bank.tiny.view.auth.Role;
import com.bank.tiny.domain.BankUser;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserDetailsManager detailsManager;
    private final AuthenticationContext authContext;
    private final AccountService accountService;
    private final UserRepository userRepository;

    /**
     * Parametrized constructor.
     * @param detailsManager {@link UserDetailsManager} instance
     * @param authContext {@link AuthenticationContext} instance
     * @param accountService {@link AccountService} instance
     * @param userRepository {@link UserRepository} instance
     */
    public UserService(UserDetailsManager detailsManager, AuthenticationContext authContext,
                       AccountService accountService, UserRepository userRepository) {
        this.detailsManager = detailsManager;
        this.authContext = authContext;
        this.accountService = accountService;
        this.userRepository = userRepository;
    }

    public BankUser createUser(BankUser bankUser) {
        if (userRepository.find(bankUser.getUsername()) != null) {
            return null;
        }
        UserDetails user = User.withUsername(bankUser.getUsername())
                .password("{noop}" + bankUser.getPassword())
                .roles(Role.USER.name())
                .build();
        userRepository.createUser(bankUser);
        detailsManager.createUser(user);
        accountService.createAccount(bankUser.getUsername());
        return bankUser;
    }

    public BankUser getCurrentUser() {
        return authContext.getAuthenticatedUser(UserDetails.class)
                .map(user -> getUser(user.getUsername()))
                .orElseThrow();
    }

    public BankUser getUser(String username) {
        return userRepository.find(username);
    }

    public boolean userExists(String username) {
        BankUser bankUser = userRepository.find(username);
        return bankUser != null && bankUser.isActive();
    }

    public void deactivateUser(String username) {
        BankUser bankUser = userRepository.find(username);
        bankUser.setActive(false);
        accountService.deactivate(username);
        deactivateAuthentication(username, bankUser);
    }

    private void deactivateAuthentication(String username, BankUser bankUser) {
        detailsManager.deleteUser(username);
        UserDetails user = User.withUsername(bankUser.getUsername())
                .password("{noop}" + bankUser.getPassword())
                .roles(Role.USER.name())
                .disabled(true)
                .build();
        detailsManager.createUser(user);
    }
}
