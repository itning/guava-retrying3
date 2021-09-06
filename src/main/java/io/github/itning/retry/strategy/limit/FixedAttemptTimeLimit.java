package io.github.itning.retry.strategy.limit;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Fixed Attempt Time Limit
 *
 * @author itning
 * @since 3.0.0
 */
@Immutable
public final class FixedAttemptTimeLimit<V> implements AttemptTimeLimiter<V> {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private final TimeLimiter timeLimiter;
    private final long duration;
    private final TimeUnit timeUnit;

    public FixedAttemptTimeLimit(long duration, @Nonnull TimeUnit timeUnit) {
        this(SimpleTimeLimiter.create(EXECUTOR_SERVICE), duration, timeUnit);
    }

    public FixedAttemptTimeLimit(long duration, @Nonnull TimeUnit timeUnit, @Nonnull ExecutorService executorService) {
        this(SimpleTimeLimiter.create(executorService), duration, timeUnit);
    }

    private FixedAttemptTimeLimit(@Nonnull TimeLimiter timeLimiter, long duration, @Nonnull TimeUnit timeUnit) {
        Preconditions.checkNotNull(timeLimiter);
        Preconditions.checkNotNull(timeUnit);
        this.timeLimiter = timeLimiter;
        this.duration = duration;
        this.timeUnit = timeUnit;
    }

    @Override
    public V call(Callable<V> callable) throws Exception {
        return timeLimiter.callWithTimeout(callable, duration, timeUnit);
    }
}