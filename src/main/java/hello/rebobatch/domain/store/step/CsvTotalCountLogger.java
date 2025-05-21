package hello.rebobatch.domain.store.step;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;

@Slf4j
public class CsvTotalCountLogger implements JobExecutionListener {

    @Override
    public void afterJob(JobExecution jobExecution) {
        long totalRead = 0;
        long totalSkip = 0;
        long totalWrite = 0;
        long totalFilter = 0;

        for (StepExecution step : jobExecution.getStepExecutions()) {
            if (step.getStepName().startsWith("csvToStagingStep")) {
                totalRead += step.getReadCount();
                totalSkip += step.getSkipCount();
                totalWrite += step.getWriteCount();
                totalFilter += step.getFilterCount();
            }
        }

        log.info("📊 ===== 전체 통계 요약 =====");
        log.info("📦 전체 읽은 행 수: {}", totalRead);
        log.info("⚠️  전체 스킵된 행 수: {}", totalSkip);
        log.info("✏️  전체 저장된 행 수: {}", totalWrite);
        log.info("🚫 전체 필터된 행 수: {}", totalFilter);
    }
}
