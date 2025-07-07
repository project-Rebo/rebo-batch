package hello.rebobatch.domain.store.logger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import javax.lang.model.element.NestingKind;

@Slf4j
public class CsvReadCountLogger implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("✅ CSV Reader 시작");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        String virtualFiles = stepExecution.getExecutionContext().getString("virtualFiles");
        long readCount = stepExecution.getReadCount();
        long skipCount = stepExecution.getSkipCount();
        long writeCount = stepExecution.getWriteCount();
        long filterCount = stepExecution.getFilterCount();

        log.info("------------------------------------------------------");
        log.info("📄 처리한 가상파일 정보: {}", virtualFiles);
        log.info("✅ 파티션 [{}] - 읽은 총 행 수: {}", stepExecution.getStepName(), readCount);
        log.info("⚠️ 파티션 [{}] - 스킵된 행 수 (에러 or 필터): {}", stepExecution.getStepName(), skipCount);
        log.info("✏️ 파티션 [{}] - 최종 저장된 행 수: {}", stepExecution.getStepName(), writeCount);
        log.info("🔄 파티션 [{}] - 필터된 행 수: {}", stepExecution.getStepName(), filterCount);
        
        // 실행 시간 계산
        if (stepExecution.getStartTime() != null && stepExecution.getEndTime() != null) {
            long executionTimeMs = java.time.Duration.between(stepExecution.getStartTime(), stepExecution.getEndTime()).toMillis();
            log.info("⏱️ 파티션 [{}] - 실행 시간: {}ms", stepExecution.getStepName(), executionTimeMs);
        }
        
        log.info("------------------------------------------------------");

        return ExitStatus.COMPLETED;
    }
}
