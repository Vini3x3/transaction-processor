package com.transactionHub.transactionProcessor.modifier;

import com.transactionHub.transactionCoreLibrary.domain.Transaction;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class AdjustOffsetModifier implements Consumer<List<Transaction>> {
    @Override
    public void accept(List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            return;
        }

        // init first transaction
        int currentOffset = transactions.getFirst().getOffset();
        var currentDate = transactions.getFirst().getDate();

        // assume offset is strictly increasing by 1
        for (var transaction: transactions.subList(1, transactions.size())) {
            if (transaction.getDate().compareTo(currentDate) != 0) {
                currentOffset = transaction.getOffset();
                currentDate = transaction.getDate();
            }
            transaction.setOffset(transaction.getOffset() - currentOffset);
        }
    }
}
