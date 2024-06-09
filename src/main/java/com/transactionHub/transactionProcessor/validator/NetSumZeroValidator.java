package com.transactionHub.transactionProcessor.validator;

import com.transactionHub.transactionCoreLibrary.domain.Transaction;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class NetSumZeroValidator implements BiConsumer<Transaction, Transaction> {
    @Override
    public void accept(Transaction current, Transaction next) {
        if (current.getBalance()
                .add(next.getDeposit())
                .subtract(next.getWithdrawal())
                .compareTo(next.getBalance()) != 0) {
            throw new RuleViolateException("net sum is not zero");
        }
    }
}
