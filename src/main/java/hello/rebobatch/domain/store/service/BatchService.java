package hello.rebobatch.domain.store.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchService {

    private final JobLauncher jobLauncher;
    private final Job stagingToMainJob;

    public void runStoreJob(String filePath, String errorPath) {

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        if (!errorPath.endsWith("/")) {
            errorPath += "/";
        }

        String errorCsvPath = errorPath + "error_" + timestamp + ".csv";

        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("filePath", filePath)
                    .addString("errorPath", errorCsvPath)
                    .addString("run.id", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
                    .toJobParameters();

            log.info("🚀 Staging → Main 배치 작업 시작");
            jobLauncher.run(stagingToMainJob, params);
            log.info("✅ 배치 작업 실행 요청 완료");
        } catch (Exception e) {
            log.error("❌ 배치 실행 중 오류 발생", e);
            throw new RuntimeException("배치 실행 실패", e);
        }
    }
}
