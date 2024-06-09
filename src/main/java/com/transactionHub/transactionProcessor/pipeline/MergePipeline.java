package com.transactionHub.transactionProcessor.pipeline;

import com.transactionHub.transactionCoreLibrary.domain.Transaction;
import com.transactionHub.transactionProcessor.validator.RuleViolateException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MergePipeline {

    // Assume the importTransactions and virtualTransactions are in order
    // only tags and meta are merged.
    public List<Transaction> mergeData(List<Transaction> importTransactions, List<Transaction> virtualTransactions) throws RuleViolateException {

        if (importTransactions.isEmpty() || virtualTransactions.isEmpty()) {
            return importTransactions;
        }

        int i = 0;

        for (Transaction transaction: importTransactions) {
            if (isMatch(transaction, virtualTransactions.get(i))) {
                mergeTags(transaction, virtualTransactions.get(i));
                mergeMeta(transaction, virtualTransactions.get(i));
                i++;
            }
            if (i >= virtualTransactions.size()) {
                break;
            }
        }

        if (i != virtualTransactions.size()) {
            throw new RuleViolateException("Merge failed - virtual transaction left unmerged");
        }

        return importTransactions;
    }

    private boolean isMatch(Transaction t1, Transaction t2) {
        return t1.getDate().compareTo(t2.getDate()) == 0
                && t1.getAccount().equals(t2.getAccount())
                && t1.getDeposit().compareTo(t2.getDeposit()) == 0
                && t1.getWithdrawal().compareTo(t2.getWithdrawal()) == 0;
    }

    private void mergeTags(Transaction transaction, Transaction reference) {
        HashSet<String> tags = transaction.getTags() == null ? new HashSet<>() : new HashSet<>(transaction.getTags());
        tags.addAll(reference.getTags());
        transaction.setTags(tags);
    }

    private void mergeMeta(Transaction transaction, Transaction reference) {
        HashMap<String, String> metas = transaction.getMeta() == null ? new HashMap<>() : new HashMap<>(transaction.getMeta());
        if (reference.getMeta() != null) {
            metas.putAll(reference.getMeta());
        }
        transaction.setMeta(metas);
    }

}
