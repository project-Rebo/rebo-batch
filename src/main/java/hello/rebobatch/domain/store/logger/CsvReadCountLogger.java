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
        String fileName = stepExecution.getExecutionContext().getString("filePath");
        long readCount = stepExecution.getReadCount();
        long skipCount = stepExecution.getSkipCount();
        long writeCount = stepExecution.getWriteCount();
        long filterCount = stepExecution.getFilterCount();

        log.info("------------------------------------------------------");
        log.info("📄 처리 중인 파일 경로: {}", fileName);
        log.info("✅ 파티션 [{}] - 읽은 총 행 수: {}", stepExecution.getStepName(), readCount);
        log.info("⚠️ 파티션 [{}] - 스킵된 행 수 (에러 or 필터): {}", stepExecution.getStepName(), skipCount);
        log.info("✏️ 파티션 [{}] - 최종 저장된 행 수: {}", stepExecution.getStepName(), writeCount);
        log.info("🚫 파티션 [{}] - Processor에서 필터된 행 수: {}", stepExecution.getStepName(), filterCount);

        return stepExecution.getExitStatus();
    }
}
