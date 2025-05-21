package hello.rebobatch.domain.store.mapper;

import hello.rebobatch.global.error.ValidationException;
import hello.rebobatch.domain.store.dto.CsvRawDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;

@Slf4j
@RequiredArgsConstructor
public class SafeLineMapper implements LineMapper<CsvRawDataDto> {

    private final LineTokenizer tokenizer;
    private final FieldSetMapper<CsvRawDataDto> fieldSetMapper;

    @Override
    public CsvRawDataDto mapLine(String line, int lineNumber) {
        try {
            FieldSet fieldSet = tokenizer.tokenize(line);
            return fieldSetMapper.mapFieldSet(fieldSet);
        } catch (Exception e) {
            log.warn("[READ SKIP] line {} -> {}", lineNumber, e.getMessage());
            throw new FlatFileParseException("Invalid line at " + lineNumber + ": " + e.getMessage(), e, line, lineNumber);
        }
    }
}
