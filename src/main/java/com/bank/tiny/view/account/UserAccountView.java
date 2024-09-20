package com.bank.tiny.view.account;

import com.bank.tiny.MainView;
import com.bank.tiny.domain.Transaction;
import com.bank.tiny.service.AccountService;
import com.bank.tiny.repostiroty.TransactionHistoryRepository;
import com.bank.tiny.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;

import java.math.BigDecimal;

@PermitAll
@Route(value = "account", layout = MainView.class)
@PageTitle("User Account")
public class UserAccountView extends VerticalLayout {

    private static final String FIELD_WIDTH = "150px";
    private final H2 balance;
    private final UserService userService;
    private final AccountService accountService;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final AuthenticationContext authContext;
    private final String username;
    private Grid<Transaction> transactionsGrid;

    /**
     * Creates user account view.
     * @param userService {@link UserService} instance
     * @param accountService {@link AccountService} instance
     * @param transactionHistoryRepository {@link TransactionHistoryRepository} instance
     * @param authContext {@link AuthenticationContext} instance
     */
    public UserAccountView(UserService userService, AccountService accountService,
                           TransactionHistoryRepository transactionHistoryRepository, AuthenticationContext authContext) {
        this.accountService = accountService;
        this.userService = userService;
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.authContext = authContext;
        setHeightFull();
        setAlignItems(Alignment.CENTER);

        username = userService.getCurrentUser().getUsername();
        H2 balanceMessage = new H2("Your current balance:");
        balance = new H2(String.valueOf(accountService.getBalance(username)));
        Span operationsMsg = new Span("You can deposit, withdraw or transfer money to another user");

        initGrid();

        add(new HorizontalLayout(balanceMessage, balance), operationsMsg, createDepositLayout(), createWithdrawLayout(),
                createTransferLayout(), createDeactivateButton(), createGridControlLayout(), transactionsGrid);
    }
    private HorizontalLayout createDepositLayout() {
        BigDecimalField depositField = new BigDecimalField("Amount to deposit:");
        depositField.setWidth(FIELD_WIDTH);
        Button depositButton = new Button("Deposit", click -> deposit(username, depositField.getValue()));
        depositButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout layout = new HorizontalLayout(depositField, depositButton);
        layout.setAlignItems(Alignment.END);
        return layout;
    }

    private void deposit(String username, BigDecimal amount) {
        if (checkAmountValue(amount)) {
            return;
        }
        accountService.deposit(username, amount);
        balance.setText(String.valueOf(accountService.getBalance(username)));
        Notification.show(String.format("[%s] was deposited into the account", amount))
                .setPosition(Notification.Position.MIDDLE);
        transactionsGrid.getDataProvider().refreshAll();
    }

    private HorizontalLayout createWithdrawLayout() {
        BigDecimalField withdrawField = new BigDecimalField("Amount to withdraw:");
        withdrawField.setWidth(FIELD_WIDTH);
        Button withdrawButton = new Button("Withdraw", click -> withdraw(withdrawField.getValue()));
        withdrawButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout layout = new HorizontalLayout(withdrawField, withdrawButton);
        layout.setAlignItems(Alignment.END);
        return layout;
    }

    private void withdraw(BigDecimal amount) {
        if (checkAmountValue(amount)) {
            return;
        }
        BigDecimal withdrawValue = accountService.withdraw(username, amount);
        balance.setText(String.valueOf(accountService.getBalance(username)));
        Notification.show(String.format("[%s] was withdrawn from the account", withdrawValue))
                .setPosition(Notification.Position.MIDDLE);
        transactionsGrid.getDataProvider().refreshAll();
    }

    private HorizontalLayout createTransferLayout() {
        BigDecimalField transferField = new BigDecimalField("Amount to transfer:");
        transferField.setWidth(FIELD_WIDTH);
        TextField userNameToTransfer = new TextField("Username to transfer:");
        userNameToTransfer.setWidth(FIELD_WIDTH);
        Button transferButton = new Button("Transfer",
                click -> transfer(transferField.getValue(), userNameToTransfer.getValue()));
        transferButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout layout = new HorizontalLayout(transferField, userNameToTransfer, transferButton);
        layout.setAlignItems(Alignment.END);
        return layout;
    }

    private void transfer(BigDecimal amount, String userToTransfer) {
        if (!userService.userExists(userToTransfer)) {
            Notification.show(String.format("User [%s] does not exists or active", userToTransfer))
                    .setPosition(Notification.Position.MIDDLE);
            return;
        }
        if (checkAmountValue(amount)) {
            return;
        }
        BigDecimal result = accountService.transfer(username, userToTransfer, amount);
        balance.setText(String.valueOf(accountService.getBalance(username)));
        Notification.show(String.format("[%s] was transferred to the [%s] user", result, userToTransfer))
                .setPosition(Notification.Position.MIDDLE);
        transactionsGrid.getDataProvider().refreshAll();
    }

    private void initGrid() {
        transactionsGrid = new Grid<>(Transaction.class, false);
        transactionsGrid.setId("transactionsGrid");
        transactionsGrid.addColumn(Transaction::amount).setHeader("Amount");
        transactionsGrid.addColumn(Transaction::transactionType).setHeader("Transaction type");
        transactionsGrid.addColumn(Transaction::user).setHeader("User");
        transactionsGrid.setItems(transactionHistoryRepository.getTransactions(username));
        transactionsGrid.setSizeFull();
    }

    private Button createDeactivateButton() {
        ConfirmDialog confirmDialog = new ConfirmDialog("Are you sure?",
                "You are about to deactivate your account. Continue?", "Confirm", confirm -> deactivateUser());
        confirmDialog.setCancelable(true);
        Button deactivateButton = new Button("Deactivate account", click -> confirmDialog.open());
        deactivateButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        return deactivateButton;
    }

    private void deactivateUser() {
        userService.deactivateUser(username);
        authContext.logout();
    }

    private boolean checkAmountValue(BigDecimal depositValue) {
        if (!bigDecimalValidator(depositValue)) {
            Notification.show("Wrong amount value! It must be positive with a maximum value of 2 digits after" +
                            " the comma.").setPosition(Notification.Position.MIDDLE);
            return true;
        }
        return false;
    }

    private boolean bigDecimalValidator(BigDecimal bigDecimal) {
        return bigDecimal.compareTo(BigDecimal.ZERO) > 0 && bigDecimal.scale() < 3;
    }

    private HorizontalLayout createGridControlLayout() {
        NativeLabel gridLabel = new NativeLabel("Transactions");
        gridLabel.setFor(transactionsGrid);
        Button refreshButton = new Button(VaadinIcon.REFRESH.create(),
                click -> transactionsGrid.getDataProvider().refreshAll());
        refreshButton.setHeight("25px");
        refreshButton.setWidth("25px");
        HorizontalLayout horizontalLayout = new HorizontalLayout(refreshButton, gridLabel);
        horizontalLayout.setAlignItems(Alignment.END);
        setAlignSelf(Alignment.START, horizontalLayout);
        return horizontalLayout;
    }
}
