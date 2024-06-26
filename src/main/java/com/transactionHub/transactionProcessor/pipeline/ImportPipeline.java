package com.transactionHub.transactionProcessor.pipeline;

import com.transactionHub.transactionCoreLibrary.constant.TransactionMeta;
import com.transactionHub.transactionCoreLibrary.domain.Transaction;
import com.transactionHub.transactionProcessor.extractor.Extractor;
import com.transactionHub.transactionProcessor.mapper.transaction.TransactionMapper;
import com.transactionHub.transactionProcessor.modifier.AdjustOffsetModifier;
import com.transactionHub.transactionProcessor.modifier.MetaUpserter;
import com.transactionHub.transactionProcessor.modifier.SystemTagger;
import com.transactionHub.transactionProcessor.modifier.Tagger;
import com.transactionHub.transactionProcessor.validator.NetSumZeroValidator;
import com.transactionHub.transactionProcessor.validator.OffsetSortedValidator;
import com.transactionHub.transactionProcessor.validator.RuleViolateException;
import com.transactionHub.transactionProcessor.validator.TransactionValidator;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ImportPipeline {

    protected final Extractor extractor;
    protected final TransactionMapper mapper;
    protected final Tagger tagger;
    protected final SystemTagger systemTagger;

    public ImportPipeline(Extractor extractor, TransactionMapper transactionMapper, Tagger tagger, SystemTagger systemTagger) {
        this.extractor = extractor;
        this.mapper = transactionMapper;
        this.tagger = tagger;
        this.systemTagger = systemTagger;
    }

    public List<Transaction> importData(InputStream inputStream, String filename) throws RuleViolateException {

        var metaUpserter = new MetaUpserter(Map.of(
                TransactionMeta.IMPORT_TIMESTAMP, DateTime.now(DateTimeZone.UTC).toString(),
                TransactionMeta.IMPORT_FILENAME, filename
        ));

        var result = extractor.extract(inputStream).stream()
                .map(mapper::map)
                .peek(tagger)
                .peek(systemTagger)
                .peek(metaUpserter)
                .toList();
        new AdjustOffsetModifier().accept(result);

        validateResult(result);

        return result;
    }

    protected void validateResult(List<Transaction> transactions) {
        transactions.stream()
                .peek(new TransactionValidator());

        Stream.iterate(0, n -> n + 1)
                .limit(transactions.size() - 1)
                .peek(i -> new OffsetSortedValidator().accept(transactions.get(i), transactions.get(i + 1)))
                .peek(i -> new NetSumZeroValidator().accept(transactions.get(i), transactions.get(i + 1)))
        ;
    }

}
