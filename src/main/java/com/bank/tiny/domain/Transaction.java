package com.bank.tiny.domain;

import java.math.BigDecimal;

public record Transaction(BigDecimal amount, TransactionType transactionType, String user) {
}
