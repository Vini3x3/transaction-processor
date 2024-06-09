package com.transactionHub.transactionProcessor.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexExtractor {
    public static List<Object> extract(String input, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);

        List<Object> result = new ArrayList<>();
        if (m.find()) {
            for (int i = 1; i < m.groupCount(); i++) {
                result.add(m.group(i));
            }
        }
        return result;
    }
}
