package hello.rebobatch.domain.store.partitioner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StoreStagingToMainPartitioner implements Partitioner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Map<String, ExecutionContext> partition(int size) {
        Map<String, ExecutionContext> partitions = new HashMap<>();

        Long minId = jdbcTemplate.queryForObject("SELECT MIN(id) FROM stg_store", Long.class);
        Long maxId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM stg_store", Long.class);

        if (minId == null || maxId == null) {
            throw new IllegalStateException("stg_store 테이블에 데이터가 없습니다.");
        }

        long totalsize = ((maxId - minId + 1) / size) + 1;

        long start = minId;
        long end;

        for (int i = 0; i < size; i++) {
            end = Math.min(start + totalsize - 1, maxId);

            ExecutionContext context = new ExecutionContext();
            context.putLong("minId", start);
            context.putLong("maxId", end);
            partitions.put("partition" + i, context);

            log.info(">>>>> 파티션 {} → minId={}, maxId={}", i, start, end);

            start = end + 1;

            if (start > maxId) {
                break;
            }
        }

        return partitions;
    }
}
