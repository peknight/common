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
package com.peknight.common.logging;

import com.peknight.common.string.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具类，建议仅在练习测试中使用
 *
 * ONLY! FOR! DEMO! AND! TEST!
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/8/2.
 */
public final class LogUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.class.getPackage().getName());

    private LogUtils() {}

    public static void trace(Object... object) {
        LOGGER.trace(StringUtils.substring(StringUtils.toString(object), 1, -1));
    }

    public static void debug(Object... object) {
        LOGGER.debug(StringUtils.substring(StringUtils.toString(object), 1, -1));
    }

    public static void info(Object... object) {
        LOGGER.info(StringUtils.substring(StringUtils.toString(object), 1, -1));
    }

    public static void warn(Object... object) {
        LOGGER.warn(StringUtils.substring(StringUtils.toString(object), 1, -1));
    }

    public static void error(Object... object) {
        LOGGER.error(StringUtils.substring(StringUtils.toString(object), 1, -1));
    }
}
