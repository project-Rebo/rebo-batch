package hello.rebobatch.domain.store.job;

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
public class CsvToStagingJobConfig {

    private final JobRepository jobRepository;
    private final Step csvToStagingStep;
    private final CsvPartitioner csvPartitioner;

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(8);
        executor.setThreadNamePrefix("csv-worker-");
        executor.initialize();

        return  executor;
    }

    @Bean
    public Step partitionedCsvToStagingStep() {
        return new StepBuilder("partitionedCsvToStagingStep", jobRepository)
                .partitioner("csvToStagingStep", csvPartitioner)
                .step(csvToStagingStep)
                .taskExecutor(taskExecutor())
                .gridSize(17)
                .build();
    }

    @Bean
    public Job csvToStagingJob() {
        return new JobBuilder("csvToStagingJob", jobRepository)
                .start(partitionedCsvToStagingStep())
                .build();
    }
}
