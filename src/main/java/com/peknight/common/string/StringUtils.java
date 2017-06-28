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
import java.util.HashSet;

/**
 * String工具类
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/6/12.
 */
public final class StringUtils {
    private StringUtils() {}

    /**
     * 将字符串转换为帕斯卡命名法（大驼峰命名法）
     */
    public static String toPascalCase(String origin) {
        if (origin == null || origin.length() == 0) {
            return origin;
        }
        if (origin.indexOf('_') != -1) {
            String[] splits = origin.split("_");
            StringBuilder builder = new StringBuilder("");
            for (String split : splits) {
                if (split.length() == 0) {
                    continue;
                }
                builder.append(toPascalCase(split));
            }
            return builder.toString();
        }
        if (Character.isUpperCase(origin.charAt(0))) {
            return origin;
        }
        char[] chars = origin.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    /**
     * 将字符串转换为驼峰命名法
     * 注: 如果前两个字母均为大写，那么首字母不会转换为小写
     */
    public static String toCamelCase(String origin) {
        if (origin == null || origin.length() == 0) {
            return origin;
        }
        if (origin.indexOf('_') != -1) {
            String[] splits = origin.split("_");
            StringBuilder builder = new StringBuilder("");
            builder.append(toCamelCase(splits[0]));
            for (int i = 1; i < splits.length; i++) {
                if (splits[i].length() == 0) {
                    continue;
                }
                builder.append(toPascalCase(splits[i]));
            }
            return builder.toString();
        }
        if (Character.isLowerCase(origin.charAt(0)) ||
                (origin.length() > 1 && Character.isUpperCase(origin.charAt(1)) &&
                Character.isUpperCase(origin.charAt(0)))){
            return origin;
        }
        char[] chars = origin.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

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
}
