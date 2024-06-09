package com.transactionHub.transactionProcessor.mapper.transaction;

import com.transactionHub.transactionCoreLibrary.constant.AccountEnum;
import com.transactionHub.transactionProcessor.util.InvalidConfigException;

public record TransactionMapperConfig(
        String dateHeader,
        String descriptionHeader,
        String withdrawalHeader,
        String depositHeader,
        String balanceHeader,
        AccountEnum account,
        String datePattern
) {
    public TransactionMapperConfig {
        isNullOrEmpty(dateHeader);
        isNullOrEmpty(descriptionHeader);
        isNullOrEmpty(withdrawalHeader);
        isNullOrEmpty(depositHeader);
        isNullOrEmpty(balanceHeader);
        if (account == null) {
            throw new InvalidConfigException("Blank or null field");
        }
        isNullOrEmpty(datePattern);

    }

    private void isNullOrEmpty(String field) {
        if (field == null || field.isBlank()) {
            throw new InvalidConfigException("Blank or null field");
        }
    }


}
