package io.github.itning.retry.strategy.wait;

import com.google.common.base.Preconditions;
import io.github.itning.retry.Attempt;

import javax.annotation.concurrent.Immutable;

/**
 * @author itning
 * @since 3.0.0
 */
@Immutable
public final class FibonacciWaitStrategy implements WaitStrategy {
    private final long multiplier;
    private final long maximumWait;

    public FibonacciWaitStrategy(long multiplier, long maximumWait) {
        Preconditions.checkArgument(multiplier > 0L, "multiplier must be > 0 but is %s", multiplier);
        Preconditions.checkArgument(maximumWait >= 0L, "maximumWait must be >= 0 but is %s", maximumWait);
        Preconditions.checkArgument(multiplier < maximumWait, "multiplier must be < maximumWait but is %s", multiplier);
        this.multiplier = multiplier;
        this.maximumWait = maximumWait;
    }

    @Override
    public long computeSleepTime(Attempt failedAttempt) {
        long fib = fib(failedAttempt.getAttemptNumber());
        long result = multiplier * fib;

        if (result > maximumWait || result < 0L) {
            result = maximumWait;
        }

        return Math.max(result, 0L);
    }

    private long fib(long n) {
        if (n == 0L) {
            return 0L;
        }
        if (n == 1L) {
            return 1L;
        }

        long prevPrev = 0L;
        long prev = 1L;
        long result = 0L;

        for (long i = 2L; i <= n; i++) {
            result = prev + prevPrev;
            prevPrev = prev;
            prev = result;
        }

        return result;
    }
}
