package hello.rebobatch.domain.store.policy;

import hello.rebobatch.global.error.ValidationException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchSkipPolicyConfig {

    @Bean
    public SkipPolicy skipPolicy() {
        return (Throwable t, long skipCount) -> {
            if (t instanceof ValidationException || t instanceof FlatFileParseException) {
                return true;
            }
            return false; // fail step
        };
    }
}
