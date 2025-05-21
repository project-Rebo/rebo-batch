package hello.rebobatch.domain.store.listener;

import hello.backend.batch.staging2.CsvRawDataDto;
import hello.backend.batch.staging2.StoreStagingData;
import hello.backend.store.domain.Store;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.file.FlatFileItemWriter;

import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class StoreToMainSkipListener implements SkipListener<Store,StoreStagingData> {

    private final FlatFileItemWriter<CsvRawDataDto> errorWriter;

    @Override
    public void onSkipInWrite(StoreStagingData item, Throwable t) {
        log.warn("[WRITE SKIP to MAIN] item: {}, reason: {}", item, t.getMessage());
        CsvRawDataDto dto = convertBack(item); // 직접 매핑 로직 구현
        try {
            synchronized (this) {
                errorWriter.write(new Chunk<>(Collections.singletonList(dto)));
            }
        }catch (Exception e) {
            log.error("❌ main error.csv 기록 실패", e);
        }
    }

    private CsvRawDataDto convertBack(StoreStagingData item) {
        return CsvRawDataDto.builder()
                .csvId(item.getCsvId())
                .storeName(item.getStoreName())  // StoreStagingData 필드에 맞게
                .roadAddress(item.getRoadAddress())
                .latitude(item.getLatitude())
                .longitude(item.getLongitude())
                .build();
    }
}
