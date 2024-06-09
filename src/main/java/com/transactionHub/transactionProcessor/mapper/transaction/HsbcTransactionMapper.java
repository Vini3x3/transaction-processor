//package com.transactionHub.transactionProcessor.mapper.transaction;
//
//import com.transactionHub.transactionCoreLibrary.domain.Transaction;
//import com.transactionHub.transactionProcessor.mapper.MapperException;
//import org.joda.time.format.DateTimeFormat;
//import org.joda.time.format.DateTimeFormatter;
//
//import java.math.BigDecimal;
//import java.util.*;
//
//import static com.transactionHub.transactionProcessor.extractor.Extractor.FILENAME;
//
//public class HsbcTransactionMapper extends TransactionMapper {
//
//    public HsbcTransactionMapper(TransactionMapperConfig config) {
//        super(config);
//    }
//
//    @Override
//    public List<Transaction> map(List<Map<String, Object>> entries) {
//        assertValidContent(entries);
//
//        List<Transaction> transactions = new ArrayList<>();
//        for (int i = 0; i < entries.size(); i++) {
//            var input = entries.get(i);
//
//            String filename = (String)input.get(FILENAME);
//            assertFilenamePattern(filename);
//
//            Date date = extractDate(input.get(config.dateHeader()), filename);
//            String description = (String) input.get(config.descriptionHeader());
//            BigDecimal deposit = (BigDecimal) input.get(config.depositHeader());
//            BigDecimal withdrawal = (BigDecimal) input.get(config.withdrawalHeader());
//            BigDecimal balance = (BigDecimal) input.get(config.balanceHeader());
//            transactions.add(new Transaction(date, i, config.account(), description, withdrawal, deposit, balance, false, Set.of()));
//        }
//        return transactions;
//    }
//
//    protected Date extractDate(Object value, String filename) {
//        String strValue = (String) value;
//        DateTimeFormatter dtf = DateTimeFormat.forPattern(config.datePattern());
//        return dtf.parseDateTime(strValue).toDate();
//
////        List<Object> filenameArgs = RegexExtractor.extract(filename, config.filenamePattern());
//    }
//
//    private void assertFilenamePattern(String filename) {
//        if (!config.filenamePattern().matches(filename)) {
//            throw new MapperException("filename pattern does not match!");
//        };
//    }
//
//
//}
