package io.github.itning.retry.strategy.wait;

import io.github.itning.retry.Attempt;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.function.Function;

/**
 * @author itning
 * @since 3.0.0
 */
@Immutable
public final class ExceptionWaitStrategy<T extends Throwable> implements WaitStrategy {
    private final Class<T> exceptionClass;
    private final Function<T, Long> function;

    public ExceptionWaitStrategy(@Nonnull Class<T> exceptionClass, @Nonnull Function<T, Long> function) {
        this.exceptionClass = exceptionClass;
        this.function = function;
    }

    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored", "unchecked"})
    @Override
    public long computeSleepTime(Attempt lastAttempt) {
        if (lastAttempt.hasException()) {
            Throwable cause = lastAttempt.getExceptionCause();
            if (exceptionClass.isAssignableFrom(cause.getClass())) {
                return function.apply((T) cause);
            }
        }
        return 0L;
    }
}
