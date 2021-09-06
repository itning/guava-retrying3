/*
 * Copyright 2012-2015 Ray Holder
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.itning.retry.strategy.wait;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Factory class for instances of {@link WaitStrategy}.
 *
 * @author JB
 */
public final class WaitStrategies {

    private static final WaitStrategy NO_WAIT_STRATEGY = new FixedWaitStrategy(0L);

    private WaitStrategies() {
    }

    /**
     * Returns a wait strategy that doesn't sleep at all before retrying. Use this at your own risk.
     *
     * @return a wait strategy that doesn't wait between retries
     */
    public static WaitStrategy noWait() {
        return NO_WAIT_STRATEGY;
    }

    /**
     * Returns a wait strategy that sleeps a fixed amount of time before retrying.
     *
     * @param sleepTime the time to sleep
     * @param timeUnit  the unit of the time to sleep
     * @return a wait strategy that sleeps a fixed amount of time
     * @throws IllegalStateException if the sleep time is &lt; 0
     */
    public static WaitStrategy fixedWait(long sleepTime, @Nonnull TimeUnit timeUnit) throws IllegalStateException {
        Objects.requireNonNull(timeUnit, "The time unit may not be null");
        return new FixedWaitStrategy(timeUnit.toMillis(sleepTime));
    }

    /**
     * Returns a strategy that sleeps a random amount of time before retrying.
     *
     * @param maximumTime the maximum time to sleep
     * @param timeUnit    the unit of the maximum time
     * @return a wait strategy with a random wait time
     * @throws IllegalStateException if the maximum sleep time is &lt;= 0.
     */
    public static WaitStrategy randomWait(long maximumTime, @Nonnull TimeUnit timeUnit) {
        Objects.requireNonNull(timeUnit, "The time unit may not be null");
        return new RandomWaitStrategy(0L, timeUnit.toMillis(maximumTime));
    }

    /**
     * Returns a strategy that sleeps a random amount of time before retrying.
     *
     * @param minimumTime     the minimum time to sleep
     * @param minimumTimeUnit the unit of the minimum time
     * @param maximumTime     the maximum time to sleep
     * @param maximumTimeUnit the unit of the maximum time
     * @return a wait strategy with a random wait time
     * @throws IllegalStateException if the minimum sleep time is &lt; 0, or if the
     *                               maximum sleep time is less than (or equals to) the minimum.
     */
    public static WaitStrategy randomWait(long minimumTime,
                                          @Nonnull TimeUnit minimumTimeUnit,
                                          long maximumTime,
                                          @Nonnull TimeUnit maximumTimeUnit) {
        Objects.requireNonNull(minimumTimeUnit, "The minimum time unit may not be null");
        Objects.requireNonNull(maximumTimeUnit, "The maximum time unit may not be null");
        return new RandomWaitStrategy(minimumTimeUnit.toMillis(minimumTime),
                maximumTimeUnit.toMillis(maximumTime));
    }

    /**
     * Returns a strategy that sleeps a fixed amount of time after the first
     * failed attempt and in incrementing amounts of time after each additional
     * failed attempt.
     *
     * @param initialSleepTime     the time to sleep before retrying the first time
     * @param initialSleepTimeUnit the unit of the initial sleep time
     * @param increment            the increment added to the previous sleep time after each failed attempt
     * @param incrementTimeUnit    the unit of the increment
     * @return a wait strategy that incrementally sleeps an additional fixed time after each failed attempt
     */
    public static WaitStrategy incrementingWait(long initialSleepTime,
                                                @Nonnull TimeUnit initialSleepTimeUnit,
                                                long increment,
                                                @Nonnull TimeUnit incrementTimeUnit) {
        Objects.requireNonNull(initialSleepTimeUnit, "The initial sleep time unit may not be null");
        Objects.requireNonNull(incrementTimeUnit, "The increment time unit may not be null");
        return new IncrementingWaitStrategy(initialSleepTimeUnit.toMillis(initialSleepTime),
                incrementTimeUnit.toMillis(increment));
    }

    /**
     * Returns a strategy which sleeps for an exponential amount of time after the first failed attempt,
     * and in exponentially incrementing amounts after each failed attempt up to Long.MAX_VALUE.
     *
     * @return a wait strategy that increments with each failed attempt using exponential backoff
     */
    public static WaitStrategy exponentialWait() {
        return new ExponentialWaitStrategy(1, Long.MAX_VALUE);
    }

    /**
     * Returns a strategy which sleeps for an exponential amount of time after the first failed attempt,
     * and in exponentially incrementing amounts after each failed attempt up to the maximumTime.
     *
     * @param maximumTime     the maximum time to sleep
     * @param maximumTimeUnit the unit of the maximum time
     * @return a wait strategy that increments with each failed attempt using exponential backoff
     */
    public static WaitStrategy exponentialWait(long maximumTime,
                                               @Nonnull TimeUnit maximumTimeUnit) {
        Objects.requireNonNull(maximumTimeUnit, "The maximum time unit may not be null");
        return new ExponentialWaitStrategy(1, maximumTimeUnit.toMillis(maximumTime));
    }

    /**
     * Returns a strategy which sleeps for an exponential amount of time after the first failed attempt,
     * and in exponentially incrementing amounts after each failed attempt up to the maximumTime.
     * The wait time between the retries can be controlled by the multiplier.
     * nextWaitTime = exponentialIncrement * {@code multiplier}.
     *
     * @param multiplier      multiply the wait time calculated by this
     * @param maximumTime     the maximum time to sleep
     * @param maximumTimeUnit the unit of the maximum time
     * @return a wait strategy that increments with each failed attempt using exponential backoff
     */
    public static WaitStrategy exponentialWait(long multiplier,
                                               long maximumTime,
                                               @Nonnull TimeUnit maximumTimeUnit) {
        Objects.requireNonNull(maximumTimeUnit, "The maximum time unit may not be null");
        return new ExponentialWaitStrategy(multiplier, maximumTimeUnit.toMillis(maximumTime));
    }

    /**
     * Returns a strategy which sleeps for an increasing amount of time after the first failed attempt,
     * and in Fibonacci increments after each failed attempt up to {@link Long#MAX_VALUE}.
     *
     * @return a wait strategy that increments with each failed attempt using a Fibonacci sequence
     */
    public static WaitStrategy fibonacciWait() {
        return new FibonacciWaitStrategy(1, Long.MAX_VALUE);
    }

    /**
     * Returns a strategy which sleeps for an increasing amount of time after the first failed attempt,
     * and in Fibonacci increments after each failed attempt up to the {@code maximumTime}.
     *
     * @param maximumTime     the maximum time to sleep
     * @param maximumTimeUnit the unit of the maximum time
     * @return a wait strategy that increments with each failed attempt using a Fibonacci sequence
     */
    public static WaitStrategy fibonacciWait(long maximumTime,
                                             @Nonnull TimeUnit maximumTimeUnit) {
        Objects.requireNonNull(maximumTimeUnit, "The maximum time unit may not be null");
        return new FibonacciWaitStrategy(1, maximumTimeUnit.toMillis(maximumTime));
    }

    /**
     * Returns a strategy which sleeps for an increasing amount of time after the first failed attempt,
     * and in Fibonacci increments after each failed attempt up to the {@code maximumTime}.
     * The wait time between the retries can be controlled by the multiplier.
     * nextWaitTime = fibonacciIncrement * {@code multiplier}.
     *
     * @param multiplier      multiply the wait time calculated by this
     * @param maximumTime     the maximum time to sleep
     * @param maximumTimeUnit the unit of the maximum time
     * @return a wait strategy that increments with each failed attempt using a Fibonacci sequence
     */
    public static WaitStrategy fibonacciWait(long multiplier,
                                             long maximumTime,
                                             @Nonnull TimeUnit maximumTimeUnit) {
        Objects.requireNonNull(maximumTimeUnit, "The maximum time unit may not be null");
        return new FibonacciWaitStrategy(multiplier, maximumTimeUnit.toMillis(maximumTime));
    }

    /**
     * Returns a strategy which sleeps for an amount of time based on the Exception that occurred. The
     * {@code function} determines how the sleep time should be calculated for the given
     * {@code exceptionClass}. If the exception does not match, a wait time of 0 is returned.
     *
     * @param function       function to calculate sleep time
     * @param exceptionClass class to calculate sleep time from
     * @return a wait strategy calculated from the failed attempt
     */
    public static <T extends Throwable> WaitStrategy exceptionWait(@Nonnull Class<T> exceptionClass,
                                                                   @Nonnull Function<T, Long> function) {
        Objects.requireNonNull(exceptionClass, "exceptionClass may not be null");
        Objects.requireNonNull(function, "function may not be null");
        return new ExceptionWaitStrategy<>(exceptionClass, function);
    }

    /**
     * Joins one or more wait strategies to derive a composite wait strategy.
     * The new joined strategy will have a wait time which is total of all wait times computed one after another in order.
     *
     * @param waitStrategies Wait strategies that need to be applied one after another for computing the sleep time.
     * @return A composite wait strategy
     */
    public static WaitStrategy join(WaitStrategy... waitStrategies) {
        Preconditions.checkState(waitStrategies.length > 0, "Must have at least one wait strategy");
        List<WaitStrategy> waitStrategyList = Lists.newArrayList(waitStrategies);
        Preconditions.checkState(!waitStrategyList.contains(null), "Cannot have a null wait strategy");
        return new CompositeWaitStrategy(waitStrategyList);
    }
}
