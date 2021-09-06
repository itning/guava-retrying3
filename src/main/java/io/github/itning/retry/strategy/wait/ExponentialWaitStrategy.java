package io.github.itning.retry.strategy.wait;

import io.github.itning.retry.Attempt;

import javax.annotation.concurrent.Immutable;

/**
 * @author itning
 * @since 3.0.0
 */
@Immutable
public final class ExponentialWaitStrategy implements WaitStrategy {
    private final long multiplier;
    private final long maximumWait;

    public ExponentialWaitStrategy(long multiplier,
                                   long maximumWait) {
        if (multiplier <= 0L) {
            throw new IllegalArgumentException("multiplier must be > 0 but is " + multiplier);
        }
        if (maximumWait < 0L) {
            throw new IllegalArgumentException("maximumWait must be >= 0 but is " + maximumWait);
        }
        if (multiplier >= maximumWait) {
            throw new IllegalArgumentException("multiplier must be < maximumWait but is " + multiplier);
        }

        this.multiplier = multiplier;
        this.maximumWait = maximumWait;
    }

    @Override
    public long computeSleepTime(Attempt failedAttempt) {
        double exp = Math.pow(2, failedAttempt.getAttemptNumber());
        long result = Math.round(multiplier * exp);
        if (result > maximumWait) {
            result = maximumWait;
        }
        return Math.max(result, 0L);
    }
}