package com.transactionHub.transactionProcessor.modifier;

import com.transactionHub.transactionCoreLibrary.constant.TagConstant;
import com.transactionHub.transactionCoreLibrary.constant.TagType;
import com.transactionHub.transactionCoreLibrary.domain.Transaction;
import com.transactionHub.transactionCoreLibrary.util.TagBuilder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class RuleBasedTagger implements Consumer<Transaction> {
    private final Map<String, Map<String, Set<String>>> rules;

    public RuleBasedTagger(Map<String, Map<String, Set<String>>> rules) {
        this.rules = rules;
    }

    @Override
    public void accept(Transaction transaction) {
        String description = transaction.getDescription().trim();
        HashSet<String> tags = transaction.getTags() == null ? new HashSet<>() : new HashSet<>(transaction.getTags());

        for (var typePair : rules.entrySet()) {
            for (var offsetRule : typePair.getValue().entrySet()) {
                for (String occurrence : offsetRule.getValue()) {
                    if (description.contains(occurrence)) {
                        String newTag = TagConstant.SYS + ":" + typePair.getKey() + ":" + offsetRule.getKey();
                        tags.add(newTag);
                    }
                }
            }
        }
        transaction.setTags(tags);
    }
}
