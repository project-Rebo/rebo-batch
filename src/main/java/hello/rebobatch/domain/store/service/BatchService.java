package hello.rebobatch.domain.store.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchService {

    private final JobLauncher jobLauncher;
    private final Job csvToMainJob;

    public void runStoreJob(String filePath, String errorPath) {

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        if (!errorPath.endsWith("/")) {
            errorPath += "/";
        }

        // 에러 디렉터리 생성 보장
        File errorDir = new File(errorPath);
        if (!errorDir.exists()) {
            boolean created = errorDir.mkdirs();
            log.info("📁 에러 디렉터리 생성: {} - {}", errorPath, created ? "성공" : "실패");
        }

        String errorCsvPath = errorPath + "error_" + timestamp + ".csv";

        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("filePath", filePath)
                    .addString("errorFile", errorCsvPath)  // errorPath → errorFile 수정
                    .addString("run.id", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
                    .toJobParameters();

            log.info("🚀 CSV → Main 배치 작업 시작");
            log.info("📁 에러 파일 경로: {}", errorCsvPath);
            jobLauncher.run(csvToMainJob, params);
            log.info("✅ 배치 작업 실행 요청 완료");
        } catch (Exception e) {
            log.error("❌ 배치 실행 중 오류 발생", e);
            throw new RuntimeException("배치 실행 실패", e);
        }
    }
}
