package io.github.itning.retry.strategy.wait;

import com.google.common.base.Preconditions;
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
        Preconditions.checkArgument(multiplier > 0L, "multiplier must be > 0 but is %s", multiplier);
        Preconditions.checkArgument(maximumWait >= 0L, "maximumWait must be >= 0 but is %s", maximumWait);
        Preconditions.checkArgument(multiplier < maximumWait, "multiplier must be < maximumWait but is %s", multiplier);
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