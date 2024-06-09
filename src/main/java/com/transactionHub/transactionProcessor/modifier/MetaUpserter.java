package com.transactionHub.transactionProcessor.modifier;

import com.transactionHub.transactionCoreLibrary.domain.Transaction;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MetaUpserter implements Consumer<Transaction> {

    private final Map<String, String> fields;

    public MetaUpserter(Map<String, String> fields) {
        this.fields = fields;
    }

    @Override
    public void accept(Transaction transaction) {
        var originalMeta = transaction.getMeta();
        Map<String, String> metaData = originalMeta == null ? new HashMap<>() : new HashMap<>(originalMeta);
        metaData.putAll(fields);
        transaction.setMeta(metaData);
    }
}
