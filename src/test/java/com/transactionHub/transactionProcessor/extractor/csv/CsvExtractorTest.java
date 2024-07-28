package com.transactionHub.transactionProcessor.extractor.csv;

import com.transactionHub.transactionCoreLibrary.constant.TransactionMeta;
import com.transactionHub.transactionProcessor.extractor.ExtractorException;
import com.transactionHub.transactionProcessor.extractor.ExtractorTestUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CsvExtractorTest {

    @Test
    void testExtract() {

        String filename = "2023-07-boc.csv";
        String filePath = "sample/" + filename;
        var inputStream = CsvExtractorTest.class.getClassLoader().getResourceAsStream(filePath);
        Assertions.assertThat(inputStream).isNotNull();

        var extractor = new CsvExtractor();
        var actual = extractor.extract(inputStream);

        var expected = new ArrayList<Map<String, Object>>();

        Map<String, Object> line0 = new HashMap<>();
        line0.put("Date", "2023/06/30");
        line0.put("Transaction Details", "Balance Brought Forward");
        line0.put("Deposit", "");
        line0.put("Withdrawal", "");
        line0.put("Balance in Original Currency", "4,000.00");
        line0.put(TransactionMeta.IMPORT_LINE_NO, 0);
        expected.add(line0);

        Map<String, Object> line1 = new HashMap<>();
        line1.put("Date", "2023/07/25");
        line1.put("Transaction Details", "Transfer FPS/PO LEUNG KUK/12230725F280350081");
        line1.put("Deposit", "");
        line1.put("Withdrawal", "3,000.00");
        line1.put("Balance in Original Currency", "1,000.00");
        line1.put(TransactionMeta.IMPORT_LINE_NO, 1);
        expected.add(line1);

        Map<String, Object> line2 = new HashMap<>();
        line2.put("Date", "2023/07/25");
        line2.put("Transaction Details", "Transfer FPS/HONG KONG & CHINA GAS LTD/12230725F348072456");
        line2.put("Deposit", "");
        line2.put("Withdrawal", "500.00");
        line2.put("Balance in Original Currency", "500.00");
        line2.put(TransactionMeta.IMPORT_LINE_NO, 2);
        expected.add(line2);

        Map<String, Object> line3 = new HashMap<>();
        line3.put("Date", "2023/07/25");
        line3.put("Transaction Details", "Transfer FPS/MR CHAN TAI MAN/FRN20230725PAYC0101333496718");
        line3.put("Deposit", "3,500.00");
        line3.put("Withdrawal", "");
        line3.put("Balance in Original Currency", "4,000.00");
        line3.put(TransactionMeta.IMPORT_LINE_NO, 3);
        expected.add(line3);

        Map<String, Object> line4 = new HashMap<>();
        line4.put("Date", "2023/07/31");
        line4.put("Transaction Details", "Balance Carried Forward");
        line4.put("Deposit", "");
        line4.put("Withdrawal", "");
        line4.put("Balance in Original Currency", "4,000.00");
        line4.put(TransactionMeta.IMPORT_LINE_NO, 4);
        expected.add(line4);

        ExtractorTestUtil.assertTableContent(expected, actual);

    }

}
