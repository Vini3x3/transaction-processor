package com.transactionHub.transactionProcessor.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RegexExtractorTest {

    @Test
    void testMatching() {
        String regex = "^([a-zA-Z]+)([0-9]+)(.*)";
        String input = "Testing123Testing";
        var expected = List.of(
                "Testing", "123"
        );

        var actual = RegexExtractor.extract(input, regex);
        Assertions.assertThat(actual).containsExactlyElementsOf(expected);
    }
}
