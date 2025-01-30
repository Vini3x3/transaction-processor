package com.transactionHub.transactionProcessor.extractor;

import com.transactionHub.transactionProcessor.extractor.csv.CsvExtractor;
import com.transactionHub.transactionProcessor.extractor.excel.ExcelExtractor;

public class ExtractorFactory {
    public static Extractor create(String type, Character delimiter) {
        if ("csv".equals(type)) {
            return new CsvExtractor(delimiter);
        }
        if ("excel".equals(type)) {
            return new ExcelExtractor();
        }
        throw new ExtractorException("unsupported extractor format: " + type);
    }

    public static Extractor create(String type) {
        return create(type, null);
    }

}
