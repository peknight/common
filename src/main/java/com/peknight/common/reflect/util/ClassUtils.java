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

import com.peknight.common.reflect.scan.ClassNameFilter;
import com.peknight.common.reflect.scan.CommonClassNameFilter;
import com.peknight.common.reflect.scan.ImplementClassResolver;
import com.peknight.common.reflect.scan.ListClassResolver;
import com.peknight.common.reflect.scan.PackageResolver;
import com.peknight.common.reflect.scan.PackageScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.util.ClassUtils.isPrimitiveOrWrapper;

/**
 * 类 工具类
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/8/4.
 */
public final class ClassUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassUtils.class);

    private ClassUtils() {}

    public static Class<?> forName(String className) throws ClassNotFoundException {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Class Not Found: {}", className, e);
            throw e;
        }
    }

    /**
     * 获取数组类型的最底层元素类型
     */
    public static Class getFinalComponentClass(Class<?> clazz) {
        while (clazz.isArray()) {
           clazz = clazz.getComponentType();
        }
        return clazz;
    }

    /**
     * 判断某类型或某数组类型的最底层元素类型是否为基本数据类型、包装类型、枚举类型、字符串类型或集合类型
     */
    public static boolean isPlainValue(Class<?> clazz) {
        clazz = getFinalComponentClass(clazz);
        if (isPrimitiveOrWrapper(clazz) || clazz.isEnum() || String.class.equals(clazz)) {
            return true;
        }
        return false;
    }

    /**
     * 获取给定所有包路径下的所有子类(为了程序稳定，默认过滤掉了部分包中的内容)
     */
    public static Set<Class> listImplementClass(Class tClass, String... basePackages) throws IOException {
        int modifiers = tClass.getModifiers();
        if (Modifier.isFinal(modifiers)) {
            return new HashSet<>();
        }
        PackageResolver<Set<Class>> resolver = new ImplementClassResolver(tClass);
        ClassNameFilter classNameFilter = new CommonClassNameFilter();
        PackageScanner<Set<Class>> scanner = new PackageScanner<>(resolver, classNameFilter);
        scanner.resolveBasePackages(basePackages);
        Set<Class> implementClassSet = resolver.getTargetObject();
        return implementClassSet;
    }

    public static Set<Class> listClass(String... basePackages) throws IOException {
        PackageResolver<Set<Class>> resolver = new ListClassResolver();
        ClassNameFilter classNameFilter = new CommonClassNameFilter();
        PackageScanner<Set<Class>> scanner = new PackageScanner<>(resolver, classNameFilter);
        scanner.resolveBasePackages(basePackages);
        Set<Class> classSet = resolver.getTargetObject();
        return classSet;
    }
}
