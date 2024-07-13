package com.transactionHub.transactionProcessor.mapper.transaction;

import com.transactionHub.transactionCoreLibrary.constant.AccountEnum;
import com.transactionHub.transactionCoreLibrary.constant.TransactionMeta;
import com.transactionHub.transactionProcessor.TestUtil;
import org.assertj.core.api.Assertions;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

class TransactionMapperTest {

    @Test
    void testMapping() {
        TransactionMapper mapper = new TransactionMapper(
                "Date",
                "Transaction Details",
                "Withdrawal",
                "Deposit",
                "",
                "Balance in Original Currency",
                AccountEnum.BOC,
                "yyyy/MM/dd");

        var entry = new HashMap<String, Object>(Map.of(
                "Date", "2024/02/25",
                "Transaction Details", "Transfer FPS CHINA MOBILE HONG KONG COMPANY LIMITED 12240225F410234225",
                "Deposit", "",
                "Withdrawal", "88.00",
                "Balance in Original Currency", "3,912.30",
                TransactionMeta.IMPORT_LINE_NO, 0
        ));

        var transaction = mapper.map(entry);
        Assertions.assertThat(transaction.getDate()).isEqualTo(TestUtil.convertToInstant(new DateTime(2024, 2, 25, 0, 0, DateTimeZone.UTC)));
        Assertions.assertThat(transaction.getDescription()).isEqualTo("Transfer FPS CHINA MOBILE HONG KONG COMPANY LIMITED 12240225F410234225");
        Assertions.assertThat(transaction.getDeposit()).isEqualTo(new BigDecimal("0.00"));
        Assertions.assertThat(transaction.getWithdrawal()).isEqualTo(new BigDecimal("88.00"));
        Assertions.assertThat(transaction.getBalance()).isEqualTo(new BigDecimal("3912.30"));

    }

    @Test
    void testMapping_delta() {
        TransactionMapper mapper = new TransactionMapper(
                "Transaction Date",
                "Description",
                "",
                "",
                "Balance Change",
                "Balance Total",
                AccountEnum.HSBC,
                "yyyy-MM-dd");

        var entry = new HashMap<String, Object>(Map.of(
                "Transaction Date", "2024-02-25",
                "Description", "Transfer FPS CHINA MOBILE HONG KONG COMPANY LIMITED 12240225F410234225",
                "Balance Change", "-88.00",
                "Balance Total", "3,912.30",
                "Balance Currency", "HKD",
                TransactionMeta.IMPORT_LINE_NO, 0
        ));

        var transaction = mapper.map(entry);
        Assertions.assertThat(transaction.getDate()).isEqualTo(TestUtil.convertToInstant(new DateTime(2024, 2, 25, 0, 0, DateTimeZone.UTC)));
        Assertions.assertThat(transaction.getDescription()).isEqualTo("Transfer FPS CHINA MOBILE HONG KONG COMPANY LIMITED 12240225F410234225");
        Assertions.assertThat(transaction.getDeposit()).isEqualTo(new BigDecimal("0.00"));
        Assertions.assertThat(transaction.getWithdrawal()).isEqualTo(new BigDecimal("88.00"));
        Assertions.assertThat(transaction.getBalance()).isEqualTo(new BigDecimal("3912.30"));


    }

}
