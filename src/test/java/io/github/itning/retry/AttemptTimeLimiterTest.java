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

package io.github.itning.retry;

import io.github.itning.retry.strategy.limit.AttemptTimeLimiters;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Jason Dunkelberger (dirkraft)
 */
public class AttemptTimeLimiterTest {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    Retryer<Void> r = RetryerBuilder.<Void>newBuilder()
            .withAttemptTimeLimiter(AttemptTimeLimiters.fixedTimeLimit(1, TimeUnit.SECONDS, EXECUTOR_SERVICE))
            .build();

    @Test
    public void testAttemptTimeLimit() throws ExecutionException, RetryException {
        try {
            r.call(new SleepyOut(0L));
        } catch (ExecutionException e) {
            fail("Should not timeout");
        }

        try {
            r.call(new SleepyOut(10 * 1000L));
            fail("Expected timeout exception");
        } catch (ExecutionException e) {
            // expected
            assertEquals(TimeoutException.class, e.getCause().getClass());
        }
    }

    static class SleepyOut implements Callable<Void> {

        final long sleepMs;

        SleepyOut(long sleepMs) {
            this.sleepMs = sleepMs;
        }

        @Override
        public Void call() throws Exception {
            Thread.sleep(sleepMs);
            System.out.println("I'm awake now");
            return null;
        }
    }
}
