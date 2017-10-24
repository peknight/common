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
package com.peknight.common.time;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DateFormat工具类
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/10/9.
 */
public final class DateFormatUtils {
    private DateFormatUtils() {}

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern(DEFAULT_DATE_FORMAT).withZone(ZoneId.systemDefault());

    public static final Map<String, DateTimeFormatter> DATE_TIME_FORMATTER_MAP = new ConcurrentHashMap<>();

    public static DateTimeFormatter ofPattern(String pattern) {
        DATE_TIME_FORMATTER_MAP.putIfAbsent(pattern, DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault()));
        return DATE_TIME_FORMATTER_MAP.get(pattern);
    }

    public static String format() {
        return DEFAULT_DATE_TIME_FORMATTER.format(Instant.now());
    }

    public static String format(Date date) {
        return format(date.getTime());
    }

    public static String format(long time) {
        return DEFAULT_DATE_TIME_FORMATTER.format(Instant.ofEpochMilli(time));
    }

    public static String format(String pattern) {
        return ofPattern(pattern).format(Instant.now());
    }

    public static String format(Date date, String pattern) {
        return format(date.getTime(), pattern);
    }

    public static String format(long time, String pattern) {
        return ofPattern(pattern).format(Instant.ofEpochMilli(time));
    }

    public static long parse(String dateStr) {
        return DEFAULT_DATE_TIME_FORMATTER.parse(dateStr, Instant::from).getEpochSecond();
    }

    public static long parse(String dateStr, String pattern) {
        return ofPattern(pattern).parse(dateStr, Instant::from).getEpochSecond();
    }
}
