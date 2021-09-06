package io.github.itning.retry.strategy.wait;

import io.github.itning.retry.Attempt;

import javax.annotation.concurrent.Immutable;
import java.util.Random;

/**
 * @author itning
 * @since 3.0.0
 */
@Immutable
public final class RandomWaitStrategy implements WaitStrategy {
    private static final Random RANDOM = new Random();
    private final long minimum;
    private final long maximum;

    public RandomWaitStrategy(long minimum, long maximum) {
        if (minimum < 0) {
            throw new IllegalArgumentException("minimum must be >= 0 but is " + minimum);
        }
        if (maximum <= minimum) {
            throw new IllegalArgumentException("maximum must be > minimum but maximum is " + maximum + " and minimum is " + minimum);
        }
        this.minimum = minimum;
        this.maximum = maximum;
    }

    @Override
    public long computeSleepTime(Attempt failedAttempt) {
        long t = Math.abs(RANDOM.nextLong()) % (maximum - minimum);
        return t + minimum;
    }
}