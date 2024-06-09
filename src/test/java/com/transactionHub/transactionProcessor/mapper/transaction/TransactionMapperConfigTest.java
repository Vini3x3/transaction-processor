package com.transactionHub.transactionProcessor.mapper.transaction;

import com.transactionHub.transactionCoreLibrary.constant.AccountEnum;
import com.transactionHub.transactionProcessor.util.InvalidConfigException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class TransactionMapperConfigTest {

    @Test
    void testInvalidConfig() {

        Assertions.assertThatThrownBy(() -> new TransactionMapperConfig(null, "Desc", "Withdrawal", "Deposit", "Balance", AccountEnum.FORMAL, "yyyy/MM/dd"))
                .isInstanceOf(InvalidConfigException.class)
                .hasMessageContaining("Blank or null field");
    }
}
