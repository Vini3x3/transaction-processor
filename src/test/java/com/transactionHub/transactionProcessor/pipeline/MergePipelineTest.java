package com.transactionHub.transactionProcessor.pipeline;

import com.transactionHub.transactionCoreLibrary.constant.AccountEnum;
import com.transactionHub.transactionCoreLibrary.constant.TagConstant;
import com.transactionHub.transactionCoreLibrary.constant.TransactionMeta;
import com.transactionHub.transactionCoreLibrary.domain.Transaction;
import com.transactionHub.transactionProcessor.validator.RuleViolateException;
import org.assertj.core.api.Assertions;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.transactionHub.transactionProcessor.TestUtil.convertToInstant;

public class MergePipelineTest {

    private static final String VIRTUAL_TAG = TagConstant.SYS + ":" + TagConstant.VIRTUAL;

    private final List<Transaction> SAMPLE_IMPORTS = List.of(
            new Transaction(convertToInstant(new DateTime(2024, 1, 15, 0, 0, 0)), 0, AccountEnum.BOC, "", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
            new Transaction(convertToInstant(new DateTime(2024, 1, 25, 0, 0, 0)), 0, AccountEnum.BOC, "", BigDecimal.ZERO, BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), Set.of("INTERNAL"), null, null),
            new Transaction(convertToInstant(new DateTime(2024, 1, 25, 0, 0, 0)), 1, AccountEnum.BOC, "", BigDecimal.valueOf(400), BigDecimal.ZERO, BigDecimal.valueOf(600), Set.of("SCHEDULE", "SCHEDULE-1"), null, null),
            new Transaction(convertToInstant(new DateTime(2024, 1, 25, 0, 0, 0)), 2, AccountEnum.BOC, "", BigDecimal.ZERO, BigDecimal.valueOf(90), BigDecimal.valueOf(510), Set.of("SCHEDULE", "SCHEDULE-1"), null, null),
            new Transaction(convertToInstant(new DateTime(2024, 1, 31, 0, 0, 0)), 0, AccountEnum.BOC, "", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(510))
    );

    private final List<Transaction> SAMPLE_VIRTUALS = List.of(
            new Transaction(Instant.ofEpochMilli(new DateTime(2024, 1, 25, 0, 0, 0).getMillis()), 0, AccountEnum.BOC, "", BigDecimal.ZERO, BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), Set.of("Fund Transfer", VIRTUAL_TAG), null, null),
            new Transaction(Instant.ofEpochMilli(new DateTime(2024, 1, 25, 0, 0, 0).getMillis()), 0, AccountEnum.BOC, "", BigDecimal.valueOf(400), BigDecimal.ZERO, BigDecimal.valueOf(600), Set.of("Electricity", VIRTUAL_TAG), Map.of(TransactionMeta.IMPORT_FILENAME, "Electricity Fee Jan 2024.pdf"), null),
            new Transaction(Instant.ofEpochMilli(new DateTime(2024, 1, 25, 0, 0, 0).getMillis()), 0, AccountEnum.BOC, "", BigDecimal.ZERO, BigDecimal.valueOf(90), BigDecimal.valueOf(510), Set.of("Mobile Data", VIRTUAL_TAG), Map.of(TransactionMeta.IMPORT_FILENAME, "china-mobile-202401.pdf"), null)
    );

    @Test
    void testMergeSuccess_MergeSuccess() {
        var pipeline = new MergePipeline();

        var result = pipeline.mergeData(SAMPLE_IMPORTS, SAMPLE_VIRTUALS);

        Assertions.assertThat(result).hasSize(5);

        Assertions.assertThat(result.get(1).getTags()).containsOnly("INTERNAL", "Fund Transfer");

        Assertions.assertThat(result.get(2).getTags()).containsOnly("SCHEDULE", "SCHEDULE-1", "Electricity");
        Assertions.assertThat(result.get(2).getMeta()).containsAllEntriesOf(Map.of(TransactionMeta.IMPORT_FILENAME, "Electricity Fee Jan 2024.pdf"));

        Assertions.assertThat(result.get(3).getTags()).containsOnly("SCHEDULE", "SCHEDULE-1", "Mobile Data");
        Assertions.assertThat(result.get(3).getMeta()).containsAllEntriesOf(Map.of(TransactionMeta.IMPORT_FILENAME, "china-mobile-202401.pdf"));

    }

    @Test
    void testMergeSuccess_ImportEmpty() {
        var pipeline = new MergePipeline();

        var result = pipeline.mergeData(List.of(), SAMPLE_VIRTUALS);

        Assertions.assertThat(result).isEmpty();
    }


    @Test
    void testMergeSuccess_VirtualEmpty() {
        var pipeline = new MergePipeline();

        var result = pipeline.mergeData(SAMPLE_IMPORTS, List.of());

        Assertions.assertThat(result).hasSize(5);
    }

    @Test
    void testMergeFailed_VirtualLeft() {
        var pipeline = new MergePipeline();

        var moreVirtuals = new ArrayList<>(SAMPLE_VIRTUALS);
        moreVirtuals.add(new Transaction(convertToInstant(new DateTime(2024, 1, 26, 0, 0, 0)), 0, AccountEnum.BOC, "", BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ZERO));

        Assertions.assertThatThrownBy(() -> pipeline.mergeData(SAMPLE_IMPORTS, moreVirtuals)).isInstanceOf(RuleViolateException.class);

    }
}
