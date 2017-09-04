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
     * 数据第一个元素表示此方法执行的总时间，第二个元素表示此方法执行的总次数，相处即为平均执行时间
     */
    private static final Map<Method, AtomicLongArray> EXECUTE_TIME = new ConcurrentHashMap<>();

    /**
     * 如果EXECUTE_TIME中不含有某个Method Key，那么以此Method为Key，新创建的长度为2的long型原子数组为Value放入EXECUTE_TIME中
     */
    private static final Function<Method, AtomicLongArray> COMPUTE_FUNCTION= method -> new AtomicLongArray(2);

    /**
     * 拦截类上或方法注解@CommonLog的所有方法
     */
    @Around("@within(com.peknight.common.logging.CommonLog) || @annotation(com.peknight.common.logging.CommonLog)")
    public Object commonLog(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Method method = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();
        // 获取方法上的CommonLog注解，如果没有则获取类上的CommonLog注解
        CommonLog commonLog;
        if (method.isAnnotationPresent(CommonLog.class)) {
            commonLog = method.getDeclaredAnnotation(CommonLog.class);
        } else {
            commonLog = method.getDeclaringClass().getDeclaredAnnotation(CommonLog.class);
        }

        // 获取Method的类的Logger
        Logger logger = LoggerFactory.getLogger(method.getDeclaringClass());

        // 获取被拦截方法的日志等级
        Level level = commonLog.value();

        // 如果程序的日志输出等级高于方法的等级，那么不打印日志直接执行方法返回
        if (isLoggingLevelEnable(logger, level)) {
            return commonLog(proceedingJoinPoint, logger, level);
        } else {
            return proceedingJoinPoint.proceed();
        }
    }

    /**
     * 打印日志
     */
    public static Object commonLog(ProceedingJoinPoint proceedingJoinPoint, Logger logger, Level level) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = proceedingJoinPoint.getArgs();
        // 获取方法参数类型（注意空指针）
        Class[] parameterTypes = methodSignature.getParameterTypes();
        if (parameterTypes == null) {
            parameterTypes = new Class[args.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                parameterTypes[i] = args[i].getClass();
            }
        }
        // 获取方法参数名（注意空指针）
        String[] parameterNames = methodSignature.getParameterNames();
        if (parameterNames == null) {
            parameterNames = new String[args.length];
            for (int i = 0; i < parameterNames.length; i++) {
                parameterNames[i] = "arg" + i;
            }
        }
        EXECUTE_TIME.computeIfAbsent(method, COMPUTE_FUNCTION);
        AtomicLongArray executeTime = EXECUTE_TIME.get(method);
        long index = executeTime.incrementAndGet(1);
        StringBuilder paramStringBuilder = new StringBuilder("");
        for (int i = 0; i < args.length; i++) {
            if (args[i] != null) {
                if (i > 0) {
                    paramStringBuilder.append(", ");
                }
                paramStringBuilder.append("(").append(parameterTypes[i].getSimpleName()).append(" ")
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

    /**
     * 打印方法执行前相应级别的日志
     */
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

    /**
     * 打印方法执行后相应级别的日志
     */
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

    /**
     * 打印异常日志
     */
    private static void postErrorLogger(Logger logger, String methodInfo,
                                            long time, long avgTime, String returnType, Throwable e) {
        String loggerFormat = "void".equals(returnType) ? "[Error] {} [Time: {}ms, AvgTime: {}ms] ExceptionMessage: {}" : "[Error] {} [Time: {}ms, AvgTime: {}ms] [ReturnType: {}] Error: {}";
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
}
