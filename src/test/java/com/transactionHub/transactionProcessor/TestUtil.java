package com.transactionHub.transactionProcessor;

import org.joda.time.DateTime;

import java.time.Instant;

public class TestUtil {

    public static Instant convertToInstant(DateTime dateTime) {
        return Instant.ofEpochMilli(dateTime.getMillis());
    }
}
