package io.github.itning.retry.strategy.stop;

import io.github.itning.retry.Attempt;

import javax.annotation.concurrent.Immutable;

/**
 * @author itning
 * @since 3.0.0
 */
@Immutable
public final class NeverStopStrategy implements StopStrategy {
    public static final StopStrategy INSTANCE = new NeverStopStrategy();

    private NeverStopStrategy() {
    }

    @Override
    public boolean shouldStop(Attempt failedAttempt) {
        return false;
    }
}

