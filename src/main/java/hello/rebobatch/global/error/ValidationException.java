package hello.backend.batch.exception; // 패키지 위치 확인

import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.context.annotation.Bean;

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}