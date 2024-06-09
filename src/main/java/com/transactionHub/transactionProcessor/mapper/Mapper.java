package com.transactionHub.transactionProcessor.mapper;

import java.util.List;
import java.util.Map;

public interface Mapper<T> {
    T map(Map<String, Object> input);
}

