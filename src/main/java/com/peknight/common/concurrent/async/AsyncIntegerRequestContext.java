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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

/**
 *
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/11/3.
 */
public class AsyncIntegerRequestContext<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncIntegerRequestContext.class);

    /** 计数器 */
    private final AtomicInteger requestCount;
    /** 容器 */
    private final Map<Integer, AsyncIntegerRequest<T>> context = new ConcurrentHashMap<>();

    private static final IntUnaryOperator OPERATOR = operand -> operand == Integer.MAX_VALUE ? 0 : ++operand;

    public AsyncIntegerRequestContext(AtomicInteger requestCount) {
        this.requestCount = requestCount;
    }

    /** 创建Request */
    public AsyncIntegerRequest<T> createRequest() {
        int requestId = requestCount.updateAndGet(OPERATOR);
        AsyncIntegerRequest<T> request = new AsyncIntegerRequest<>(requestId);
        context.put(requestId, request);
        return request;
    }

    /** 释放Request */
    public void releaseRequest(int requestId, T value) {
        if (context.containsKey(requestId)) {
            context.get(requestId).setValue(value);
        } else {
            LOGGER.warn("Missing Request [{} : {}]", requestId, value);
        }
    }

    /** 删除Request */
    public void removeRequest(int requestId) {
        if (context.containsKey(requestId)) {
            context.remove(requestId);
        }
    }
}
