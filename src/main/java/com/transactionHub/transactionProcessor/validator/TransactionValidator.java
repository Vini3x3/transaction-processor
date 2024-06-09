package com.transactionHub.transactionProcessor.validator;

import com.transactionHub.transactionCoreLibrary.domain.Transaction;

import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TransactionValidator implements Consumer<Transaction> {
    @Override
    public void accept(Transaction transaction) {
        if (transaction.getBalance() == null) {
            throw new RuleViolateException("transaction has no balance");
        }
        if (transaction.getAccount() == null) {
            throw new RuleViolateException("transaction has no account");
        }
        if (transaction.getDate() == null) {
            throw new RuleViolateException("transaction has no date");
        }
        if (transaction.getOffset() == null || transaction.getOffset() < 0) {
            throw new RuleViolateException("transaction has invalid offset");
        }
        if (transaction.getDeposit() != null && transaction.getDeposit().compareTo(BigDecimal.ZERO) > 0
                && transaction.getWithdrawal() != null && transaction.getWithdrawal().compareTo(BigDecimal.ZERO) > 0
        ) {
            throw new RuleViolateException("transaction has both deposit and withdrawal");
        }
    }
}
