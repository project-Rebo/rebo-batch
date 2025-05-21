package hello.rebobatch.domain.store.reader;

import hello.rebobatch.domain.store.mapper.SafeLineMapper;
import hello.rebobatch.domain.store.dto.CsvRawDataDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Slf4j
@Configuration
public class StoreStagingReaderConfig {

    @Bean
    @StepScope
    public FlatFileItemReader<CsvRawDataDto> storeCsvReader(
            @Value("#{stepExecutionContext['filePath']}") String filePath
    ) {
        log.info("읽어들일 파일 경로: {}", filePath);

        FlatFileItemReader<CsvRawDataDto> reader = new FlatFileItemReader<>();
        reader.setName("storeCsvReader");
        reader.setResource(new FileSystemResource(filePath));
        reader.setLinesToSkip(1);
        reader.setEncoding("UTF-8");

        // SafeLineMapper 설정
        reader.setLineMapper(new SafeLineMapper(delimitedTokenizer(), fieldSetMapper()));

        return reader;
    }

    private DelimitedLineTokenizer delimitedTokenizer() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setNames(
                "csvId", "storeName", "branchName", "categoryLargeCode", "categoryLarge", "categoryMiddleCode", "categoryMiddle",
                "categorySmallCode", "categorySmall", "industryCode", "industryName", "sidoCode", "sidoName", "sigunguCode", "sigunguName",
                "adminDongCode", "adminDongName", "legalDongCode", "legalDongName", "lotNumberCode", "lotDivisionCode", "lotDivisionName",
                "lotMainNumber", "lotSubNumber", "lotAddress", "roadNameCode", "roadName", "buildingMainNumber", "buildingSubNumber",
                "buildingMgmtNumber", "buildingName", "roadAddress", "oldZipCode", "newZipCode", "dongInfo", "floorInfo", "unitInfo",
                "longitude", "latitude"
        );
        return tokenizer;
    }

    private BeanWrapperFieldSetMapper<CsvRawDataDto> fieldSetMapper() {
        BeanWrapperFieldSetMapper<CsvRawDataDto> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setTargetType(CsvRawDataDto.class);
        return mapper;
    }
}
