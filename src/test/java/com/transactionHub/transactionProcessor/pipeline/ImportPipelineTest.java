package com.transactionHub.transactionProcessor.pipeline;

import com.transactionHub.transactionCoreLibrary.constant.AccountEnum;
import com.transactionHub.transactionCoreLibrary.constant.TagType;
import com.transactionHub.transactionCoreLibrary.constant.TransactionMeta;
import com.transactionHub.transactionCoreLibrary.domain.Transaction;
import com.transactionHub.transactionProcessor.extractor.Extractor;
import com.transactionHub.transactionProcessor.extractor.csv.CsvExtractor;
import com.transactionHub.transactionProcessor.extractor.excel.ExcelExtractor;
import com.transactionHub.transactionProcessor.mapper.transaction.TransactionMapper;
import com.transactionHub.transactionProcessor.modifier.SystemTagger;
import com.transactionHub.transactionProcessor.modifier.Tagger;
import org.assertj.core.api.Assertions;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import static com.transactionHub.transactionProcessor.TestUtil.convertToInstant;

class ImportPipelineTest {

    @Test
    @Timeout(3)
    void testImportCsv_BOC() {

        String filename = "BoC-2023-12.csv";
        var extractor = new CsvExtractor();

        assertBocImport(filename, extractor);
    }

    @Test
    @Timeout(3)
    void testImportExcel_BOC() {

        String filename = "BoC-2023-12.xlsx";
        var extractor = new ExcelExtractor();

        assertBocImport(filename, extractor);
    }

    void assertBocImport(String filename, Extractor extractor) {
        String excelPath = "sample/" + filename;
        var inputStream = ImportPipelineTest.class.getClassLoader().getResourceAsStream(excelPath);

        var mapper = new TransactionMapper(
                "Date",
                "Transaction Details",
                "Withdrawal",
                "Deposit",
                "Balance in Original Currency",
                AccountEnum.BOC,
                "yyyy/MM/dd");

        var tagger = new Tagger(Map.of(
                "MR CHU CHI HANG", Set.of(
                        "INTERNAL"
                )
        ));

        var systemTagger = new SystemTagger(Map.of(
                TagType.SCHEDULE, Map.of(
                        "SCHEDULE-1", Set.of(
                                "THE HONGKONG ELECTRIC CO LTD",
                                "CHINA MOBILE HONG KONG COMPANY LIMITED",
                                "CUMBERLAND PRESBYTERIAN CHURCH H K P"
                        )
                )
        ));

        var importPipeline = new ImportPipeline(extractor, mapper, tagger, systemTagger);

        var transactions = importPipeline.importData(inputStream, filename);

        DateTimeFormatter fmt = ISODateTimeFormat.date();
        String importDateString = fmt.print(DateTime.now(DateTimeZone.UTC));

        Assertions.assertThat(transactions).hasSize(6);

        Transaction transaction0 = transactions.get(0);
        Assertions.assertThat(transaction0.getDate()).isEqualTo(convertToInstant(new DateTime(2023, 11, 30, 0, 0, 0, DateTimeZone.UTC)));
        Assertions.assertThat(transaction0.getOffset()).isEqualTo(0);
        Assertions.assertThat(transaction0.getDescription()).isEqualTo("Balance Brought Forward");
        Assertions.assertThat(transaction0.getDeposit()).isEqualTo(new BigDecimal("0.00"));
        Assertions.assertThat(transaction0.getWithdrawal()).isEqualTo(new BigDecimal("0.00"));
        Assertions.assertThat(transaction0.getTags()).isEmpty();
        Assertions.assertThat(transaction0.getMeta()).containsEntry(TransactionMeta.IMPORT_FILENAME, filename);
        Assertions.assertThat(transaction0.getMeta()).containsKey(TransactionMeta.IMPORT_TIMESTAMP);
        Assertions.assertThat(transaction0.getMeta().get(TransactionMeta.IMPORT_TIMESTAMP)).contains(importDateString);

        Transaction transaction1 = transactions.get(1);
        Assertions.assertThat(transaction1.getDate()).isEqualTo(convertToInstant(new DateTime(2023, 12, 25, 0, 0, 0, DateTimeZone.UTC)));
        Assertions.assertThat(transaction1.getOffset()).isEqualTo(0);
        Assertions.assertThat(transaction1.getDescription()).isEqualTo("Transfer FPS/MR CHU CHI HANG/FRN20231225PAYC0101405310021");
        Assertions.assertThat(transaction1.getDeposit()).isEqualTo(new BigDecimal("3575.34"));
        Assertions.assertThat(transaction1.getWithdrawal()).isEqualTo(new BigDecimal("0.00"));
        Assertions.assertThat(transaction1.getTags()).containsExactly("INTERNAL");
        Assertions.assertThat(transaction1.getMeta()).containsEntry(TransactionMeta.IMPORT_FILENAME, filename);
        Assertions.assertThat(transaction1.getMeta()).containsKey(TransactionMeta.IMPORT_TIMESTAMP);
        Assertions.assertThat(transaction1.getMeta().get(TransactionMeta.IMPORT_TIMESTAMP)).contains(importDateString);

        Transaction transaction2 = transactions.get(2);
        Assertions.assertThat(transaction2.getDate()).isEqualTo(convertToInstant(new DateTime(2023, 12, 25, 0, 0, 0, DateTimeZone.UTC)));
        Assertions.assertThat(transaction2.getOffset()).isEqualTo(1);
        Assertions.assertThat(transaction2.getDescription()).isEqualTo("Transfer FPS/THE HONGKONG ELECTRIC CO LTD/12231225F386428088");
        Assertions.assertThat(transaction2.getDeposit()).isEqualTo(new BigDecimal("0.00"));
        Assertions.assertThat(transaction2.getWithdrawal()).isEqualTo(new BigDecimal("279.00"));
        Assertions.assertThat(transaction2.getBalance()).isEqualTo(new BigDecimal("3480.34"));
        Assertions.assertThat(transaction2.getTags()).containsExactly("SYS:SCHEDULE:SCHEDULE-1");
        Assertions.assertThat(transaction2.getMeta()).containsEntry(TransactionMeta.IMPORT_FILENAME, filename);
        Assertions.assertThat(transaction2.getMeta()).containsKey(TransactionMeta.IMPORT_TIMESTAMP);
        Assertions.assertThat(transaction2.getMeta().get(TransactionMeta.IMPORT_TIMESTAMP)).contains(importDateString);
    }


}
