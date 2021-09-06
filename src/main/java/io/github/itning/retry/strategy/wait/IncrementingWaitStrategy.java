package io.github.itning.retry.strategy.wait;

import io.github.itning.retry.Attempt;

import javax.annotation.concurrent.Immutable;

/**
 * @author itning
 * @since 3.0.0
 */
@Immutable
public final class IncrementingWaitStrategy implements WaitStrategy {
    private final long initialSleepTime;
    private final long increment;

    public IncrementingWaitStrategy(long initialSleepTime,
                                    long increment) {
        if (initialSleepTime < 0L) {
            throw new IllegalArgumentException("initialSleepTime must be >= 0 but is " + initialSleepTime);
        }

        this.initialSleepTime = initialSleepTime;
        this.increment = increment;
    }

    @Override
    public long computeSleepTime(Attempt failedAttempt) {
        long result = initialSleepTime + (increment * (failedAttempt.getAttemptNumber() - 1));
        return Math.max(result, 0L);
    }
}