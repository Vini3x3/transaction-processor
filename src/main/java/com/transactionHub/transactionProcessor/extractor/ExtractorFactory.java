package com.transactionHub.transactionProcessor.extractor;

import com.transactionHub.transactionProcessor.extractor.csv.CsvExtractor;
import com.transactionHub.transactionProcessor.extractor.excel.ExcelExtractor;

public class ExtractorFactory {

    private String type;
    private Character delimiter;
    private Integer skip;

    public ExtractorFactory() {
        this.delimiter = null;
        this.skip = null;
    }

    public static ExtractorFactory create() {
        return new ExtractorFactory();
    }

    public ExtractorFactory withType(String type) {
        this.type = type;
        return this;
    }

    public ExtractorFactory withDelimiter(Character delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    public ExtractorFactory withSkip(Integer skip) {
        this.skip = skip;
        return this;
    }

    public Extractor build() {
        if ("csv".equals(type)) {
            return new CsvExtractor(delimiter, skip);
        }
        if ("excel".equals(type)) {
            return new ExcelExtractor();
        }
        throw new ExtractorException("unsupported extractor format: " + type);
    }

}
