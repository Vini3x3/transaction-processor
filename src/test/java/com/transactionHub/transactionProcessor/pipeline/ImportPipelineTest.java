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

        String filename = "2023-07-boc.csv";
        var extractor = new CsvExtractor();

        assertBocImport(filename, extractor);
    }

    @Test
    @Timeout(3)
    void testImportExcel_BOC() {

        String filename = "2023-07-boc.xlsx";
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
                "",
                "Balance in Original Currency",
                AccountEnum.BOC,
                "yyyy/MM/dd");

        var tagger = new Tagger(Map.of(
                "MR CHAN TAI MAN", Set.of(
                        "FROM-HSBC"
                )
        ));

        var systemTagger = new SystemTagger(Map.of(
                TagType.SCHEDULE, Map.of(
                        "BILL", Set.of(
                                "PO LEUNG KUK",
                                "HONG KONG & CHINA GAS LTD"
                        )
                )
        ));

        var importPipeline = new ImportPipeline(extractor, mapper, tagger, systemTagger);

        var transactions = importPipeline.importData(inputStream, filename);

        DateTimeFormatter fmt = ISODateTimeFormat.date();
        String importDateString = fmt.print(DateTime.now(DateTimeZone.UTC));

        Assertions.assertThat(transactions).hasSize(5);

        Transaction transaction0 = transactions.get(0);
        Assertions.assertThat(transaction0.getDate()).isEqualTo(convertToInstant(new DateTime(2023, 6, 30, 0, 0, 0, DateTimeZone.UTC)));
        Assertions.assertThat(transaction0.getOffset()).isEqualTo(0);
        Assertions.assertThat(transaction0.getDescription()).isEqualTo("Balance Brought Forward");
        Assertions.assertThat(transaction0.getDeposit()).isEqualTo(new BigDecimal("0.00"));
        Assertions.assertThat(transaction0.getWithdrawal()).isEqualTo(new BigDecimal("0.00"));
        Assertions.assertThat(transaction0.getTags()).isEmpty();
        Assertions.assertThat(transaction0.getMeta()).containsEntry(TransactionMeta.IMPORT_FILENAME, filename);
        Assertions.assertThat(transaction0.getMeta()).containsKey(TransactionMeta.IMPORT_TIMESTAMP);
        Assertions.assertThat(transaction0.getMeta().get(TransactionMeta.IMPORT_TIMESTAMP)).contains(importDateString);

        Transaction transaction1 = transactions.get(1);
        Assertions.assertThat(transaction1.getDate()).isEqualTo(convertToInstant(new DateTime(2023, 7, 25, 0, 0, 0, DateTimeZone.UTC)));
        Assertions.assertThat(transaction1.getOffset()).isEqualTo(0);
        Assertions.assertThat(transaction1.getDescription()).isEqualTo("Transfer FPS/PO LEUNG KUK/12230725F280350081");
        Assertions.assertThat(transaction1.getDeposit()).isEqualTo(new BigDecimal("0.00"));
        Assertions.assertThat(transaction1.getWithdrawal()).isEqualTo(new BigDecimal("3000.00"));
        Assertions.assertThat(transaction1.getTags()).containsExactly("SYS:SCHEDULE:BILL");
        Assertions.assertThat(transaction1.getMeta()).containsEntry(TransactionMeta.IMPORT_FILENAME, filename);
        Assertions.assertThat(transaction1.getMeta()).containsKey(TransactionMeta.IMPORT_TIMESTAMP);
        Assertions.assertThat(transaction1.getMeta().get(TransactionMeta.IMPORT_TIMESTAMP)).contains(importDateString);

        Transaction transaction2 = transactions.get(2);
        Assertions.assertThat(transaction2.getDate()).isEqualTo(convertToInstant(new DateTime(2023, 7, 25, 0, 0, 0, DateTimeZone.UTC)));
        Assertions.assertThat(transaction2.getOffset()).isEqualTo(1);
        Assertions.assertThat(transaction2.getDescription()).isEqualTo("Transfer FPS/HONG KONG & CHINA GAS LTD/12230725F348072456");
        Assertions.assertThat(transaction2.getDeposit()).isEqualTo(new BigDecimal("0.00"));
        Assertions.assertThat(transaction2.getWithdrawal()).isEqualTo(new BigDecimal("500.00"));
        Assertions.assertThat(transaction2.getBalance()).isEqualTo(new BigDecimal("500.00"));
        Assertions.assertThat(transaction2.getTags()).containsExactly("SYS:SCHEDULE:BILL");
        Assertions.assertThat(transaction2.getMeta()).containsEntry(TransactionMeta.IMPORT_FILENAME, filename);
        Assertions.assertThat(transaction2.getMeta()).containsKey(TransactionMeta.IMPORT_TIMESTAMP);
        Assertions.assertThat(transaction2.getMeta().get(TransactionMeta.IMPORT_TIMESTAMP)).contains(importDateString);

        Transaction transaction3 = transactions.get(3);
        Assertions.assertThat(transaction3.getDate()).isEqualTo(convertToInstant(new DateTime(2023, 7, 25, 0, 0, 0, DateTimeZone.UTC)));
        Assertions.assertThat(transaction3.getOffset()).isEqualTo(2);
        Assertions.assertThat(transaction3.getDescription()).isEqualTo("Transfer FPS/MR CHAN TAI MAN/FRN20230725PAYC0101333496718");
        Assertions.assertThat(transaction3.getDeposit()).isEqualTo(new BigDecimal("3500.00"));
        Assertions.assertThat(transaction3.getWithdrawal()).isEqualTo(new BigDecimal("000.00"));
        Assertions.assertThat(transaction3.getBalance()).isEqualTo(new BigDecimal("4000.00"));
        Assertions.assertThat(transaction3.getTags()).containsExactly("FROM-HSBC");
        Assertions.assertThat(transaction3.getMeta()).containsEntry(TransactionMeta.IMPORT_FILENAME, filename);
        Assertions.assertThat(transaction3.getMeta()).containsKey(TransactionMeta.IMPORT_TIMESTAMP);
        Assertions.assertThat(transaction3.getMeta().get(TransactionMeta.IMPORT_TIMESTAMP)).contains(importDateString);
    }


}
