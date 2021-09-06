package io.github.itning.retry.strategy.block;

import javax.annotation.concurrent.Immutable;

/**
 * @author itning
 * @since 3.0.0
 */
@Immutable
public final class ThreadSleepStrategy implements BlockStrategy {

    public static final BlockStrategy INSTANCE = new ThreadSleepStrategy();

    private ThreadSleepStrategy() {
    }

    @Override
    public void block(long sleepTime) throws InterruptedException {
        Thread.sleep(sleepTime);
    }
}