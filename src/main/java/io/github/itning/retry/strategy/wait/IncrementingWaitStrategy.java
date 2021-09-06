package io.github.itning.retry.strategy.wait;

import com.google.common.base.Preconditions;
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
        Preconditions.checkArgument(initialSleepTime >= 0L, "initialSleepTime must be >= 0 but is %s", initialSleepTime);
        this.initialSleepTime = initialSleepTime;
        this.increment = increment;
    }

    @Override
    public long computeSleepTime(Attempt failedAttempt) {
        long result = initialSleepTime + (increment * (failedAttempt.getAttemptNumber() - 1));
        return Math.max(result, 0L);
    }
}