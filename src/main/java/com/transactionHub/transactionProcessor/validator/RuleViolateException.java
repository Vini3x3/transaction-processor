package com.transactionHub.transactionProcessor.validator;

public class RuleViolateException extends RuntimeException {
    public RuleViolateException(String message) {
        super("Rule Violation Exception: " + message);
    }
}
