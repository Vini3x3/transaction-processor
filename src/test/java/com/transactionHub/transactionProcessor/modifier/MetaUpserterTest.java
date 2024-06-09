package com.transactionHub.transactionProcessor.modifier;

import com.transactionHub.transactionCoreLibrary.constant.TransactionMeta;
import com.transactionHub.transactionCoreLibrary.domain.Transaction;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class MetaUpserterTest {

    @Test
    void testMetaUpsert() {
        var transaction = new Transaction();
        transaction.setMeta(Map.of(
                TransactionMeta.IMPORT_FILENAME, "2024-03-BOC.csv",
                TransactionMeta.IMPORT_TIMESTAMP, "2024-03-15T04:25:18.257"
        ));

        var expectedMeta = Map.of(
                TransactionMeta.IMPORT_FILENAME, "2024-03-BOC.csv",
                TransactionMeta.IMPORT_TIMESTAMP, "2024-03-17T11:11:00.005",
                TransactionMeta.ATTACHED_FILES, "SALARY-TAX-2023-2024.pdf"
        );

        var metaUpserter = new MetaUpserter(expectedMeta);

        Assertions.assertThatNoException().isThrownBy(() -> metaUpserter.accept(transaction));

        Assertions.assertThat(transaction.getMeta()).containsAllEntriesOf(expectedMeta);

    }
}
