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
package com.peknight.common.reflect;

import com.peknight.common.CommonApplication;
import com.peknight.common.logging.LogUtils;
import com.peknight.common.reflect.scanner.PackageResolver;
import com.peknight.common.reflect.scanner.PackageScanner;
import com.peknight.common.springframework.context.ApplicationContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 *
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/8/4.
 */
public final class ReflectUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectUtils.class);

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
        if (ClassUtils.isPrimitiveOrWrapper(clazz) || clazz.isEnum() || String.class.equals(clazz)
                || Collection.class.isAssignableFrom(clazz)) {
            return true;
        }
        return false;
    }

    public static List<Class> listImplementClass(Class tClass, String... basePackages) throws IOException {
        ImplementClassResolver resolver = new ImplementClassResolver(tClass);
        PackageScanner<List<Class>> scanner = new PackageScanner<>(resolver);
        scanner.resolveBasePackages(basePackages);
        return resolver.getTargetObject();
    }

    public static void main(String[] args) throws IOException {
        ApplicationContextHolder.run(CommonApplication.class, args, Banner.Mode.LOG);
//        LogUtils.info(listImplementClass(List.class, ".*"));

        LogUtils.info(listImplementClass(List.class, "*"));
    }
}
