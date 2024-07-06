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

public class OffsetValidatorTest {

    @ParameterizedTest(name = "{3}")
    @MethodSource("transactionList")
    void testValidate(Transaction current, Transaction next, boolean invalid, String displayName) {
        if (invalid) {
            Assertions.assertThatThrownBy(() -> new OffsetSortedValidator().accept(current, next))
                    .isInstanceOf(RuleViolateException.class);
        } else {
            Assertions.assertThatNoException().isThrownBy(() -> new OffsetSortedValidator().accept(current, next));
        }
    }

    public static Stream<Arguments> transactionList() {
        return Stream.of(
                Arguments.of(
                        new Transaction(new DateTime(2024,1,15,0,0), 0, AccountEnum.BOC, "", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                        new Transaction(new DateTime(2024,1,15,0,0), 0, AccountEnum.BOC, "", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                        true, "same date invalid offset"),
                Arguments.of(
                        new Transaction(new DateTime(2024,1,15,0,0), 0, AccountEnum.BOC, "", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                        new Transaction(new DateTime(2024,1,17,0,0), 1, AccountEnum.BOC, "", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                        true, "different date increasing offset"),
                Arguments.of(
                        new Transaction(new DateTime(2024,1,15,0,0), 0, AccountEnum.BOC, "", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                        new Transaction(new DateTime(2024,1,15,0,0), 1, AccountEnum.BOC, "", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                        false, "same date increasing offset"),
                Arguments.of(
                        new Transaction(new DateTime(2024,1,15,0,0), 0, AccountEnum.BOC, "", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                        new Transaction(new DateTime(2024,1,17,0,0), 0, AccountEnum.BOC, "", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                        false, "different reset offset")
        );
    }
}
