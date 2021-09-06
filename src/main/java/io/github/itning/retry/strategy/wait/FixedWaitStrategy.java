package io.github.itning.retry.strategy.wait;

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
        if (sleepTime < 0L) {
            throw new IllegalArgumentException("sleepTime must be >= 0 but is " + sleepTime);
        }

        this.sleepTime = sleepTime;
    }

    @Override
    public long computeSleepTime(Attempt failedAttempt) {
        return sleepTime;
    }
}