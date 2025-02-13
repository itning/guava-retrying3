package io.github.itning.retry;

import io.github.itning.retry.strategy.limit.AttemptTimeLimiters;
import io.github.itning.retry.strategy.stop.StopStrategies;
import io.github.itning.retry.strategy.wait.WaitStrategies;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author itning
 * @since 3.0.0
 */
public class RetryWithRunnableTest {


    @Test
    void testRunnableApi() {
        AtomicInteger atomicInteger = new AtomicInteger();
        Retryer<Void> retryer = RetryerBuilder.<Void>newBuilder()
                .retryIfException()
                .withRetryListener(attempt -> {})
                .withWaitStrategy(WaitStrategies.fixedWait(1, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(5))
                .withAttemptTimeLimiter(AttemptTimeLimiters.noTimeLimit())
                .build();

        try {
            retryer.call(new Runnable() {
                @Override
                public void run() {
                    atomicInteger.incrementAndGet();
                }
            });
        } catch (ExecutionException e) {
            fail("ExecutionException expected");
        } catch (RetryException e) {
            fail("RetryException expected");
        }
    }
}
