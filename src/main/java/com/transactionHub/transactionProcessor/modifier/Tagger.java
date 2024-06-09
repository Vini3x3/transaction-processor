package com.transactionHub.transactionProcessor.modifier;

import com.transactionHub.transactionCoreLibrary.domain.Transaction;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class Tagger implements Consumer<Transaction> {
    private final Map<String, Set<String>> rules;

    public Tagger(Map<String, Set<String>> rules) {
        this.rules = rules;
    }

    @Override
    public void accept(Transaction transaction) {
        String description = transaction.getDescription().trim();
        HashSet<String> tags = transaction.getTags() == null ? new HashSet<>() : new HashSet<>(transaction.getTags());
        for (Map.Entry<String, Set<String>> rule : rules.entrySet()) {
            if (description.contains(rule.getKey())) {
                tags.addAll(rule.getValue());
            }
        }
        transaction.setTags(tags);
    }
}
