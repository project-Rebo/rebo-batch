package hello.rebobatch.domain.store.writer;

import hello.rebobatch.domain.store.dto.CsvRawDataDto;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.File;

@Configuration
public class ErrorCsvWriterConfig {

    @Bean
    @StepScope
    public FlatFileItemWriter<CsvRawDataDto> errorCsvWriter(
            @Value("#{jobParameters['errorFile']}") String errorFilePath,
            @Value("#{stepExecutionContext['partition']}") String partitionId) {
        
        // 파티션별 고유 파일명 생성 (더 안전한 방식)
        String finalErrorFile = generateSafePartitionErrorFile(errorFilePath, partitionId);
        
        // 에러 파일 디렉터리 생성 보장
        File errorFile = new File(finalErrorFile);
        File errorDir = errorFile.getParentFile();
        if (!errorDir.exists()) {
            errorDir.mkdirs();
        }
        
        return new FlatFileItemWriterBuilder<CsvRawDataDto>()
                .name("errorCsvWriter")
                .resource(new FileSystemResource(finalErrorFile))
                .lineAggregator(new DelimitedLineAggregator<>() {{
                    setDelimiter(",");
                    setFieldExtractor(new BeanWrapperFieldExtractor<>() {{
                        setNames(new String[]{"csvId", "storeName", "roadAddress", "latitude", "longitude", "categoryLarge", "categoryMiddle", "categorySmall"});
                    }});
                }})
                .headerCallback(writer -> writer.write("csvId,storeName,roadAddress,latitude,longitude,categoryLarge,categoryMiddle,categorySmall"))
                .append(false)  // 새로운 파일 생성
                .shouldDeleteIfEmpty(false)  // 빈 파일이라도 삭제하지 않음
                .build();
    }
    
    private String generateSafePartitionErrorFile(String baseErrorFile, String partitionId) {
        try {
            // 기본 파일 경로에서 디렉터리와 파일명 분리
            File file = new File(baseErrorFile);
            String directory = file.getParent();
            String fileName = file.getName();
            
            // 확장자 분리
            int dotIndex = fileName.lastIndexOf('.');
            String nameWithoutExt = (dotIndex > 0) ? fileName.substring(0, dotIndex) : fileName;
            String extension = (dotIndex > 0) ? fileName.substring(dotIndex) : ".csv";
            
            // 파티션 ID가 있으면 파일명에 추가
            if (partitionId != null && !partitionId.trim().isEmpty()) {
                String partitionFileName = nameWithoutExt + "_partition_" + partitionId + extension;
                return new File(directory, partitionFileName).getAbsolutePath();
            } else {
                // 파티션 ID가 없으면 기본 파일명 사용
                return baseErrorFile;
            }
        } catch (Exception e) {
            // 예외 발생 시 기본 파일명 사용
            return baseErrorFile;
        }
    }
}