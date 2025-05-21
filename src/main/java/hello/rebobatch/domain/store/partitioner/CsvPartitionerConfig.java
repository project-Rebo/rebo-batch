package hello.backend.batch.staging2;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.ResourcePatternResolver;

@Configuration
public class CsvPartitionerConfig {

    private final ResourcePatternResolver resourcePatternResolver;

    public CsvPartitionerConfig(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    @Bean
    @StepScope
    public CsvPartitioner csvPartitioner(@Value("#{jobParameters['filePath']}") String inputDirectoryPath) {
        return new CsvPartitioner(resourcePatternResolver, inputDirectoryPath);
    }
}