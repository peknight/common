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
package com.peknight.common.string;

import java.util.Arrays;

/**
 * String工具类
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/6/12.
 */
public final class StringUtils {
    public static final char UNDERLINE = '_';

    private StringUtils() {}

    /**
     * 将驼峰命名法的字符串转换为下划线连接的字符串
     */
    public static String toUnderscore(String origin) {
        if (isEmpty(origin)) {
            return origin;
        }

        StringBuilder builder = new StringBuilder();
        int length = origin.length();

        builder.append(Character.toLowerCase(origin.charAt(0)));
        for (int i = 1; i < length; i++) {
            char c = origin.charAt(i);
            if (Character.isUpperCase(c)) {
                builder.append(UNDERLINE).append(Character.toLowerCase(c));
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    /**
     * 将字符串转换为帕斯卡命名法（大驼峰命名法）
     */
    public static String underscoreToPascalCase(String origin) {
        if (isEmpty(origin)) {
            return origin;
        }

        StringBuilder builder = new StringBuilder();
        boolean toUpperCase = true;
        int length = origin.length();

        for (int i = 0; i < length; i++) {
            char c = origin.charAt(i);
            if (UNDERLINE == c) {
                toUpperCase = true;
            } else if (toUpperCase) {
                builder.append(Character.toUpperCase(c));
                toUpperCase = false;
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    /**
     * 将字符串转换为驼峰命名法
     * 注: 如果前两个字母均为大写，那么首字母不会转换为小写
     */
    public static String underscoreToCamelCase(String origin) {
        if (isEmpty(origin)) {
            return origin;
        }

        StringBuilder builder = new StringBuilder();
        boolean toUpperCase = false;
        int length = origin.length();

        char first = origin.charAt(0);
        if (length > 1 && Character.isUpperCase(first) && Character.isUpperCase(origin.charAt(1))) {
            builder.append(first);
        } else {
            builder.append(Character.toLowerCase(first));
        }

        for (int i = 1; i < length; i++) {
            char c = origin.charAt(i);
            if (UNDERLINE == c) {
                toUpperCase = true;
            } else if (toUpperCase) {
                builder.append(Character.toUpperCase(c));
                toUpperCase = false;
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    /**
     * 通用toString方法，可以直接输出数组、多维数组内的值而不是句柄
     */
    public static <T> String toString(T t) {
        if (t == null) {
            return null;
        }
        Class<?> tClass = t.getClass();
        if (tClass.isArray()) {
            if (tClass == byte[].class)
                return Arrays.toString((byte[]) t);
            else if (tClass == short[].class)
                return Arrays.toString((short[]) t);
            else if (tClass == int[].class)
                return Arrays.toString((int[]) t);
            else if (tClass == long[].class)
                return Arrays.toString((long[]) t);
            else if (tClass == char[].class)
                return Arrays.toString((char[]) t);
            else if (tClass == float[].class)
                return Arrays.toString((float[]) t);
            else if (tClass == double[].class)
                return Arrays.toString((double[]) t);
            else if (tClass == boolean[].class)
                return Arrays.toString((boolean[]) t);
            else { // t is an array of object references
                return Arrays.deepToString((Object[]) t);
            }
        } else {  // t is non-null and not an array
            return t.toString();
        }
    }

    /**
     * 支持负值参数的substring方法
     */
    public static String substring(String origin, int beginIndex) {
        if (beginIndex < 0) {
            beginIndex = origin.length() + beginIndex;
        }
        return origin.substring(beginIndex);
    }

    /**
     * 支持负值参数的substring方法
     */
    public static String substring(String origin, int beginIndex, int endIndex) {
        if (beginIndex < 0) {
            beginIndex = origin.length() + beginIndex;
        }
        if (endIndex < 0) {
            endIndex = origin.length() + endIndex;
        }
        return origin.substring(beginIndex, endIndex);
    }
}
