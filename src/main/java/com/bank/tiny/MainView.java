package com.bank.tiny;

import com.bank.tiny.view.auth.LoginView;
import com.bank.tiny.view.auth.RegisterView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * The main view contains login and registration buttons.
 */
@Route("")
@AnonymousAllowed
public class MainView extends AppLayout {

    private final transient AuthenticationContext authContext;

    /**
     * Creates main view.
     * @param authContext {@link AuthenticationContext} instance
     */
    public MainView(AuthenticationContext authContext) {
        this.authContext = authContext;
        addNavBar();

        Span greeting = new Span("Please, login to get access to Tiny Bank functionality!");
        Button loginButton = new Button("Login", e -> UI.getCurrent().navigate(LoginView.class));
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button registerButton = new Button("Register", e -> UI.getCurrent().navigate(RegisterView.class));
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.addClickShortcut(Key.ENTER);

        VerticalLayout verticalLayout = new VerticalLayout(greeting, loginButton, registerButton);
        verticalLayout.setSizeFull();
        verticalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        setContent(verticalLayout);
    }

    private void addNavBar() {
        H1 logo = new H1("Tiny Bank Web Application");
        logo.addClassName("logo");
        HorizontalLayout header = authContext.getAuthenticatedUser(UserDetails.class)
                        .map(user -> getUserHeaderLayout(user, logo)).orElseGet(() -> new HorizontalLayout(logo));
        addToNavbar(header);
    }

    private HorizontalLayout getUserHeaderLayout(UserDetails user, H1 logo) {
        Button logout = new Button("Logout", click -> this.authContext.logout());
        H2 loggedUser = new H2("Welcome " + user.getUsername());
        return new HorizontalLayout(logo, loggedUser, logout);
    }
}
