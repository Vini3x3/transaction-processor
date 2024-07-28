package com.transactionHub.transactionProcessor.extractor;

import com.transactionHub.transactionProcessor.extractor.csv.CsvExtractor;
import com.transactionHub.transactionProcessor.extractor.excel.ExcelExtractor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ExtractorFactoryTest {

    @Test
    void testCreate_CsvExtractor() {
        var parser = ExtractorFactory.create("csv", '|');
        Assertions.assertThat(parser).isInstanceOf(CsvExtractor.class);
    }

    @Test
    void testCreate_ExcelExtractor() {
        var parser = ExtractorFactory.create("excel");
        Assertions.assertThat(parser).isInstanceOf(ExcelExtractor.class);
    }

    @Test
    void testCreate_Unsupported() {
        Assertions.assertThatExceptionOfType(ExtractorException.class)
                .isThrownBy(() -> ExtractorFactory.create("json"))
                .withMessageContaining("unsupported extractor format: json");
    }

}
