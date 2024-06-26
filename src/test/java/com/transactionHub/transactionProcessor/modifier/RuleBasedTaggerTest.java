package com.transactionHub.transactionProcessor.modifier;

import com.transactionHub.transactionCoreLibrary.constant.TagType;
import com.transactionHub.transactionCoreLibrary.domain.Transaction;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

public class RuleBasedTaggerTest {

    @Test
    void testTagging() {
        var transaction = new Transaction();
        transaction.setDescription("20240513 HK Electricity Company - Sales");

        var config = Map.of(
                TagType.SCHEDULE, Map.of(
                        "SCHEDULE-1", Set.of(
                                "HK Electricity Company",
                                "China Mobile Ltd.",
                                "Southwest Church"
                        )
                )
        );

        var tagger = new RuleBasedTagger(config);

        Assertions.assertThatNoException().isThrownBy(() -> tagger.accept(transaction));

        Assertions.assertThat(transaction.getTags()).containsAll(Set.of(
                "SYS:SCHEDULE:SCHEDULE-1"
        ));


    }
}
