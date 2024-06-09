package com.transactionHub.transactionProcessor.extractor.csv;

import com.transactionHub.transactionCoreLibrary.constant.TransactionMeta;
import com.transactionHub.transactionProcessor.extractor.Extractor;
import com.transactionHub.transactionProcessor.extractor.ExtractorException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class CsvExtractor implements Extractor {

    private final String delimiter;
    private final String lineBreak;

    public CsvExtractor(String delimiter, String lineBreak) {
        this.delimiter = delimiter;
        this.lineBreak = lineBreak;
    }

    @Override
    public List<Map<String, Object>> extract(InputStream inputStream) {

        try {
            String content = new String(inputStream.readAllBytes());
            List<String> lines = Arrays.stream(content.split(this.lineBreak)).toList();
            if (lines.size() < 2) {
                throw new ExtractorException("Empty CSV");
            }

            String header = lines.getFirst();
            List<String> headers = Arrays.stream(header.split(delimiter)).map(String::trim).toList();


            List<Map<String, Object>> result = new ArrayList<>();

            int rowNo = 0;
            for (String line : lines.subList(1, lines.size())) {
                List<String> values = Arrays.stream(line.split(delimiter)).map(String::trim).toList();
                if (values.size() != headers.size()) {
                    throw new ExtractorException("CSV has line with items not equal to header");
                }
                var entryMap = convertToMap(headers, values);
                entryMap.put(TransactionMeta.IMPORT_LINE_NO, rowNo);
                result.add(entryMap);
                rowNo++;
            }

            return result;
        } catch (IOException e) {
            throw new ExtractorException(e.getMessage());
        }
    }

    private Map<String, Object> convertToMap(List<String> headers, List<String> items) {
        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            result.put(headers.get(i), items.get(i));
        }
        return result;
    }
}
