package com.transactionHub.transactionProcessor.mapper;

public class MapperException extends RuntimeException {
    public MapperException(String message) {
        super("mapper Exception: " + message);
    }
}
