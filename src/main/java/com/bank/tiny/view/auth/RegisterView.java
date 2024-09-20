package com.bank.tiny.view.auth;

import com.bank.tiny.MainView;
import com.bank.tiny.domain.BankUser;
import com.bank.tiny.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Register view. Allows the user to create an account.
 */
@Route("register")
@PageTitle("Register")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {

    private static final int USERNAME_MAX_LENGTH = 256;
    private static final int PASSWORD_MIN_LENGTH = 4;

    private final UserService userService;
    private final Binder<BankUser> bankUserBinder = new Binder<>();

    /**
     * Creates register view.
     * @param userService {@link UserService} instance
     */
    public RegisterView(@Autowired UserService userService) {
        this.userService = userService;
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        bankUserBinder.setBean(new BankUser());

        FormLayout formLayout = initFormlayout();

        Button registerButton = new Button("Register", click -> addUserAndRedirect());
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout horizontalLayout = new HorizontalLayout(formLayout);
        horizontalLayout.setWidth("400px");

        add(new H1("Tiny Bank Application"), new H2("Registration"), horizontalLayout, registerButton,
                new Button("Return to main page", click -> UI.getCurrent().navigate(MainView.class)));
    }

    private FormLayout initFormlayout() {
        TextField username = new TextField("Please enter your username:");
        username.setMaxLength(USERNAME_MAX_LENGTH);
        bankUserBinder.forField(username)
                .asRequired()
                .bind(BankUser::getUsername, BankUser::setUsername);
        PasswordField passwordField = new PasswordField("Please enter your password:");
        passwordField.setMinLength(PASSWORD_MIN_LENGTH);
        passwordField.setHelperText(PASSWORD_MIN_LENGTH + " characters minimum");
        bankUserBinder.forField(passwordField)
                .asRequired()
                .bind(BankUser::getPassword, BankUser::setPassword);

        FormLayout formLayout = new FormLayout();
        formLayout.add(username, passwordField);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        return formLayout;
    }

    private void addUserAndRedirect() {
        if (bankUserBinder.validate().hasErrors()) {
            return;
        }
        if (userService.createUser(bankUserBinder.getBean()) == null) {
            Notification.show("User with this username already exists").setPosition(Notification.Position.MIDDLE);
            return;
        }
        UI.getCurrent().navigate(MainView.class);
    }
}
