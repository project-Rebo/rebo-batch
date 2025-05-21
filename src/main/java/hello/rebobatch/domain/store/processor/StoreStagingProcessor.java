package hello.rebobatch.domain.store.processor;

import hello.rebobatch.domain.store.dto.CsvRawDataDto;
import hello.rebobatch.domain.store.entity.StoreStagingData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class StoreStagingProcessor implements ItemProcessor<CsvRawDataDto, StoreStagingData> {
    @Override
    public StoreStagingData process(CsvRawDataDto dto) {
        if (dto.getCsvId() == null || dto.getStoreName() == null
                || dto.getRoadAddress() == null || dto.getLatitude() == null
                || dto.getLongitude() == null || dto.getCategoryLarge() == null
                || dto.getCategoryMiddle() == null || dto.getCategorySmall() == null) {
            log.error("❌ 유효하지 않은 데이터 발견: {}", dto);
            return null;
        }

        return StoreStagingData.builder()
                .csvId(dto.getCsvId())
                .storeName(dto.getStoreName())
                .roadAddress(dto.getRoadAddress())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .categoryLarge(dto.getCategoryLarge())
                .categoryMiddle(dto.getCategoryMiddle())
                .categorySmall(dto.getCategorySmall())
                .build();
    }
}