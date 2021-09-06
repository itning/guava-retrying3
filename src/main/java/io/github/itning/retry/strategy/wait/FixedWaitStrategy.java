package io.github.itning.retry.strategy.wait;

import com.google.common.base.Preconditions;
import io.github.itning.retry.Attempt;

import javax.annotation.concurrent.Immutable;

/**
 * @author itning
 * @since 3.0.0
 */
@Immutable
public final class FixedWaitStrategy implements WaitStrategy {
    private final long sleepTime;

    public FixedWaitStrategy(long sleepTime) {
        Preconditions.checkArgument(sleepTime >= 0L, "sleepTime must be >= 0 but is %s", sleepTime);
        this.sleepTime = sleepTime;
    }

    @Override
    public long computeSleepTime(Attempt failedAttempt) {
        return sleepTime;
    }
}