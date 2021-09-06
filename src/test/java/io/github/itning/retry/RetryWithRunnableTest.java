package io.github.itning.retry;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author itning
 * @since 2021/9/6 13:41
 */
public class RetryWithRunnableTest {


    @Test
    void testRunnableApi() {
        AtomicInteger atomicInteger = new AtomicInteger();
        Retryer<Void> retryer = RetryerBuilder.<Void>newBuilder()
                .retryIfException()
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {

                    }
                })
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
