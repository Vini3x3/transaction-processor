package com.transactionHub.transactionProcessor.validator;

import com.transactionHub.transactionCoreLibrary.constant.AccountEnum;
import com.transactionHub.transactionCoreLibrary.domain.Transaction;
import org.assertj.core.api.Assertions;
import org.joda.time.DateTime;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

public class TransactionValidatorTest {

    @ParameterizedTest(name = "{3}")
    @MethodSource("transactionList")
    void testValidate(Transaction transaction, boolean invalid, String displayName) {
        if (invalid) {
            Assertions.assertThatThrownBy(() -> new TransactionValidator().accept(transaction))
                    .isInstanceOf(RuleViolateException.class);
        } else {
            Assertions.assertThatNoException().isThrownBy(() -> new TransactionValidator().accept(transaction));
        }
    }


    static Stream<Arguments> transactionList() {
        return Stream.of(
                Arguments.of(new Transaction(null, 0, AccountEnum.BOC, "placeholder", BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ONE, null, null), true, "no date"),
                Arguments.of(new Transaction(DateTime.now().toDate(), -2, AccountEnum.BOC, "placeholder", BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ONE, null, null), true, "negative offset"),
                Arguments.of(new Transaction(DateTime.now().toDate(), 3, null, "placeholder", BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ONE, null, null), true, "no account"),
                Arguments.of(new Transaction(DateTime.now().toDate(), 3, AccountEnum.BOC, "placeholder", BigDecimal.ONE, BigDecimal.ZERO, null, null, null), true, "no balance"),
                Arguments.of(new Transaction(DateTime.now().toDate(), 3, AccountEnum.BOC, "placeholder", BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ZERO, null, null), true, "deposit and withdraw together"),
                Arguments.of(new Transaction(DateTime.now().toDate(), 3, AccountEnum.BOC, "placeholder", BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ONE, null, null), false, "valid")
        );
    }
}
