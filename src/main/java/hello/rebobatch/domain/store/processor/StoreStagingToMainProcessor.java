package hello.rebobatch.domain.store.processor;

import hello.backend.batch.staging2.StoreStagingData;
import hello.backend.store.domain.Store;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Slf4j
@Component
public class StoreStagingToMainProcessor implements ItemProcessor<StoreStagingData, Store> {

    @Override
    public Store process(StoreStagingData item) {

        BigDecimal lat = toDecimal(item.getLatitude());
        BigDecimal lon = toDecimal(item.getLongitude());

        if (lat == null || lon == null) {
            log.warn("⚠️ 위도/경도 변환 실패. skip: {}", item.getCsvId());
            return null; // → 전체 row skip
        }

        return Store.builder()
                .csvId(item.getCsvId())
                .name(item.getStoreName())
                .categoryLarge(item.getCategoryLarge())
                .categoryMiddle(item.getCategoryMiddle())
                .categorySmall(item.getCategorySmall())
                .roadAddress(item.getRoadAddress())
                .latitude(lat)
                .longitude(lon)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
    }

    private BigDecimal toDecimal(String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            log.error("❌ 숫자 변환 실패: {}", value);
            return null;
        }
    }
}
