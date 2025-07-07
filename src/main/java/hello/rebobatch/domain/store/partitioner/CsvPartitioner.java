package hello.rebobatch.domain.store.partitioner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
public class CsvPartitioner implements Partitioner {

    private final ResourcePatternResolver resourcePatternResolver;
    private final String inputDirectoryPath;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> partitions = new HashMap<>();

        try {
            String filePattern = "file:" + inputDirectoryPath + "/*.csv";
            Resource[] resources = resourcePatternResolver.getResources(filePattern);
            
            // 1단계: 파일별 라인 수 계산
            List<FileInfo> fileInfos = new ArrayList<>();
            long totalLines = 0;
            
            for (Resource resource : resources) {
                long lineCount = countLines(resource.getFile().getAbsolutePath());
                fileInfos.add(new FileInfo(resource.getFile().getAbsolutePath(), lineCount));
                totalLines += lineCount;
                log.info("📄 파일: {} - 라인 수: {}", resource.getFilename(), lineCount);
            }
            
            log.info("📊 총 라인 수: {}, 파티션 수: {}", totalLines, gridSize);
            
            // 2단계: 파티션 수에 맞춰 가상파일 생성
            List<VirtualFile> virtualFiles = createOptimalVirtualFiles(fileInfos, totalLines, gridSize);
            
            // 3단계: 각 파티션에 하나씩 가상파일 배정
            assignVirtualFilesToPartitions(virtualFiles, partitions);
            
        } catch (Exception e) {
            log.error("CSV 파티셔닝 실패", e);
            throw new RuntimeException("CSV 파티셔닝 실패", e);
        }

        return partitions;
    }
    
    private List<VirtualFile> createOptimalVirtualFiles(List<FileInfo> fileInfos, long totalLines, int gridSize) {
        List<VirtualFile> virtualFiles = new ArrayList<>();
        
        // 파티션당 목표 라인 수 계산
        long targetLinesPerPartition = totalLines / gridSize;
        log.info("📏 파티션당 목표 라인 수: {}", targetLinesPerPartition);
        
        // 각 파일을 적절한 크기로 분할
        for (FileInfo fileInfo : fileInfos) {
            if (fileInfo.lineCount > targetLinesPerPartition * 1.15) {
                // 큰 파일은 목표 크기에 맞춰 분할
                int splits = (int) Math.ceil((double) fileInfo.lineCount / targetLinesPerPartition);
                long linesPerSplit = fileInfo.lineCount / splits;
                
                log.info("✂️ 대용량 파일 분할: {} ({} 행) → {} 개 가상파일", 
                    fileInfo.filePath.substring(fileInfo.filePath.lastIndexOf('/') + 1), 
                    fileInfo.lineCount, splits);
                
                for (int i = 0; i < splits; i++) {
                    long startLine = i * linesPerSplit + 1;
                    long endLine = (i == splits - 1) ? fileInfo.lineCount : (i + 1) * linesPerSplit;
                    VirtualFile virtualFile = new VirtualFile(fileInfo.filePath, startLine, endLine);
                    virtualFiles.add(virtualFile);
                    
                    log.info("  📋 가상파일 {}: {}", i + 1, virtualFile.toString());
                }
            } else {
                // 작은 파일은 그대로 하나의 가상파일로 생성
                VirtualFile virtualFile = new VirtualFile(fileInfo.filePath, 1, fileInfo.lineCount);
                virtualFiles.add(virtualFile);
                log.info("📄 단일 가상파일: {}", virtualFile.toString());
            }
        }
        
        log.info("🎯 생성된 가상파일 수: {} 개", virtualFiles.size());
        
        // 가상파일 수가 파티션 수보다 많으면 조정
        if (virtualFiles.size() > gridSize) {
            log.info("📦 가상파일 수({})가 파티션 수({})보다 많습니다. 균등 분배합니다.", 
                virtualFiles.size(), gridSize);
            virtualFiles = mergeVirtualFiles(virtualFiles, gridSize);
        }
        
        return virtualFiles;
    }
    
    private List<VirtualFile> mergeVirtualFiles(List<VirtualFile> virtualFiles, int gridSize) {
        // 가상파일들을 라인 수 기준으로 정렬 (큰 것부터)
        virtualFiles.sort((a, b) -> Long.compare(b.getLineCount(), a.getLineCount()));
        
        List<List<VirtualFile>> partitions = new ArrayList<>();
        long[] partitionSizes = new long[gridSize];
        
        // 파티션 초기화
        for (int i = 0; i < gridSize; i++) {
            partitions.add(new ArrayList<>());
        }
        
        // 각 가상파일을 가장 작은 파티션에 배치
        for (VirtualFile virtualFile : virtualFiles) {
            int minIndex = 0;
            for (int i = 1; i < gridSize; i++) {
                if (partitionSizes[i] < partitionSizes[minIndex]) {
                    minIndex = i;
                }
            }
            
            partitions.get(minIndex).add(virtualFile);
            partitionSizes[minIndex] += virtualFile.getLineCount();
        }
        
        // 병합된 가상파일 리스트 생성
        List<VirtualFile> mergedFiles = new ArrayList<>();
        for (int i = 0; i < gridSize; i++) {
            if (!partitions.get(i).isEmpty()) {
                // 첫 번째 가상파일을 대표로 사용
                VirtualFile representative = partitions.get(i).get(0);
                mergedFiles.add(representative);
                
                log.info("🎯 파티션 {}: {} 개 가상파일 병합 (총 {} 행)", 
                    i, partitions.get(i).size(), partitionSizes[i]);
                
                for (VirtualFile vf : partitions.get(i)) {
                    log.info("  📋 포함: {}", vf.toString());
                }
            }
        }
        
        return mergedFiles;
    }
    
    private void assignVirtualFilesToPartitions(List<VirtualFile> virtualFiles, Map<String, ExecutionContext> partitions) {
        for (int i = 0; i < virtualFiles.size(); i++) {
            VirtualFile virtualFile = virtualFiles.get(i);
            String partitionKey = "partition" + i;
            ExecutionContext executionContext = new ExecutionContext();
            
            // 가상파일 정보를 "파일경로,시작라인,끝라인" 형태로 저장
            String virtualFileInfo = virtualFile.getFilePath() + "," + 
                                   virtualFile.getStartLine() + "," + 
                                   virtualFile.getEndLine();
            
            executionContext.put("virtualFiles", virtualFileInfo);
            executionContext.put("partition", String.valueOf(i));  // 파티션 ID 추가
            
            partitions.put(partitionKey, executionContext);
            
            log.info("🎯 {} 파티션 할당: {}", partitionKey, virtualFileInfo);
        }
    }
    
    private long countLines(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            long lines = 0;
            while (reader.readLine() != null) {
                lines++;
            }
            return lines;
        }
    }
    
    private static class FileInfo {
        final String filePath;
        final long lineCount;
        
        FileInfo(String filePath, long lineCount) {
            this.filePath = filePath;
            this.lineCount = lineCount;
        }
    }
}
