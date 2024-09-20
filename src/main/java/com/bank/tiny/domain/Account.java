package com.bank.tiny.domain;

import java.math.BigDecimal;

/**
 * Account domain class.
 */
public class Account {

    private BigDecimal amount = BigDecimal.ZERO;
    private boolean active = true;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
