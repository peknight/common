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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private static final ThreadLocal<Map<String, DateFormat>> DATE_FORMAT_CONTEXT = new ThreadLocal<>();

    public static Map getDateFormatMap() {
        if (DATE_FORMAT_CONTEXT.get() == null) {
            DATE_FORMAT_CONTEXT.set(new ConcurrentHashMap<>());
        }
        return DATE_FORMAT_CONTEXT.get();
    }

    public static DateFormat getDefaultDateFormat() {
        return getDateFormat(DEFAULT_DATE_FORMAT);
    }

    public static DateFormat getDateFormat(String dateFormat) {
        Map<String, DateFormat> dateFormatMap = getDateFormatMap();
        dateFormatMap.putIfAbsent(dateFormat, new SimpleDateFormat(dateFormat));
        return dateFormatMap.get(dateFormat);
    }

    public static String format(Date date) {
        return getDefaultDateFormat().format(date);
    }

    public static String format(Date date, String dateFormat) {
        return getDateFormat(dateFormat).format(date);
    }

    public static Date parse(String dateStr) throws ParseException {
        return getDefaultDateFormat().parse(dateStr);
    }

    public static Date parse(String dateStr, String dateFormat) throws ParseException {
        return getDateFormat(dateFormat).parse(dateStr);
    }
}
