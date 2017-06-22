package com.peknight.common.logging;

import com.peknight.common.string.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通用日志切面
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/6/21.
 */
@Aspect
public class CommonLogAspect {
    private static final Map<Method, long[]> AVG_TIME = new ConcurrentHashMap<>();

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
        Logger logger = LoggerFactory.getLogger(method.getDeclaringClass());
        if (!AVG_TIME.containsKey(method)) {
            AVG_TIME.put(method, new long[2]);
        }
        long index = AVG_TIME.get(method)[1] + 1;
        AVG_TIME.get(method)[1] = index;
        StringBuilder paramStringBuilder = new StringBuilder("");
        for (int i = 0; i < args.length; i++) {
            if (args[i] != null) {
                if (i > 0) {
                    paramStringBuilder.append(", ");
                }
                paramStringBuilder.append("Param").append(i + 1)
                        .append(" (").append(args[i].getClass().getSimpleName()).append("): ").append(args[i]);
            }
        }

        preLogger(logger, level, beginMargin, method.getName(), index, paramStringBuilder);

        long start = System.currentTimeMillis();
        long end, time, avgTime;

        try {
            Object object = proceedingJoinPoint.proceed();
            end = System.currentTimeMillis();
            time = end -start;
            AVG_TIME.get(method)[0] = AVG_TIME.get(method)[0] + time;
            avgTime = AVG_TIME.get(method)[0]/AVG_TIME.get(method)[1];
            postLogger(logger, level, endMargin, method.getName(), index, time, avgTime, method.getReturnType().getSimpleName(), object);
            return object;
        } catch (Throwable e) {
            end = System.currentTimeMillis();
            time = end - start;
            AVG_TIME.get(method)[0] = AVG_TIME.get(method)[0] + time;
            avgTime = AVG_TIME.get(method)[0]/AVG_TIME.get(method)[1];
            postExceptionLogger(logger, exceptionMargin, method.getName(), index, time, avgTime, method.getReturnType().getSimpleName(), e.toString());
            throw e;
        }
    }

    private static void preLogger(Logger logger, CommonLog.LoggingLevel level, String beginMargin, String methodName,
                                  long index, StringBuilder paramStringBuilder) {
        String loggerFormat = "{}{}[{}] Begin\tParamList: [{}]";
        switch (level) {
            case TRACE:
                logger.trace(loggerFormat, beginMargin, methodName, index, paramStringBuilder);
                return;
            case DEBUG:
                logger.debug(loggerFormat, beginMargin, methodName, index, paramStringBuilder);
                return;
            case INFO:
                logger.info(loggerFormat, beginMargin, methodName, index, paramStringBuilder);
                return;
            case WARN:
                logger.warn(loggerFormat, beginMargin, methodName, index, paramStringBuilder);
                return;
            case ERROR:
                logger.error(loggerFormat, beginMargin, methodName, index, paramStringBuilder);
                return;
            default:
                return;
        }
    }

    private static void postLogger(Logger logger, CommonLog.LoggingLevel level, String endMargin, String methodName,
                                   long index, long time, long avgTime, String returnType, Object returnObj) {
        String loggerFormat = "{}{}[{}] End\t[Time: {}ms, AvgTime: {}ms]\tReturn ({}): {}";
        switch (level) {
            case TRACE:
                logger.trace(loggerFormat, endMargin, methodName, index, time, avgTime, returnType, returnObj);
                return;
            case DEBUG:
                logger.debug(loggerFormat, endMargin, methodName, index, time, avgTime, returnType, returnObj);
                return;
            case INFO:
                logger.info(loggerFormat, endMargin, methodName, index, time, avgTime, returnType, returnObj);
                return;
            case WARN:
                logger.warn(loggerFormat, endMargin, methodName, index, time, avgTime, returnType, returnObj);
                return;
            case ERROR:
                logger.error(loggerFormat, endMargin, methodName, index, time, avgTime, returnType, returnObj);
                return;
            default:
                return;
        }
    }

    private static void postExceptionLogger(Logger logger, String exceptionMargin, String methodName, long index,
                                            long time, long avgTime, String returnType, String exception) {
        String loggerFormat = "{}{}[{}] Exception\t[Time: {}ms, AvgTime: {}ms]\tReturnType: {}\tExceptionMessage: {}";
        logger.error(loggerFormat, exceptionMargin, methodName, index, time, avgTime, returnType, exception);
    }
}
