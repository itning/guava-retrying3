package io.github.itning.retry.strategy.limit;

import javax.annotation.concurrent.Immutable;
import java.util.concurrent.Callable;

/**
 * No Attempt Time Limit
 *
 * @author itning
 * @since 3.0.0
 */
@Immutable
public final class NoAttemptTimeLimit<V> implements AttemptTimeLimiter<V> {
    @Override
    public V call(Callable<V> callable) throws Exception {
        return callable.call();
    }
}
