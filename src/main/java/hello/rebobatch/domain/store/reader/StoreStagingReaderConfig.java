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
            @Value("#{stepExecutionContext['virtualFiles']}") String virtualFiles
    ) {
        log.info("가상파일 정보: {}", virtualFiles);

        // 가상파일 정보 파싱: "파일경로,시작라인,끝라인" (파티션당 하나의 가상파일)
        String[] parts = virtualFiles.split(",");
        String filePath = parts[0];
        long startLine = Long.parseLong(parts[1]);
        long endLine = Long.parseLong(parts[2]);
        long lineCount = endLine - startLine + 1;
        
        log.info("📄 처리할 가상파일: {} [라인 {}-{}] ({} 행)", 
            filePath.substring(filePath.lastIndexOf('/') + 1), 
            startLine, endLine, lineCount);
        
        FlatFileItemReader<CsvRawDataDto> reader = new FlatFileItemReader<>();
        reader.setName("virtualFileReader");
        reader.setResource(new FileSystemResource(filePath));
        reader.setLinesToSkip((int) (startLine - 1)); // 시작 라인 전까지 스킵 (헤더 포함)
        reader.setMaxItemCount((int) lineCount); // 읽을 라인 수 제한
        reader.setEncoding("UTF-8");
        
        // SafeLineMapper 설정
        reader.setLineMapper(new SafeLineMapper(delimitedTokenizer(), fieldSetMapper()));
        
        log.info("🔧 Reader 설정 완료: Skip {} lines, Read {} lines", 
            startLine - 1, lineCount);
        
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
