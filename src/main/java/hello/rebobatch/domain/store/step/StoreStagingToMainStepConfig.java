package hello.rebobatch.domain.store.step;

import hello.rebobatch.domain.store.dto.CsvRawDataDto;
import hello.rebobatch.domain.store.entity.Store;
import hello.rebobatch.domain.store.entity.StoreStagingData;
import hello.rebobatch.domain.store.listener.StoreToMainSkipListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class StoreStagingToMainStepConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Step stagingToMainStep(
            @Qualifier("stagingToMainReader") ItemReader<StoreStagingData> reader,
            @Qualifier("storeStagingToMainProcessor") ItemProcessor<StoreStagingData, Store> processor,
            @Qualifier("storeWriter") ItemWriter<Store> writer,
            @Qualifier("errorCsvWriter") FlatFileItemWriter<CsvRawDataDto> errorCsvWriter) {
        return new StepBuilder("stagingToMainStep", jobRepository)
                .<StoreStagingData, Store>chunk(1000, platformTransactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skipLimit(10000)
                .skip(Exception.class)
                .listener(new StoreToMainSkipListener(errorCsvWriter))
                .build();

    }
}
