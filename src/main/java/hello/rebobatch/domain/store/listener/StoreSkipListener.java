package hello.rebobatch.domain.store.listener;

import hello.rebobatch.domain.store.dto.CsvRawDataDto;
import hello.rebobatch.domain.store.entity.StoreStagingData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.file.FlatFileItemWriter;

import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class StoreSkipListener implements SkipListener<CsvRawDataDto, StoreStagingData> {

    private final FlatFileItemWriter<CsvRawDataDto> errorWriter;

    @Override
    public void onSkipInRead(Throwable t) {
        log.warn("[READ SKIP] reason: {}", t.getMessage());
    }

    @Override
    public void onSkipInProcess(CsvRawDataDto item, Throwable t) {
        log.warn("[PROCESS SKIP] item: {}, reason: {}", item, t.getMessage());
        try {
            synchronized (this) {
                errorWriter.write(new Chunk<>(Collections.singletonList(item)));
            }
        } catch (Exception e) {
            log.error("Failed to write skipped item to error.csv", e);
        }
    }

    @Override
    public void onSkipInWrite(StoreStagingData item, Throwable t) {
        log.warn("[WRITE SKIP] item: {}, reason: {}", item, t.getMessage());
    }
}
