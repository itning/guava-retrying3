package io.github.itning.retry.strategy.stop;

import com.google.common.base.Preconditions;
import io.github.itning.retry.Attempt;

import javax.annotation.concurrent.Immutable;

/**
 * @author itning
 * @since 3.0.0
 */
@Immutable
public final class StopAfterDelayStrategy implements StopStrategy {
    private final long maxDelay;

    public StopAfterDelayStrategy(long maxDelay) {
        Preconditions.checkArgument(maxDelay >= 0L, "maxDelay must be >= 0 but is %s", maxDelay);
        this.maxDelay = maxDelay;
    }

    @Override
    public boolean shouldStop(Attempt failedAttempt) {
        return failedAttempt.getDelaySinceFirstAttempt() >= maxDelay;
    }
}