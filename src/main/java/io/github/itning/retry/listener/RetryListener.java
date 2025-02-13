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

package io.github.itning.retry.listener;

import io.github.itning.retry.Attempt;
import io.github.itning.retry.Retryer;

/**
 * This listener provides callbacks for several events that occur when running
 * code through a {@link Retryer} instance.
 *
 * @param <V> the type returned by the retryer callable
 */
@FunctionalInterface
public interface RetryListener<V> {

    /**
     * The listener method will be triggered if and only when retrying
     *
     * @param attempt the current {@link Attempt}
     */
    void onRetry(Attempt<V> attempt);
}
