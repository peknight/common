/**
 * MIT License
 *
 * Copyright (c) 2017-2027 PeKnight(JKpeknight@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.peknight.common.logging;

import com.peknight.common.string.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.function.Function;

/**
 * Common Log Aspect
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/6/21.
 */
@Order(0)
@Aspect
public class CommonLogAspect {
    private static final Map<Method, AtomicLongArray> EXECUTE_TIME = new ConcurrentHashMap<>();

    private static final Function<Method, AtomicLongArray> COMPUTE_FUNCTION= method -> new AtomicLongArray(2);

    @Around("@within(com.peknight.common.logging.CommonLog) || @annotation(com.peknight.common.logging.CommonLog)")
    public Object commonLog(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Method method = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();
        CommonLog commonLog;
        if (method.isAnnotationPresent(CommonLog.class)) {
            commonLog = method.getDeclaredAnnotation(CommonLog.class);
        } else {
            commonLog = method.getDeclaringClass().getDeclaredAnnotation(CommonLog.class);
        }
        Logger logger = LoggerFactory.getLogger(method.getDeclaringClass());
        Level level = commonLog.value();
        if (isLoggingLevelEnable(logger, level)) {
            return commonLog(proceedingJoinPoint, logger, level);
        } else {
            return proceedingJoinPoint.proceed();
        }
    }

    public static Object commonLog(ProceedingJoinPoint proceedingJoinPoint, Logger logger, Level level) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = proceedingJoinPoint.getArgs();
        EXECUTE_TIME.computeIfAbsent(method, COMPUTE_FUNCTION);
        AtomicLongArray executeTime = EXECUTE_TIME.get(method);
        long index = executeTime.incrementAndGet(1);
        String[] parameterNames = methodSignature.getParameterNames();
        if (parameterNames == null) {
            parameterNames = new String[args.length];
            for (int i = 0; i < parameterNames.length; i++) {
                parameterNames[i] = "arg" + i;
            }
        }
        StringBuilder paramStringBuilder = new StringBuilder("");
        for (int i = 0; i < args.length; i++) {
            if (args[i] != null) {
                if (i > 0) {
                    paramStringBuilder.append(", ");
                }
                paramStringBuilder.append("(").append(args[i].getClass().getSimpleName()).append(" ")
                        .append(parameterNames[i]).append(") ").append(StringUtils.toString(args[i]));
            }
        }

        String methodInfo = String.format("%s%s%d%s", method.getName(), "[", index, "]");
        preLogger(logger, level, methodInfo, paramStringBuilder);

        long start = System.currentTimeMillis();
        long taskTime;

        try {
            Object object = proceedingJoinPoint.proceed();
            taskTime = System.currentTimeMillis() - start;
            executeTime.addAndGet(0, taskTime);
            postLogger(logger, level, methodInfo, taskTime, executeTime.get(0) / executeTime.get(1), method.getReturnType().getSimpleName(), object);
            return object;
        } catch (Throwable e) {
            taskTime = System.currentTimeMillis() - start;
            executeTime.addAndGet(0, taskTime);
            postErrorLogger(logger, methodInfo, taskTime, executeTime.get(0) / executeTime.get(1), method.getReturnType().getSimpleName(), e);
            throw e;
        }
    }

    private static void preLogger(Logger logger, Level level, String methodInfo,
                                  StringBuilder paramStringBuilder) {
        String loggerFormat = paramStringBuilder.length() == 0 ? "[Begin] {}" : "[Begin] {} Args: [{}]";
        switch (level) {
            case TRACE:
                logger.trace(loggerFormat, methodInfo, paramStringBuilder);
                return;
            case DEBUG:
                logger.debug(loggerFormat, methodInfo, paramStringBuilder);
                return;
            case INFO:
                logger.info(loggerFormat, methodInfo, paramStringBuilder);
                return;
            case WARN:
                logger.warn(loggerFormat, methodInfo, paramStringBuilder);
                return;
            case ERROR:
                logger.error(loggerFormat, methodInfo, paramStringBuilder);
                return;
            default:
                return;
        }
    }

    private static void postLogger(Logger logger, Level level, String methodInfo,
                                   long time, long avgTime, String returnType, Object returnObj) {
        String loggerFormat = "void".equals(returnType) ? "[  End] {} [Time: {}ms, AvgTime: {}ms]" : "[  End] {} [Time: {}ms, AvgTime: {}ms] Return: ({}) {}";
        switch (level) {
            case TRACE:
                logger.trace(loggerFormat, methodInfo, time, avgTime, returnType, returnObj);
                return;
            case DEBUG:
                logger.debug(loggerFormat, methodInfo, time, avgTime, returnType, returnObj);
                return;
            case INFO:
                logger.info(loggerFormat, methodInfo, time, avgTime, returnType, returnObj);
                return;
            case WARN:
                logger.warn(loggerFormat, methodInfo, time, avgTime, returnType, returnObj);
                return;
            case ERROR:
                logger.error(loggerFormat, methodInfo, time, avgTime, returnType, returnObj);
                return;
            default:
                return;
        }
    }

    private static void postErrorLogger(Logger logger, String methodInfo,
                                            long time, long avgTime, String returnType, Throwable e) {
        String loggerFormat = "void".equals(returnType) ? "[Error] {} [Time: {}ms, AvgTime: {}ms] ExceptionMessage: {}" : "[Error] {} [Time: {}ms, AvgTime: {}ms] [ReturnType: {}] Error: {}";
        logger.error(loggerFormat, methodInfo, time, avgTime, returnType, e.toString(), e);
    }

    public static boolean isLoggingLevelEnable(Logger logger, Level level) {
        switch (level) {
            case TRACE:
                return logger.isTraceEnabled();
            case DEBUG:
                return logger.isDebugEnabled();
            case INFO:
                return logger.isInfoEnabled();
            case WARN:
                return logger.isWarnEnabled();
            case ERROR:
                return logger.isErrorEnabled();
            default:
                return false;
        }
    }
}
