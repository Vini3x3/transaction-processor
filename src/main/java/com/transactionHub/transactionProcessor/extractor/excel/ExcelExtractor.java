package com.transactionHub.transactionProcessor.extractor.excel;

import com.transactionHub.transactionCoreLibrary.constant.TransactionMeta;
import com.transactionHub.transactionProcessor.extractor.Extractor;
import com.transactionHub.transactionProcessor.extractor.ExtractorException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

public class ExcelExtractor implements Extractor {

    @Override
    public List<Map<String, Object>> extract(InputStream inputStream) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            int numberOfSheets = workbook.getNumberOfSheets();
            if (numberOfSheets < 1) {
                throw new ExtractorException("no sheets");
            }
            XSSFSheet sheet = workbook.getSheetAt(0);
            List<XSSFTable> tables = sheet.getTables();
            if (tables.isEmpty()) {
                throw new ExtractorException("no table");
            }
            var table = tables.getFirst();

            var tableRows = extractTable(sheet, table);

            for (int rowNo = 0; rowNo < tableRows.size(); rowNo++) {
                tableRows.get(rowNo).put(TransactionMeta.IMPORT_LINE_NO, rowNo);
            }
            return tableRows;

        } catch (IOException e) {
            throw new ExtractorException("fail to read file");
        }
    }

    private List<Map<String, Object>> extractTable(XSSFSheet sheet, XSSFTable table) {
        var area = table.getArea();
        var firstCell = area.getFirstCell();
        var lastCell = area.getLastCell();

        List<Map<String, Object>> result = new ArrayList<>();
        Map<Integer, String> colHeaderSet = new HashMap<>();


        for (int rowId = firstCell.getRow(); rowId <= lastCell.getRow(); rowId++) {

            // if first row, record as table head
            if (rowId == firstCell.getRow()) {
                var row = sheet.getRow(rowId);
                for (int colId = firstCell.getCol(); colId <= lastCell.getCol(); colId++) {
                    var cell = row.getCell(colId);
                    if (cell == null) {
                        continue;
                    }
                    String colHeader = (String) convertCellValue(cell);
                    colHeaderSet.put(colId, colHeader);
                }
            } else {
                var row = sheet.getRow(rowId);
                Map<String, Object> rowMap = new HashMap<>();
                for (int colId = firstCell.getCol(); colId <= lastCell.getCol(); colId++) {
                    var cell = row.getCell(colId);
                    if (cell == null) {
                        rowMap.put(colHeaderSet.get(colId), null);
                    } else {
                        Object objValue = convertCellValue(cell);
                        rowMap.put(colHeaderSet.get(colId), objValue);
                    }
                }
                result.add(rowMap);
            }
        }

        return result;
    }

    private Object convertCellValue(XSSFCell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (cell.getDateCellValue().after(Date.from(Instant.EPOCH))) {
                    yield cell.getDateCellValue();
                } else {
                    yield BigDecimal.valueOf(cell.getNumericCellValue());
                }
            }
            case BOOLEAN -> cell.getBooleanCellValue();
            default -> null;
        };
    }
}
