package hello.rebobatch;

import hello.rebobatch.domain.store.service.BatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class ReboBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReboBatchApplication.class, args);
    }
}

@Component
@RequiredArgsConstructor
@Slf4j
class BatchRunner implements ApplicationRunner {

    private final BatchService batchService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            log.info("🚀 배치 애플리케이션 시작");
            
            // 파라미터 설정
            String filePath = "/Users/mac/stores/202503/data/";
            String errorPath = "/Users/mac/stores/202503/error/";
            
            batchService.runStoreJob(filePath, errorPath);
            log.info("✅ 배치 애플리케이션 완료");
        } catch (Exception e) {
            log.error("❌ 배치 애플리케이션 실패", e);
            System.exit(1);
        }
        
        // 배치 완료 후 애플리케이션 종료
        System.exit(0);
    }
}
