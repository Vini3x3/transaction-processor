package com.transactionHub.transactionProcessor.mapper.transaction;

import com.transactionHub.transactionCoreLibrary.constant.TransactionMeta;
import com.transactionHub.transactionCoreLibrary.domain.Transaction;
import com.transactionHub.transactionProcessor.mapper.Mapper;
import com.transactionHub.transactionProcessor.mapper.MapperException;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class TransactionMapper implements Mapper<Transaction> {

    protected final TransactionMapperConfig config;

    public TransactionMapper(TransactionMapperConfig config) {
        this.config = config;
    }

    @Override
    public Transaction map(Map<String, Object> entries) {
        Date date = extractDate(entries.get(config.dateHeader()));
        String description = (String) entries.get(config.descriptionHeader());
        BigDecimal deposit = extractBigDecimal(entries.get(config.depositHeader()));
        BigDecimal withdrawal = extractBigDecimal(entries.get(config.withdrawalHeader()));
        BigDecimal balance = extractBigDecimal(entries.get(config.balanceHeader()));
        int offset = (int)entries.get(TransactionMeta.IMPORT_LINE_NO);
        return new Transaction(date, offset, config.account(), description, withdrawal, deposit, balance);
    }

    protected Date extractDate(Object value) {
        if (value instanceof Date date) {
            return date;
        }
        String strValue = (String) value;
        DateTimeFormatter dtf = DateTimeFormat.forPattern(config.datePattern());
        return dtf.parseDateTime(strValue).toDate();
    }

    protected BigDecimal extractBigDecimal(Object value) {

        if (value == null) {
            return BigDecimal.ZERO.setScale(2);
        }

        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal.setScale(2);
        }

        String strValue = (String) value;
        if (strValue.isBlank()) {
            return BigDecimal.ZERO.setScale(2);
        }

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        String pattern = "#,##0.0#";
        DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
        decimalFormat.setParseBigDecimal(true);

        try {
            return (BigDecimal) decimalFormat.parse(strValue);
        } catch (ParseException e) {
            throw new MapperException(String.format("invalid number format: %s", strValue));
        }
    }

}
