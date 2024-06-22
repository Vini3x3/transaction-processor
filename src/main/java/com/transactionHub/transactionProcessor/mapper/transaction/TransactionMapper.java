package com.transactionHub.transactionProcessor.mapper.transaction;

import com.transactionHub.transactionCoreLibrary.constant.AccountEnum;
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

    private final String dateHeader;
    private final String descriptionHeader;
    private final String withdrawalHeader;
    private final String depositHeader;
    private final String balanceHeader;
    private final AccountEnum account;
    private final String datePattern;

    public TransactionMapper(String dateHeader,
                             String descriptionHeader,
                             String withdrawalHeader,
                             String depositHeader,
                             String balanceHeader,
                             AccountEnum account,
                             String datePattern) {
        this.dateHeader = dateHeader;
        this.descriptionHeader = descriptionHeader;
        this.withdrawalHeader = withdrawalHeader;
        this.depositHeader = depositHeader;
        this.balanceHeader = balanceHeader;
        this.account = account;
        this.datePattern = datePattern;
    }

    @Override
    public Transaction map(Map<String, Object> entries) {
        Date date = extractDate(entries.get(this.dateHeader));
        String description = (String) entries.get(this.descriptionHeader);
        BigDecimal deposit = extractBigDecimal(entries.get(this.depositHeader));
        BigDecimal withdrawal = extractBigDecimal(entries.get(this.withdrawalHeader));
        BigDecimal balance = extractBigDecimal(entries.get(this.balanceHeader));
        int offset = (int)entries.get(TransactionMeta.IMPORT_LINE_NO);
        return new Transaction(date, offset, this.account, description, withdrawal, deposit, balance);
    }

    protected Date extractDate(Object value) {
        if (value instanceof Date date) {
            return date;
        }
        String strValue = (String) value;
        DateTimeFormatter dtf = DateTimeFormat.forPattern(this.datePattern);
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
