package com.transactionHub.transactionProcessor.extractor;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface Extractor {
    List<Map<String, Object>> extract(InputStream inputStream);

    String FILENAME = "filename";
}

