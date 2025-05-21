package hello.rebobatch.domain.store.partitioner;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class CsvPartitioner implements Partitioner {

    private final ResourcePatternResolver resourcePatternResolver;
    private final String inputDirectoryPath;

    @Override
    public Map<String, ExecutionContext> partition(int size) {
        Map<String, ExecutionContext> partitions = new HashMap<>();

        try {
            String filePattern = "file:" + inputDirectoryPath + "/*.csv";
            Resource[] resources = resourcePatternResolver.getResources(filePattern);

            for (int i = 0; i < resources.length; i++) {
                ExecutionContext context = new ExecutionContext();
                context.putString("filePath", resources[i].getFile().getAbsolutePath());
                partitions.put("partition" + i, context);
            }
        } catch (Exception e) {
            throw new RuntimeException("CSV 파티셔닝 실패", e);
        }

        return partitions;
    }
}
