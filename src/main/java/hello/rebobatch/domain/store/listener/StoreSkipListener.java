package hello.rebobatch.domain.store.listener;

import hello.rebobatch.domain.store.dto.CsvRawDataDto;
import hello.rebobatch.domain.store.entity.Store;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.file.FlatFileItemWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class StoreSkipListener implements SkipListener<CsvRawDataDto, Store> {

    private final FlatFileItemWriter<CsvRawDataDto> errorWriter;
    private static final String ERROR_DIR = "/Users/mac/stores/202503/error/";

    @Override
    public void onSkipInRead(Throwable t) {
        log.warn("❌ [READ SKIP] 파일 읽기 중 에러: {}", t.getMessage());
    }

    @Override
    public void onSkipInProcess(CsvRawDataDto item, Throwable t) {
        log.warn("❌ [PROCESS SKIP] 데이터 처리 중 에러: csvId={}, reason={}", 
                item != null ? item.getCsvId() : "null", t.getMessage());
        
        if (item != null) {
            // 방법 1: Writer 사용 시도
            try {
                errorWriter.write(new Chunk<>(Collections.singletonList(item)));
                log.info("📝 에러 파일에 기록 (Writer): csvId={}", item.getCsvId());
            } catch (Exception e) {
                log.warn("⚠️ Writer 방식 실패: {}", e.getMessage());
                
                // 방법 2: 직접 파일 쓰기 (fallback)
                writeErrorToFile(item, t);
            }
        }
    }

    @Override
    public void onSkipInWrite(Store item, Throwable t) {
        log.warn("❌ [WRITE SKIP] DB 저장 중 에러: csvId={}, reason={}", 
                item != null ? item.getCsvId() : "null", t.getMessage());
    }
    
    private void writeErrorToFile(CsvRawDataDto item, Throwable t) {
        try {
            // 에러 디렉터리 생성
            File errorDir = new File(ERROR_DIR);
            if (!errorDir.exists()) {
                errorDir.mkdirs();
            }
            
            // 에러 파일명 생성 (timestamp 포함)
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String errorFileName = "error_direct_" + timestamp + ".csv";
            File errorFile = new File(errorDir, errorFileName);
            
            // 헤더 작성 (파일이 새로 생성된 경우)
            boolean isNewFile = !errorFile.exists();
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(errorFile, true))) {
                if (isNewFile) {
                    writer.println("csvId,storeName,roadAddress,latitude,longitude,categoryLarge,categoryMiddle,categorySmall,errorMessage");
                }
                
                // 에러 데이터 작성
                writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,\"%s\"%n",
                    nullSafe(item.getCsvId()),
                    nullSafe(item.getStoreName()), 
                    nullSafe(item.getRoadAddress()),
                    nullSafe(item.getLatitude()),
                    nullSafe(item.getLongitude()),
                    nullSafe(item.getCategoryLarge()),
                    nullSafe(item.getCategoryMiddle()),
                    nullSafe(item.getCategorySmall()),
                    nullSafe(t.getMessage())
                );
                
                log.info("📝 에러 파일에 직접 기록: {} -> {}", item.getCsvId(), errorFile.getName());
            }
            
        } catch (IOException e) {
            log.error("❌ 에러 파일 직접 작성도 실패: {}", e.getMessage());
        }
    }
    
    private String nullSafe(String value) {
        return value != null ? value.replace(",", ";").replace("\"", "'") : "";
    }
}
