package com.transactionHub.transactionProcessor.extractor;

import com.transactionHub.transactionProcessor.extractor.csv.CsvExtractor;
import com.transactionHub.transactionProcessor.extractor.excel.ExcelExtractor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ExtractorFactoryTest {

    @Test
    void testCreate_CsvExtractor() {
        var parser = ExtractorFactory.create().withType("csv").build();
        Assertions.assertThat(parser).isInstanceOf(CsvExtractor.class);
    }

    @Test
    void testCreate_ExcelExtractor() {
        var parser = ExtractorFactory.create().withType("excel").build();
        Assertions.assertThat(parser).isInstanceOf(ExcelExtractor.class);
    }

    @Test
    void testCreate_Unsupported() {
        Assertions.assertThatExceptionOfType(ExtractorException.class)
                .isThrownBy(() -> ExtractorFactory.create().withType("json").build())
                .withMessageContaining("unsupported extractor format: json");
    }

}
