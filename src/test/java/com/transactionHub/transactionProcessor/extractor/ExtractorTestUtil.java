package com.transactionHub.transactionProcessor.extractor;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;

import java.util.List;
import java.util.Map;

public class ExtractorTestUtil {

    public static void assertTableContent(List<Map<String, Object>> expected, List<Map<String, Object>> actual) {
        Assertions.assertThat(actual).hasSize(expected.size());
        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertThat(expected.get(i)).asInstanceOf(InstanceOfAssertFactories.MAP)
                    .containsExactlyEntriesOf(actual.get(i));
        }

    }
}
