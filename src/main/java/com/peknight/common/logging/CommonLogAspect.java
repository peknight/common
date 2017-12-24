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

import com.peknight.common.reflect.util.MethodUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLongArray;

/**
 * 日志切面，通过拦截@CommonLog注解，打印被拦截方法的信息
 * 分别在方法执行前、执行后打印一条日志
 * 方法执行前打印方法名、执行次数、各参数（参数类型、参数名、参数值）
 * 方法执行后，如果没有抛出异常打印方法名、执行次数、执行时间、平均执行时间、返回类型、返回值
 * 方法执行后，如果抛出异常打印方法名、执行次数、执行时间、平均执行时间、返回类型、异常信息
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/6/21.
 */
@Order(0)
@Aspect
public class CommonLogAspect {

    /**
     * 为每个方法执行计数计时的Map，使用ConcurrentHashMap保证线程安全
     * Key为Method对应计数计时的方法，AtomicLongArray为一个长度为2的long型原子数组（保证线程安全）
     * 数据第一个元素表示此方法执行的总时间，第二个元素表示此方法执行的总次数，相除即为平均执行时间
     * 第三个元素用于记录当前是第几次执行（不同于第二个元素，第二个元素用于计算平均时间直接使用会有计算不准确的问题）
     */
    private static final Map<Method, AtomicLongArray> EXECUTE_TIME = new ConcurrentHashMap<>();

    /**
     * 拦截类上或方法注解@CommonLog的所有方法
     */
    @Around("@within(org.joinquant.common.logging.CommonLog) || @annotation(org.joinquant.common.logging.CommonLog)")
    public Object commonLog(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Method method = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();
        Class<?> declaringClass = method.getDeclaringClass();
        // 获取Method的类的Logger
        Logger logger = LoggerFactory.getLogger(declaringClass);
        // 获取方法上的CommonLog注解，如果没有则获取类上的CommonLog注解
        CommonLog commonLog = null;
        if (method.isAnnotationPresent(CommonLog.class)) {
            commonLog = method.getDeclaredAnnotation(CommonLog.class);
        } else if (declaringClass.isAnnotationPresent(CommonLog.class)) {
            Class<?> targetClass = declaringClass;
            while ((commonLog = targetClass.getDeclaredAnnotation(CommonLog.class)) == null) {
                targetClass = targetClass.getSuperclass();
            }
        }
        // 获取被拦截方法的日志等级
        Level level = commonLog.value();
        return commonLog(proceedingJoinPoint, logger, level);
    }

    /**
     * 打印日志
     */
    public static Object commonLog(ProceedingJoinPoint proceedingJoinPoint, Logger logger, Level level) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = proceedingJoinPoint.getArgs();
        EXECUTE_TIME.putIfAbsent(method, new AtomicLongArray(3));
        AtomicLongArray executeTime = EXECUTE_TIME.get(method);
        long index = executeTime.incrementAndGet(2);
        String methodInfo = String.format("%s%s%d%s", method.getName(), "[", index, "]");
        if (isLoggingLevelEnable(logger, level)) {
            preLogger(logger, level, methodInfo, MethodUtils.argsToString(methodSignature, args));
        }
        String returnType = MethodUtils.getReturnTypeSimpleName(method);
        long taskTime;
        long start = System.nanoTime();
        try {
            Object object = proceedingJoinPoint.proceed();
            taskTime = System.nanoTime() - start;
            if (object != null && object instanceof CommonResult && ((CommonResult) object).getCode() != 0
                    && level.toInt() < Level.WARN.toInt()) {
                if (!isLoggingLevelEnable(logger, level) && isLoggingLevelEnable(logger, Level.WARN)) {
                    preLogger(logger, Level.WARN, methodInfo, MethodUtils.argsToString(methodSignature, args));
                }
                level = Level.WARN;
            }
            postLogger(logger, level, methodInfo, timeFormat(taskTime),
                    timeFormat(executeTime.addAndGet(0, taskTime) / executeTime.incrementAndGet(1)),
                    returnType, object);
            return object;
        } catch (Throwable e) {
            taskTime = System.nanoTime() - start;
            if (!isLoggingLevelEnable(logger, level) && isLoggingLevelEnable(logger, Level.ERROR)) {
                preLogger(logger, Level.ERROR, methodInfo, MethodUtils.argsToString(methodSignature, args));
            }
            postErrorLogger(logger, methodInfo, timeFormat(taskTime),
                    timeFormat(executeTime.addAndGet(0, taskTime) / executeTime.incrementAndGet(1)),
                    returnType, e);
            throw e;
        }
    }

    /**
     * 打印方法执行前相应级别的日志
     */
    private static void preLogger(Logger logger, Level level, String methodInfo, String argsInfo) {
        String loggerFormat = argsInfo.length() == 0 ? "[Begin] {}" : "[Begin] {} Args: [{}]";
        switch (level) {
            case TRACE:
                logger.trace(loggerFormat, methodInfo, argsInfo);
                return;
            case DEBUG:
                logger.debug(loggerFormat, methodInfo, argsInfo);
                return;
            case INFO:
                logger.info(loggerFormat, methodInfo, argsInfo);
                return;
            case WARN:
                logger.warn(loggerFormat, methodInfo, argsInfo);
                return;
            case ERROR:
                logger.error(loggerFormat, methodInfo, argsInfo);
                return;
            default:
                return;
        }
    }

    /**
     * 打印方法执行后相应级别的日志
     */
    private static void postLogger(Logger logger, Level level, String methodInfo,
                                   String time, String avgTime, String returnType, Object returnObj) {
        String loggerFormat = "void".equals(returnType) ?
                "[  End] {} [Time: {}, AvgTime: {}]" :
                "[  End] {} [Time: {}, AvgTime: {}] Return: ({}) {}";
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

    /**
     * 打印异常日志
     */
    private static void postErrorLogger(Logger logger, String methodInfo,
                                        String time, String avgTime, String returnType, Throwable e) {
        String loggerFormat = "void".equals(returnType) ?
                "[Error] {} [Time: {}, AvgTime: {}] ExceptionMessage: {}" :
                "[Error] {} [Time: {}, AvgTime: {}] [ReturnType: {}] Error: {}";
        logger.error(loggerFormat, methodInfo, time, avgTime, returnType, e.toString(), e);
    }

    /**
     * 判断日志等级是否允许输出
     */
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

    private static String timeFormat(long nanoTime) {
        if (nanoTime >= 10L * 1000 * 1000 * 1000) {
            return nanoTime / (1000 * 1000 * 1000) + "s";
        } else if (nanoTime >= 10 * 1000 * 1000) {
            return nanoTime / (1000 * 1000) + "ms";
        } else if (nanoTime >= 10 * 1000) {
            return nanoTime / 1000 + "us";
        } else {
            return nanoTime + "ns";
        }
    }
}

