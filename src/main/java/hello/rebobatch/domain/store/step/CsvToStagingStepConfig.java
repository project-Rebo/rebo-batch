package hello.backend.batch.staging2;

import hello.backend.batch.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class CsvToStagingStepConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final SkipPolicy skipPolicy;

    @Bean
    public Step csvToStagingStep(
            @Qualifier("storeCsvReader") ItemReader<CsvRawDataDto> reader,
            @Qualifier("storeStagingProcessor") ItemProcessor<CsvRawDataDto, StoreStagingData> processor,
            @Qualifier("storeStagingWriter") ItemWriter<StoreStagingData> writer,
            @Qualifier("errorCsvWriter") FlatFileItemWriter<CsvRawDataDto> errorWriter
    ) {
        return new StepBuilder("csvToStagingStep", jobRepository)
                .<CsvRawDataDto, StoreStagingData>chunk(1000, platformTransactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skipPolicy(skipPolicy)
                .skip(ValidationException.class)
                .skip(FlatFileParseException.class)
                .skipLimit(10000)
                .listener(new CsvReadCountLogger())
                .listener(new StoreSkipListener(errorWriter))
                .build();
    }
}
