package io.github.itning.retry.strategy.wait;

import com.google.common.base.Preconditions;
import io.github.itning.retry.Attempt;

import javax.annotation.concurrent.Immutable;
import java.util.Random;

/**
 * @author itning
 * @since 2021/9/6 18:23
 */
@Immutable
public final class RandomWaitStrategy implements WaitStrategy {
    private static final Random RANDOM = new Random();
    private final long minimum;
    private final long maximum;

    public RandomWaitStrategy(long minimum, long maximum) {
        Preconditions.checkArgument(minimum >= 0, "minimum must be >= 0 but is %s", minimum);
        Preconditions.checkArgument(maximum > minimum, "maximum must be > minimum but maximum is %s and minimum is", maximum, minimum);

        this.minimum = minimum;
        this.maximum = maximum;
    }

    @Override
    public long computeSleepTime(Attempt failedAttempt) {
        long t = Math.abs(RANDOM.nextLong()) % (maximum - minimum);
        return t + minimum;
    }
}