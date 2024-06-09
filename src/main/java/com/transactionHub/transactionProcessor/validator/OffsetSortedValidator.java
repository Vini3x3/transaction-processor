package com.transactionHub.transactionProcessor.validator;

import com.transactionHub.transactionCoreLibrary.domain.Transaction;

import java.util.function.BiConsumer;

public class OffsetSortedValidator implements BiConsumer<Transaction, Transaction> {

    @Override
    public void accept(Transaction current, Transaction next) {
        if (current.getDate().compareTo(next.getDate()) == 0) {
            if (!next.getOffset().equals(current.getOffset() + 1)) {
                throw new RuleViolateException("offset is not incremented in same date");
            }
        } else {
            if (next.getOffset() != 0) {
                throw new RuleViolateException("offset is not reset in new date");
            }
        }
    }
}
