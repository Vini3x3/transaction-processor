package com.transactionHub.transactionProcessor.extractor.csv;

import com.transactionHub.transactionCoreLibrary.constant.TransactionMeta;
import com.transactionHub.transactionProcessor.extractor.ExtractorTestUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CsvExtractorTest {

    @Test
    @DisplayName("Test specialized setup with stroke and non-standard quote format")
    void testExtract() {

        String filename = "2023-07-boc.csv";
        String filePath = "sample/" + filename;
        var inputStream = CsvExtractorTest.class.getClassLoader().getResourceAsStream(filePath);
        Assertions.assertThat(inputStream).isNotNull();

        var extractor = new CsvExtractor(',', 3);
        var actual = extractor.extract(inputStream);

        var expected = new ArrayList<Map<String, Object>>();

        Map<String, Object> line0 = new HashMap<>();
        line0.put("Transaction Date", "2023/07/25");
        line0.put("Transaction Details", "Transfer");
        line0.put("Currency", "HKD");
        line0.put("Particulars", "Transfer FPS/PO LEUNG KUK/12230725F280350081");
        line0.put("Deposits", "");
        line0.put("Withdrawals", "3,000.00");
        line0.put("Balance/Debit(DR)", "1,000.00");
        line0.put(TransactionMeta.IMPORT_LINE_NO, 0);
        expected.add(line0);

        Map<String, Object> line1 = new HashMap<>();
        line1.put("Transaction Date", "2023/07/25");
        line1.put("Transaction Details", "Transfer");
        line1.put("Currency", "HKD");
        line1.put("Particulars", "Transfer FPS/HONG KONG & CHINA GAS LTD/12230725F348072456");
        line1.put("Deposits", "");
        line1.put("Withdrawals", "500.00");
        line1.put("Balance/Debit(DR)", "500.00");
        line1.put(TransactionMeta.IMPORT_LINE_NO, 1);
        expected.add(line1);

        Map<String, Object> line2 = new HashMap<>();
        line2.put("Transaction Date", "2023/07/25");
        line2.put("Transaction Details", "Transfer");
        line2.put("Currency", "HKD");
        line2.put("Particulars", "Transfer FPS/MR CHAN TAI MAN/FRN20230725PAYC0101333496718");
        line2.put("Deposits", "3,500.00");
        line2.put("Withdrawals", "");
        line2.put("Balance/Debit(DR)", "4,000.00");
        line2.put(TransactionMeta.IMPORT_LINE_NO, 2);
        expected.add(line2);

        ExtractorTestUtil.assertTableContent(expected, actual);

    }

    @Test
    @DisplayName("Test default setup with comma and no-quote format")
    void testExtract_Default() {

        String filename = "2023-07-hsbc-saving.csv";
        String filePath = "sample/" + filename;
        var inputStream = CsvExtractorTest.class.getClassLoader().getResourceAsStream(filePath);
        Assertions.assertThat(inputStream).isNotNull();

        var extractor = new CsvExtractor();
        var actual = extractor.extract(inputStream);

        var expected = new ArrayList<Map<String, Object>>();

        Map<String, Object> line0 = new HashMap<>();
        line0.put("Date", "25/07/2023");
        line0.put("Description", "CHAN TAI MAN             N23828924859(25JUL23)");
        line0.put("Billing amount", "8,000.00");
        line0.put("Billing currency", "HKD");
        line0.put("Balance", "108,000.00");
        line0.put("Balance currency", "HKD");
        line0.put(TransactionMeta.IMPORT_LINE_NO, 0);
        expected.add(line0);

        Map<String, Object> line1 = new HashMap<>();
        line1.put("Date", "28/07/2023");
        line1.put("Description", "CREDIT INTEREST");
        line1.put("Billing amount", "64.80");
        line1.put("Billing currency", "HKD");
        line1.put("Balance", "108,064.80");
        line1.put("Balance currency", "HKD");
        line1.put(TransactionMeta.IMPORT_LINE_NO, 1);
        expected.add(line1);

        ExtractorTestUtil.assertTableContent(expected, actual);

    }

}
