package com.transactionHub.transactionProcessor.extractor;

public class ExtractorException extends RuntimeException {
    public ExtractorException(String message) {
        super("Parse File Exception: " + message);
    }
}