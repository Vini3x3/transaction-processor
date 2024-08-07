package com.transactionHub.transactionProcessor.mapper.transaction;

import com.transactionHub.transactionCoreLibrary.constant.AccountEnum;
import com.transactionHub.transactionCoreLibrary.constant.TransactionMeta;
import com.transactionHub.transactionCoreLibrary.domain.Transaction;
import com.transactionHub.transactionProcessor.mapper.Mapper;
import com.transactionHub.transactionProcessor.mapper.MapperException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class TransactionMapper implements Mapper<Transaction> {

    private final String dateHeader;
    private final String descriptionHeader;
    private final String withdrawalHeader;
    private final String depositHeader;
    private final String deltaHeader;
    private final String balanceHeader;
    private final AccountEnum account;
    private final String datePattern;

    public TransactionMapper(String dateHeader,
                             String descriptionHeader,
                             String withdrawalHeader,
                             String depositHeader,
                             String deltaHeader,
                             String balanceHeader,
                             AccountEnum account,
                             String datePattern) {
        this.dateHeader = dateHeader;
        this.descriptionHeader = descriptionHeader;
        this.withdrawalHeader = withdrawalHeader;
        this.depositHeader = depositHeader;
        this.balanceHeader = balanceHeader;
        this.deltaHeader = deltaHeader;
        this.account = account;
        this.datePattern = datePattern;
    }

    @Override
    public Transaction map(Map<String, Object> entries) {
        Instant date = extractDate(entries.get(this.dateHeader));
        String description = (String) entries.get(this.descriptionHeader);

        BigDecimal deposit, withdrawal;

        if (entries.containsKey(this.depositHeader) && entries.containsKey(this.withdrawalHeader)) {
            deposit = extractBigDecimal(entries.get(this.depositHeader));
            withdrawal = extractBigDecimal(entries.get(this.withdrawalHeader));
        } else {
            BigDecimal delta = extractBigDecimal(entries.get(this.deltaHeader));
            deposit = delta.compareTo(BigDecimal.ZERO) > 0 ? delta.abs() : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
            withdrawal = delta.compareTo(BigDecimal.ZERO) < 0 ? delta.abs() : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
        }

        BigDecimal balance = extractBigDecimal(entries.get(this.balanceHeader));
        int offset = (int) entries.get(TransactionMeta.IMPORT_LINE_NO);
        return new Transaction(date, offset, this.account, description, withdrawal, deposit, balance);
    }

    protected Instant extractDate(Object value) {
        if (value instanceof Date date) {
            var tmp = new DateTime(date.getTime()).withTimeAtStartOfDay();
            var tmp2 = new DateTime(tmp.getYear(), tmp.getMonthOfYear(), tmp.getDayOfMonth(), 0, 0, DateTimeZone.UTC);
            return Instant.ofEpochMilli(tmp2.getMillis());
        }
        String strValue = (String) value;
        DateTimeFormatter dtf = DateTimeFormat.forPattern(this.datePattern).withZoneUTC();
        return Instant.ofEpochMilli(dtf.parseDateTime(strValue).getMillis());
    }

    protected BigDecimal extractBigDecimal(Object value) {

        if (value == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
        }

        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal.setScale(2, RoundingMode.HALF_EVEN);
        }

        String strValue = (String) value;
        if (strValue.isBlank()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
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
