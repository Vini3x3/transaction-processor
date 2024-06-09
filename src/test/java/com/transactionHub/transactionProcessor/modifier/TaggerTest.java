package com.transactionHub.transactionProcessor.modifier;

import com.transactionHub.transactionCoreLibrary.domain.Transaction;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

class TaggerTest {
    @Test
    void testTagging() {
        var transaction = new Transaction();
        transaction.setDescription("20240513 HK Electricity Company - Sales");

        var config = Map.of(
                "HK Electricity Company", Set.of("SCHEDULE", "SCHEDULE-1", "Util Expenses"),
                "China Mobile Ltd.", Set.of("SCHEDULE", "SCHEDULE-2", "Util Expenses"),
                "Sales", Set.of("Credit card transactions")
        );

        var tagger = new Tagger(config);

        Assertions.assertThatNoException().isThrownBy(() -> tagger.accept(transaction));

        Assertions.assertThat(transaction.getTags()).containsAll(Set.of(
                "SCHEDULE", "SCHEDULE-1", "Util Expenses", "Credit card transactions"
        ));


    }

}
