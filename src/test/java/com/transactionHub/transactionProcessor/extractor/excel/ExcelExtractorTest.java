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
        String filename = "2023-07-boc.xlsx";
        String excelPath = "sample/" + filename;
        var inputStream = ExcelExtractorTest.class.getClassLoader().getResourceAsStream(excelPath);

        var extractor = new ExcelExtractor();

        var actual = extractor.extract(inputStream);
        var expected = new ArrayList<Map<String, Object>>();

        Map<String, Object> line0 = new HashMap<>();
        line0.put("Date", DateTime.parse("2023-06-30").toDate());
        line0.put("Transaction Details", "Balance Brought Forward");
        line0.put("Deposit", null);
        line0.put("Withdrawal", null);
        line0.put("Balance in Original Currency", BigDecimal.valueOf(4000.00));
        line0.put(TransactionMeta.IMPORT_LINE_NO, 0);
        expected.add(line0);

        Map<String, Object> line1 = new HashMap<>();
        line1.put("Date", DateTime.parse("2023-07-25").toDate());
        line1.put("Transaction Details", "Transfer FPS/PO LEUNG KUK/12230725F280350081");
        line1.put("Deposit", null);
        line1.put("Withdrawal", BigDecimal.valueOf(3000.00));
        line1.put("Balance in Original Currency", BigDecimal.valueOf(1000.00));
        line1.put(TransactionMeta.IMPORT_LINE_NO, 1);
        expected.add(line1);

        Map<String, Object> line2 = new HashMap<>();
        line2.put("Date", DateTime.parse("2023-07-25").toDate());
        line2.put("Transaction Details", "Transfer FPS/HONG KONG & CHINA GAS LTD/12230725F348072456");
        line2.put("Deposit", null);
        line2.put("Withdrawal", BigDecimal.valueOf(500.00));
        line2.put("Balance in Original Currency", BigDecimal.valueOf(500.00));
        line2.put(TransactionMeta.IMPORT_LINE_NO, 2);
        expected.add(line2);

        Map<String, Object> line3 = new HashMap<>();
        line3.put("Date", DateTime.parse("2023-07-25").toDate());
        line3.put("Transaction Details", "Transfer FPS/MR CHAN TAI MAN/FRN20230725PAYC0101333496718");
        line3.put("Deposit", BigDecimal.valueOf(3500.00));
        line3.put("Withdrawal", null);
        line3.put("Balance in Original Currency", BigDecimal.valueOf(4000.00));
        line3.put(TransactionMeta.IMPORT_LINE_NO, 3);
        expected.add(line3);

        Map<String, Object> line4 = new HashMap<>();
        line4.put("Date", DateTime.parse("2023-07-31").toDate());
        line4.put("Transaction Details", "Balance Carried Forward");
        line4.put("Deposit", null);
        line4.put("Withdrawal", null);
        line4.put("Balance in Original Currency", BigDecimal.valueOf(4000.00));
        line4.put(TransactionMeta.IMPORT_LINE_NO, 4);
        expected.add(line4);

        ExtractorTestUtil.assertTableContent(expected, actual);
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

