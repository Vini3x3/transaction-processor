package com.transactionHub.transactionProcessor.extractor.csv;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.transactionHub.transactionCoreLibrary.constant.TransactionMeta;
import com.transactionHub.transactionProcessor.extractor.Extractor;
import com.transactionHub.transactionProcessor.extractor.ExtractorException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class CsvExtractor implements Extractor {

    private final char separator;

    public CsvExtractor() {
        this('|');
    }

    public CsvExtractor(char separator) {
        this.separator = separator;
    }

    @Override
    public List<Map<String, Object>> extract(InputStream inputStream) {

        CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream))
                .withCSVParser(new CSVParserBuilder()
                        .withSeparator(separator)
                        .build())
                .build();

        List<String> headers = new ArrayList<>();
        List<Map<String, Object>> result = new ArrayList<>();


        int lineRead = 0;
        String[] line;
        try {
            while ((line = reader.readNext()) != null) {
                if (lineRead == 0) {
                    headers = Arrays.stream(line).map(String::trim).toList();
                } else {
                    List<String> values = Arrays.stream(line).map(String::trim).toList();
                    if (values.size() != headers.size()) {
                        throw new ExtractorException("CSV has line with items not equal to header");
                    }
                    var entryMap = convertToMap(headers, values);
                    entryMap.put(TransactionMeta.IMPORT_LINE_NO, lineRead - 1);
                    result.add(entryMap);
                }
                lineRead++;
            }
        } catch (CsvValidationException | IOException e) {
            throw new ExtractorException(e.getMessage());
        }

        return result;
    }

    private Map<String, Object> convertToMap(List<String> headers, List<String> items) {
        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            result.put(headers.get(i), items.get(i));
        }
        return result;
    }
}
