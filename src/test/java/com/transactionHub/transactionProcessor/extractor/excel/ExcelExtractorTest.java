package com.transactionHub.transactionProcessor.extractor.excel;

import com.transactionHub.transactionCoreLibrary.constant.TransactionMeta;
import com.transactionHub.transactionProcessor.extractor.ExtractorException;
import com.transactionHub.transactionProcessor.extractor.ExtractorTestUtil;
import org.assertj.core.api.Assertions;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class ExcelExtractorTest {

    @Test
    void testTableValue() {
        String filename = "BoC-2023-12.xlsx";
        String excelPath = "sample/" + filename;
        var inputStream = ExcelExtractorTest.class.getClassLoader().getResourceAsStream(excelPath);

        var extractor = new ExcelExtractor();

        var content = extractor.extract(inputStream);
        var expected = new ArrayList<Map<String, Object>>();

        Map<String, Object> line1 = new HashMap<>();
        line1.put("Date", DateTime.parse("2023-11-30").toDate());
        line1.put("Transaction Details", "Balance Brought Forward");
        line1.put("Deposit", null);
        line1.put("Withdrawal", null);
        line1.put("Balance in Original Currency", BigDecimal.valueOf(184.00));
        line1.put(TransactionMeta.IMPORT_LINE_NO, 0);
        expected.add(line1);

        Map<String, Object> line2 = new HashMap<>();
        line2.put("Date", DateTime.parse("2023-12-25").toDate());
        line2.put("Transaction Details", "Transfer FPS/MR CHU CHI HANG/FRN20231225PAYC0101405310021");
        line2.put("Deposit", BigDecimal.valueOf(3575.34));
        line2.put("Withdrawal", null);
        line2.put("Balance in Original Currency", BigDecimal.valueOf(3759.34));
        line2.put(TransactionMeta.IMPORT_LINE_NO, 1);
        expected.add(line2);

        Map<String, Object> line3 = new HashMap<>();
        line3.put("Date", DateTime.parse("2023-12-25").toDate());
        line3.put("Transaction Details", "Transfer FPS/THE HONGKONG ELECTRIC CO LTD/12231225F386428088");
        line3.put("Deposit", null);
        line3.put("Withdrawal", BigDecimal.valueOf(279.00));
        line3.put("Balance in Original Currency", BigDecimal.valueOf(3480.34));
        line3.put(TransactionMeta.IMPORT_LINE_NO, 2);
        expected.add(line3);

        Map<String, Object> line4 = new HashMap<>();
        line4.put("Date", DateTime.parse("2023-12-25").toDate());
        line4.put("Transaction Details", "Transfer FPS/CHINA MOBILE HONG KONG COMPANY LIMITED/12231225F386432112");
        line4.put("Deposit", null);
        line4.put("Withdrawal", BigDecimal.valueOf(88.00));
        line4.put("Balance in Original Currency", BigDecimal.valueOf(3392.34));
        line4.put(TransactionMeta.IMPORT_LINE_NO, 3);
        expected.add(line4);

        Map<String, Object> line5 = new HashMap<>();
        line5.put("Date", DateTime.parse("2023-12-25").toDate());
        line5.put("Transaction Details", "Transfer FPS/CUMBERLAND PRESBYTERIAN CHURCH H K P /12231225F386428315");
        line5.put("Deposit", null);
        line5.put("Withdrawal", BigDecimal.valueOf(3000.00));
        line5.put("Balance in Original Currency", BigDecimal.valueOf(392.34));
        line5.put(TransactionMeta.IMPORT_LINE_NO, 4);
        expected.add(line5);

        Map<String, Object> line6 = new HashMap<>();
        line6.put("Date", DateTime.parse("2023-12-30").toDate());
        line6.put("Transaction Details", "Balance Carried Forward");
        line6.put("Deposit", null);
        line6.put("Withdrawal", null);
        line6.put("Balance in Original Currency", BigDecimal.valueOf(392.34));
        line6.put(TransactionMeta.IMPORT_LINE_NO, 5);
        expected.add(line6);

        ExtractorTestUtil.assertTableContent(expected, content);
    }

    @Test
    void testEmptyFile() {
        String filename = "EmptyExcel.xlsx";
        String excelPath = "sample/" + filename;
        var extractor = new ExcelExtractor();

        var inputStream = ExcelExtractorTest.class.getClassLoader().getResourceAsStream(excelPath);

        Assertions.assertThatThrownBy(() -> extractor.extract(inputStream))
                .withFailMessage("no table")
                .isInstanceOf(ExtractorException.class);
    }

}

