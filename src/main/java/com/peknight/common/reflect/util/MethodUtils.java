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
package com.peknight.common.reflect.util;

import com.peknight.common.reflect.material.BeanCreationException;
import com.peknight.common.reflect.material.BeanMaterial;
import com.peknight.common.reflect.metadata.ConstructorMetadata;
import com.peknight.common.reflect.metadata.MetadataContext;
import com.peknight.common.reflect.metadata.MethodMetadata;
import com.peknight.common.string.StringUtils;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 方法工具类
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/8/9.
 */
public final class MethodUtils {
    private MethodUtils() {}

    private static final Set<MethodMetadata> OBJECT_METHODS = new HashSet<>();
    
    static {
        for (Method method : Object.class.getDeclaredMethods()) {
            OBJECT_METHODS.add(MetadataContext.getMethodMetadata(method));
        }
    }

    public static Class[] getParameterTypesByClassNames(List<String> paramList) throws ClassNotFoundException {
        int length = paramList == null ? 0 : paramList.size();
        Class[] parameterTypes = new Class[length];
        for (int i = 0; i < length; i++) {
            parameterTypes[i] = ClassUtils.forName(paramList.get(i));
        }
        return parameterTypes;
    }

    public static Class[] getParameterTypes(List<BeanMaterial> paramList) {
        int length = paramList == null ? 0 : paramList.size();
        Class[] parameterTypes = new Class[length];
        for (int i = 0; i < length; i++) {
            parameterTypes[i] = paramList.get(i).getDeclaredClass();
        }
        return parameterTypes;
    }

    public static Object[] getArgs(List<BeanMaterial> paramList) throws BeanCreationException {
        int length = paramList == null ? 0 : paramList.size();
        Object[] args = new Object[length];
        for (int i = 0; i < length; i++) {
            args[i] = paramList.get(i).getBean();
        }
        return args;
    }

    public static <T> Constructor<T> getConstructor(Class<T> tClass, Class[] parameterTypes) throws NoSuchMethodException {
        Constructor constructor;
        try {
            constructor = tClass.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            constructor = tClass.getDeclaredConstructor(parameterTypes);
        }
        return constructor;
    }

    public static <T> Set<ConstructorMetadata<T>> getConstructorSet(Class<T> clazz) {
        Set<ConstructorMetadata<T>> constructorSet = new HashSet<>();
        for (Constructor constructor : clazz.getConstructors()) {
            constructorSet.add(MetadataContext.getConstructorMetadata(constructor));
        }
        for (Constructor constructor : clazz.getDeclaredConstructors()) {
            constructorSet.add(MetadataContext.getConstructorMetadata(constructor));
        }
        if (constructorSet.size() == 0) {
            return null;
        } else {
            return constructorSet;
        }
    }

    public static Method getMethod(Class tClass, String methodName, Class[] parameterTypes) throws NoSuchMethodException {
        Method method;
        try {
            method = tClass.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            while (true) {
                try {
                    method = tClass.getDeclaredMethod(methodName, parameterTypes);
                    break;
                } catch (NoSuchMethodException ee) {
                    if (Object.class.equals(tClass)) {
                        throw ee;
                    } else {
                        tClass = tClass.getSuperclass();
                    }
                }

            }
        }
        return method;
    }

    public static Set<MethodMetadata> getMethodSet(Class<?> clazz) {
        Set<MethodMetadata> methodSet = new HashSet<>();
        for (Method method : clazz.getMethods()) {
            methodSet.add(MetadataContext.getMethodMetadata(method));
        }
        while (true) {
            for (Method method : clazz.getDeclaredMethods()) {
                methodSet.add(MetadataContext.getMethodMetadata(method));
            }
            clazz = clazz.getSuperclass();
            if (Object.class.equals(clazz)) {
                break;
            }
        }
        methodSet.removeAll(OBJECT_METHODS);

        return methodSet;
    }

    public static String getReturnTypeSimpleName(Method method) {
        // 生成返回值类型
        Type genericReturnType = method.getGenericReturnType();
        String simpleName = method.getReturnType().getSimpleName();
        if (genericReturnType instanceof ParameterizedType) { // 返回值如果含有泛型
            StringBuilder returnType = new StringBuilder(simpleName);
            returnType.append("<");
            String actualTypes = StringUtils.toString(((ParameterizedType) genericReturnType).getActualTypeArguments());
            return simpleName + "<" + StringUtils.substring(actualTypes, 1, -1) + ">";
        } else {
            return simpleName;
        }
    }

    public static String argsToString(MethodSignature methodSignature, Object[] args) {
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
        return paramStringBuilder.toString();
    }
}
