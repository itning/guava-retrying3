package io.github.itning.retry.strategy.stop;

import io.github.itning.retry.Attempt;

import javax.annotation.concurrent.Immutable;

/**
 * @author itning
 * @since 3.0.0
 */
@Immutable
public final class StopAfterAttemptStrategy implements StopStrategy {
    private final int maxAttemptNumber;

    public StopAfterAttemptStrategy(int maxAttemptNumber) {
        if (maxAttemptNumber < 1) {
            throw new IllegalArgumentException("maxAttemptNumber must be >= 1 but is " + maxAttemptNumber);
        }
        this.maxAttemptNumber = maxAttemptNumber;
    }

    @Override
    public boolean shouldStop(Attempt failedAttempt) {
        return failedAttempt.getAttemptNumber() >= maxAttemptNumber;
    }
}