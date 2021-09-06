package io.github.itning.retry.strategy.wait;

import io.github.itning.retry.Attempt;

import javax.annotation.concurrent.Immutable;
import java.util.List;

/**
 * @author itning
 * @since 3.0.0
 */
@Immutable
public final class CompositeWaitStrategy implements WaitStrategy {
    private final List<WaitStrategy> waitStrategies;

    public CompositeWaitStrategy(List<WaitStrategy> waitStrategies) {
        if (waitStrategies.isEmpty()) {
            throw new IllegalStateException("Need at least one wait strategy");
        }
        this.waitStrategies = waitStrategies;
    }

    @Override
    public long computeSleepTime(Attempt failedAttempt) {
        long waitTime = 0L;
        for (WaitStrategy waitStrategy : waitStrategies) {
            waitTime += waitStrategy.computeSleepTime(failedAttempt);
        }
        return waitTime;
    }
}
