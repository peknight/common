package com.peknight.common.string;

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
}
