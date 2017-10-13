/**
 * MIT License
 * <p>
 * Copyright (c) 2017-2027 PeKnight(JKpeknight@gmail.com)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.peknight.common.concurrent;

import com.peknight.common.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 二进制指数退避算法
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/10/12.
 */
public class BinaryExponentialBackoff {
    private static final Logger LOGGER = LoggerFactory.getLogger(BinaryExponentialBackoff.class);

    private final Random random = new Random(System.currentTimeMillis());

    private final long requestTimeoutInMillis;

    private final long beBackoffSlotInMillis;

    private final int maximumRetryCount;

    public BinaryExponentialBackoff() {
        this.requestTimeoutInMillis = TimeUnit.SECONDS.toMillis(20);
        this.beBackoffSlotInMillis = 100;
        this.maximumRetryCount = 20;
    }

    public BinaryExponentialBackoff(long requestTimeoutInMillis, long beBackoffSlotInMillis, int maximumRetryCount) {
        this.requestTimeoutInMillis = requestTimeoutInMillis;
        this.beBackoffSlotInMillis = beBackoffSlotInMillis;
        this.maximumRetryCount = maximumRetryCount;
    }

    public <T, R> R backoff(Function<T, R> function, T param, Class<? extends Exception> eClass) {
        RetryParam retryParam = new RetryParam(requestTimeoutInMillis);
        while (true) {
            try {
                return function.apply(param);
            } catch (Throwable t) {
                if (t.getClass().equals(eClass)) {
                    if (sleep(retryParam)) {
                        continue;
                    } else {
                        LOGGER.warn("Error: {}", t.toString(), t);
                        return null;
                    }
                } else {
                    LOGGER.warn("Unexpected Error {}", t.toString(), t);
                    return null;
                }
            }
        }
    }

    private boolean sleep(RetryParam retryParam) {
        retryParam.retryCountIncrement();
        try {
            if (retryParam.getRemainTimeInMillis() > 0) {
                long sleepTimeInMillis = ((long) (random.nextDouble() *
                        (1L << Math.min(retryParam.getRetryCount(), maximumRetryCount)))) * beBackoffSlotInMillis;
                sleepTimeInMillis = Math.min(sleepTimeInMillis, retryParam.getRemainTimeInMillis());
                TimeUnit.MILLISECONDS.sleep(sleepTimeInMillis);
                retryParam.remainTimeDecrement(sleepTimeInMillis);
                return true;
            } else {
                LOGGER.warn("Task has been rejected " + retryParam.getRetryCount() + " times till timeout");
                return false;
            }
        } catch (InterruptedException e) {
            LOGGER.warn("Interrupted while waiting to place client on executor queue.");
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private class RetryParam {

        private int retryCount = 0;
        private long remainTimeInMillis = 0;

        public RetryParam(long remainTimeInMillis) {
            this.remainTimeInMillis = remainTimeInMillis;
        }

        public int getRetryCount() {
            return retryCount;
        }

        public long getRemainTimeInMillis() {
            return remainTimeInMillis;
        }

        public void retryCountIncrement() {
            retryCount++;
        }

        public void remainTimeDecrement(long sleepTimeInMillis) {
            remainTimeInMillis -= sleepTimeInMillis;
        }
    }
}
