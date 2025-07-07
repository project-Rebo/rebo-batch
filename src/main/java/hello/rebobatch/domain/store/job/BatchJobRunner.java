package hello.rebobatch.domain.store.job;

import hello.rebobatch.domain.store.service.BatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BatchJobRunner implements ApplicationRunner {

    private final BatchService batchService;

    @Override
    public void run(ApplicationArguments args) {
        String filePath = "/Users/mac/stores/202503/data/";
        String errorPath = "/Users/mac/stores/202503/error/";

        batchService.runStoreJob(filePath, errorPath);
    }
}
