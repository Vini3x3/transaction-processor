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

        String filename = "BoC-2023-12.csv";
        String filePath = "sample/" + filename;
        var inputStream = CsvExtractorTest.class.getClassLoader().getResourceAsStream(filePath);
        Assertions.assertThat(inputStream).isNotNull();

        var extractor = new CsvExtractor("\\|", System.lineSeparator());
        var actual = extractor.extract(inputStream);

        var expected = new ArrayList<Map<String, Object>>();

        Map<String, Object> line1 = new HashMap<>();
        line1.put("Date", "2023/11/30");
        line1.put("Transaction Details", "Balance Brought Forward");
        line1.put("Deposit", "");
        line1.put("Withdrawal", "");
        line1.put("Balance in Original Currency", "184.00");
        line1.put(TransactionMeta.IMPORT_LINE_NO, 0);
        expected.add(line1);

        Map<String, Object> line2 = new HashMap<>();
        line2.put("Date", "2023/12/25");
        line2.put("Transaction Details", "Transfer FPS/MR CHU CHI HANG/FRN20231225PAYC0101405310021");
        line2.put("Deposit", "3,575.34");
        line2.put("Withdrawal", "");
        line2.put("Balance in Original Currency", "3,759.34");
        line2.put(TransactionMeta.IMPORT_LINE_NO, 1);
        expected.add(line2);

        Map<String, Object> line3 = new HashMap<>();
        line3.put("Date", "2023/12/25");
        line3.put("Transaction Details", "Transfer FPS/THE HONGKONG ELECTRIC CO LTD/12231225F386428088");
        line3.put("Deposit", "");
        line3.put("Withdrawal", "279.00");
        line3.put("Balance in Original Currency", "3,480.34");
        line3.put(TransactionMeta.IMPORT_LINE_NO, 2);
        expected.add(line3);

        Map<String, Object> line4 = new HashMap<>();
        line4.put("Date", "2023/12/25");
        line4.put("Transaction Details", "Transfer FPS/CHINA MOBILE HONG KONG COMPANY LIMITED/12231225F386432112");
        line4.put("Deposit", "");
        line4.put("Withdrawal", "88.00");
        line4.put("Balance in Original Currency", "3,392.34");
        line4.put(TransactionMeta.IMPORT_LINE_NO, 3);
        expected.add(line4);

        Map<String, Object> line5 = new HashMap<>();
        line5.put("Date", "2023/12/25");
        line5.put("Transaction Details", "Transfer FPS/CUMBERLAND PRESBYTERIAN CHURCH H K P/12231225F386428315");
        line5.put("Deposit", "");
        line5.put("Withdrawal", "3,000.00");
        line5.put("Balance in Original Currency", "392.34");
        line5.put(TransactionMeta.IMPORT_LINE_NO, 4);
        expected.add(line5);

        Map<String, Object> line6 = new HashMap<>();
        line6.put("Date", "2023/12/30");
        line6.put("Transaction Details", "Balance Carried Forward");
        line6.put("Deposit", "");
        line6.put("Withdrawal", "");
        line6.put("Balance in Original Currency", "392.34");
        line6.put(TransactionMeta.IMPORT_LINE_NO, 5);
        expected.add(line6);

        ExtractorTestUtil.assertTableContent(expected, actual);

    }

    @Test
    void testHeaderOnlyFile() {
        String filename = "HeaderOnlyCsv.csv";
        String filePath = "sample/" + filename;

        var inputStream = CsvExtractorTest.class.getClassLoader().getResourceAsStream(filePath);
        Assertions.assertThat(inputStream).isNotNull();

        var extractor = new CsvExtractor("\\|", System.lineSeparator());

        Assertions.assertThatThrownBy(() -> extractor.extract(inputStream))
                .withFailMessage("Empty CSV")
                .isInstanceOf(ExtractorException.class);
    }
}
