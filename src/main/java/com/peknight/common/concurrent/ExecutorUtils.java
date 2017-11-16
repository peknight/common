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

import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池工具类
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/10/12.
 */
public final class ExecutorUtils {

    private static final ThreadFactory DEFAULT_THREAD_FACTORY = new CustomizableThreadFactory();

    private static final RejectedExecutionHandler DEFAULT_REJECT_HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private ExecutorUtils() {}

    public static BlockingQueue<Runnable> createQueue(int queueCapacity) {
        if (queueCapacity > 0) {
            return new LinkedBlockingQueue<>(queueCapacity);
        }
        else {
            return new SynchronousQueue<>();
        }
    }

    public static ExecutorService createExecutorService(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                                        int queueCapacity) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
                createQueue(queueCapacity), new CustomizableThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public static ExecutorService createExecutorService(String poolName, int corePoolSize, int maximumPoolSize,
                                                        long keepAliveTime, int queueCapacity) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
                createQueue(queueCapacity), new CustomizableThreadFactory(poolName), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public static TaskExecutor createTaskExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                                  int queueCapacity) {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(corePoolSize);
        taskExecutor.setMaxPoolSize(maximumPoolSize);
        taskExecutor.setKeepAliveSeconds((int) (keepAliveTime / 1000));
        taskExecutor.setQueueCapacity(queueCapacity);
        taskExecutor.setThreadFactory(DEFAULT_THREAD_FACTORY);
        taskExecutor.setRejectedExecutionHandler(DEFAULT_REJECT_HANDLER);
        return taskExecutor;
    }

    public static TaskExecutor createTaskExecutor(String poolName, int corePoolSize, int maximumPoolSize,
                                                  long keepAliveTime, int queueCapacity) {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(corePoolSize);
        taskExecutor.setMaxPoolSize(maximumPoolSize);
        taskExecutor.setKeepAliveSeconds((int) (keepAliveTime / 1000));
        taskExecutor.setQueueCapacity(queueCapacity);
        taskExecutor.setThreadFactory(new CustomizableThreadFactory(poolName));
        taskExecutor.setRejectedExecutionHandler(DEFAULT_REJECT_HANDLER);
        return taskExecutor;
    }

    public static TaskScheduler createTaskScheduler(int poolSize) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(poolSize);
        taskScheduler.setThreadFactory(DEFAULT_THREAD_FACTORY);
        taskScheduler.setRejectedExecutionHandler(DEFAULT_REJECT_HANDLER);
        return taskScheduler;
    }

    public static TaskScheduler createTaskScheduler(String poolName, int poolSize) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(poolSize);
        taskScheduler.setThreadFactory(new CustomizableThreadFactory(poolName));
        taskScheduler.setRejectedExecutionHandler(DEFAULT_REJECT_HANDLER);
        return taskScheduler;
    }
}
