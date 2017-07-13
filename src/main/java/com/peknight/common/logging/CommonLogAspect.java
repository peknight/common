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

import com.peknight.common.annotation.Param;
import com.peknight.common.string.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Common Log Aspect
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/6/21.
 */
@Aspect
public class CommonLogAspect {
    private static final Map<Method, long[]> EXECUTE_TIME = new ConcurrentHashMap<>();

    @Around("@within(com.peknight.common.logging.CommonLog) || @annotation(com.peknight.common.logging.CommonLog)")
    public Object commonLog(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Method method = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();
        CommonLog commonLog;
        if (method.isAnnotationPresent(CommonLog.class)) {
            commonLog = method.getDeclaredAnnotation(CommonLog.class);
        } else {
            commonLog = method.getDeclaringClass().getDeclaredAnnotation(CommonLog.class);
        }
        String margin = commonLog.value();
        String beginMargin = StringUtils.isEmpty(commonLog.beginMargin()) ? margin : commonLog.beginMargin();
        String endMargin = StringUtils.isEmpty(commonLog.endMargin()) ? margin : commonLog.endMargin();
        String exceptionMargin = StringUtils.isEmpty(commonLog.exceptionMargin()) ? margin : commonLog.exceptionMargin();
        CommonLog.LoggingLevel level = commonLog.level();
        return commonLog(proceedingJoinPoint, beginMargin, endMargin, exceptionMargin, level);
    }

    public static Object commonLog(ProceedingJoinPoint proceedingJoinPoint, String beginMargin, String endMargin, String exceptionMargin, CommonLog.LoggingLevel level) throws Throwable {
        Method method = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();
        Object[] args = proceedingJoinPoint.getArgs();
        Annotation[][] annotations = method.getParameterAnnotations();
        Logger logger = LoggerFactory.getLogger(method.getDeclaringClass());
        if (!EXECUTE_TIME.containsKey(method)) {
            EXECUTE_TIME.put(method, new long[2]);
        }
        long[] executeTime = EXECUTE_TIME.get(method);
        int index = (int) ++executeTime[1];
        StringBuilder paramStringBuilder = new StringBuilder("");
        for (int i = 0; i < args.length; i++) {
            if (args[i] != null) {
                if (i > 0) {
                    paramStringBuilder.append(", ");
                }
                paramStringBuilder.append("(").append(args[i].getClass().getSimpleName()).append(" ");
                String paramName = null;
                for (Annotation annotation : annotations[i]) {
                    if (annotation.annotationType() == Param.class) {
                        paramName = ((Param) annotation).value();
                    }
                }
                if (paramName == null) {
                    paramStringBuilder.append("arg").append(i);
                } else {
                    paramStringBuilder.append(paramName);
                }
                paramStringBuilder.append(") ").append(StringUtils.toString(args[i]));
            }
        }

        String methodInfo = String.format("%s%s%d%s", method.getName(), "[", index, "]");
        preLogger(logger, level, beginMargin, methodInfo, paramStringBuilder);

        long start = System.currentTimeMillis();
        long taskTime;

        try {
            Object object = proceedingJoinPoint.proceed();
            taskTime = System.currentTimeMillis() - start;
            executeTime[0] += taskTime;
            postLogger(logger, level, endMargin, methodInfo, taskTime, executeTime[0] / executeTime[1], method.getReturnType().getSimpleName(), object);
            return object;
        } catch (Throwable e) {
            taskTime = System.currentTimeMillis() - start;
            executeTime[0] += taskTime;
            postErrorLogger(logger, exceptionMargin, methodInfo, taskTime, executeTime[0] / executeTime[1], method.getReturnType().getSimpleName(), e);
            throw e;
        }
    }

    private static void preLogger(Logger logger, CommonLog.LoggingLevel level, String beginMargin, String methodInfo,
                                  StringBuilder paramStringBuilder) {
        String loggerFormat = paramStringBuilder.length() == 0 ? "{}[Begin] {}" : "{}[Begin] {} Args: [{}]";
        switch (level) {
            case TRACE:
                logger.trace(loggerFormat, beginMargin, methodInfo, paramStringBuilder);
                return;
            case DEBUG:
                logger.debug(loggerFormat, beginMargin, methodInfo, paramStringBuilder);
                return;
            case INFO:
                logger.info(loggerFormat, beginMargin, methodInfo, paramStringBuilder);
                return;
            case WARN:
                logger.warn(loggerFormat, beginMargin, methodInfo, paramStringBuilder);
                return;
            case ERROR:
                logger.error(loggerFormat, beginMargin, methodInfo, paramStringBuilder);
                return;
            default:
                return;
        }
    }

    private static void postLogger(Logger logger, CommonLog.LoggingLevel level, String endMargin, String methodInfo,
                                   long time, long avgTime, String returnType, Object returnObj) {
        String loggerFormat = "void".equals(returnType) ? "{}[  End] {} [Time: {}ms, AvgTime: {}ms]" : "{}[  End] {} [Time: {}ms, AvgTime: {}ms] Return: ({}) {}";
        switch (level) {
            case TRACE:
                logger.trace(loggerFormat, endMargin, methodInfo, time, avgTime, returnType, returnObj);
                return;
            case DEBUG:
                logger.debug(loggerFormat, endMargin, methodInfo, time, avgTime, returnType, returnObj);
                return;
            case INFO:
                logger.info(loggerFormat, endMargin, methodInfo, time, avgTime, returnType, returnObj);
                return;
            case WARN:
                logger.warn(loggerFormat, endMargin, methodInfo, time, avgTime, returnType, returnObj);
                return;
            case ERROR:
                logger.error(loggerFormat, endMargin, methodInfo, time, avgTime, returnType, returnObj);
                return;
            default:
                return;
        }
    }

    private static void postErrorLogger(Logger logger, String exceptionMargin, String methodInfo,
                                            long time, long avgTime, String returnType, Throwable e) {
        String loggerFormat = "void".equals(returnType) ? "{}[Error] {} [Time: {}ms, AvgTime: {}ms] ExceptionMessage: {}" : "{}[Error] {} [Time: {}ms, AvgTime: {}ms] [ReturnType: {}] Error: {}";
        logger.error(loggerFormat, exceptionMargin, methodInfo, time, avgTime, returnType, e.toString(), e);
    }
}
