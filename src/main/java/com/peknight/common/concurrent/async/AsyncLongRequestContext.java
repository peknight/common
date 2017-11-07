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
package com.peknight.common.concurrent.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongUnaryOperator;

/**
 *
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/11/3.
 */
public class AsyncLongRequestContext<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncLongRequestContext.class);

    /** 计数器 */
    private final AtomicLong requestCount;
    /** 容器 */
    private final Map<Long, AsyncLongRequest<T>> context = new ConcurrentHashMap<>();

    private static final LongUnaryOperator OPERATOR = operand -> operand == Long.MAX_VALUE ? 0 : ++operand;

    public AsyncLongRequestContext(AtomicLong requestCount) {
        this.requestCount = requestCount;
    }

    /** 创建Request */
    public AsyncLongRequest<T> createRequest() {
        long requestId = requestCount.updateAndGet(OPERATOR);
        AsyncLongRequest<T> request = new AsyncLongRequest<>(requestId);
        context.put(requestId, request);
        return request;
    }

    /** 释放Request */
    public void releaseRequest(long requestId, T value) {
        if (context.containsKey(requestId)) {
            context.get(requestId).setValue(value);
        } else {
            LOGGER.warn("Missing Request [{} : {}]", requestId, value);
        }
    }

    /** 删除Request */
    public void removeRequest(long requestId) {
        context.remove(requestId);
    }
}
