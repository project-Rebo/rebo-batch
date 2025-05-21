package hello.rebobatch.domain.store.job;

import hello.rebobatch.domain.store.partitioner.StoreStagingToMainPartitioner;
import hello.rebobatch.domain.store.step.CsvTotalCountLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class StagingToMainJobConfig {

    private final JobRepository jobRepository;
    private final StoreStagingToMainPartitioner storeStagingToMainPartitioner;
    private final Step stagingToMainStep;

    @Bean
    public TaskExecutor stagingTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(8);
        executor.setThreadNamePrefix("staging-worker-");
        executor.initialize();
        return executor;
    }

    @Bean
    public Step partitionedStagingToMainStep() {
        return new StepBuilder("partitionedStagingToMainStep", jobRepository)
                .partitioner("stagingToMainStep", storeStagingToMainPartitioner)
                .step(stagingToMainStep)
                .gridSize(8)
                .taskExecutor(stagingTaskExecutor())
                .build();
    }

    @Bean
    public Job stagingToMainJob(Step partitionedCsvToStagingStep) {
        return new JobBuilder("stagingToMainJob", jobRepository)
                .listener(new CsvTotalCountLogger())
                .start(partitionedCsvToStagingStep)
                .next(partitionedStagingToMainStep())
                .build();
    }
}
