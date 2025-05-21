package hello.rebobatch.domain.store.writer;

import hello.backend.batch.staging2.CsvRawDataDto;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class ErrorCsvWriterConfig {

    @Bean
    @StepScope
    public FlatFileItemWriter<CsvRawDataDto> errorCsvWriter(
            @Value("#{jobParameters['errorFile']}") String errorFilePath) {
        return new FlatFileItemWriterBuilder<CsvRawDataDto>()
                .name("errorCsvWriter")
                .resource(new FileSystemResource(errorFilePath))
                .lineAggregator(new DelimitedLineAggregator<>() {{
                    setDelimiter(",");
                    setFieldExtractor(new BeanWrapperFieldExtractor<>() {{
                        setNames(new String[]{"csvId", "name", "roadAddress", "latitude", "longitude"});
                    }});
                }})
                .append(true)
                .build();
    }
}