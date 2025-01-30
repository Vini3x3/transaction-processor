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
        line0.put("Transaction Date", DateTime.parse("2023-07-25").toDate());
        line0.put("Particulars", "Transfer FPS/PO LEUNG KUK/12230725F280350081");
        line0.put("Deposits", null);
        line0.put("Withdrawals", BigDecimal.valueOf(3000.00));
        line0.put("Balance/Debit(DR)", BigDecimal.valueOf(1000.00));
        line0.put(TransactionMeta.IMPORT_LINE_NO, 0);
        expected.add(line0);

        Map<String, Object> line1 = new HashMap<>();
        line1.put("Transaction Date", DateTime.parse("2023-07-25").toDate());
        line1.put("Particulars", "Transfer FPS/HONG KONG & CHINA GAS LTD/12230725F348072456");
        line1.put("Deposits", null);
        line1.put("Withdrawals", BigDecimal.valueOf(500.00));
        line1.put("Balance/Debit(DR)", BigDecimal.valueOf(500.00));
        line1.put(TransactionMeta.IMPORT_LINE_NO, 1);
        expected.add(line1);

        Map<String, Object> line2 = new HashMap<>();
        line2.put("Transaction Date", DateTime.parse("2023-07-25").toDate());
        line2.put("Particulars", "Transfer FPS/MR CHAN TAI MAN/FRN20230725PAYC0101333496718");
        line2.put("Deposits", BigDecimal.valueOf(3500.00));
        line2.put("Withdrawals", null);
        line2.put("Balance/Debit(DR)", BigDecimal.valueOf(4000.00));
        line2.put(TransactionMeta.IMPORT_LINE_NO, 2);
        expected.add(line2);

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

