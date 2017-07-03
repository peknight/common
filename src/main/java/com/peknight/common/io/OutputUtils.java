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
package com.peknight.common.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by PeKnight on 2017/6/12.
 */
public final class OutputUtils {
    public static final String KEY_SEPARATOR = ", ";

    public static final String MAP_SEPARATOR = ", ";

    public static final String FILE_SEPARATOR = "\n";

    private OutputUtils() {}

    public static PrintWriter getPrintWriter(String fileName) throws FileNotFoundException, UnsupportedEncodingException {
        return getPrintWriter(fileName, IOConstants.DEFAULT_CHARSET, true);
    }

    public static PrintWriter getPrintWriter(String fileName, String charset) throws FileNotFoundException, UnsupportedEncodingException {
        return getPrintWriter(fileName, charset, true);
    }

    public static PrintWriter getPrintWriter(String fileName, boolean autoFlush) throws FileNotFoundException, UnsupportedEncodingException {
        return getPrintWriter(fileName, IOConstants.DEFAULT_CHARSET, autoFlush);
    }

    public static PrintWriter getPrintWriter(String fileName, String charset, boolean autoFlush) throws FileNotFoundException, UnsupportedEncodingException {
        if (fileName == null || !new File(fileName).exists()) {
            return null;
        }
        if (charset == null) {
            charset = IOConstants.DEFAULT_CHARSET;
        }
        return new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileName), charset), autoFlush);
    }

    public static PrintWriter getPrintWriter(File file) throws FileNotFoundException, UnsupportedEncodingException {
        return getPrintWriter(file, IOConstants.DEFAULT_CHARSET, true);
    }

    public static PrintWriter getPrintWriter(File file, String charset) throws FileNotFoundException, UnsupportedEncodingException {
        return getPrintWriter(file, charset, true);
    }

    public static PrintWriter getPrintWriter(File file, boolean autoFlush) throws FileNotFoundException, UnsupportedEncodingException {
        return getPrintWriter(file, IOConstants.DEFAULT_CHARSET, autoFlush);
    }

    public static PrintWriter getPrintWriter(File file, String charset, boolean autoFlush) throws FileNotFoundException, UnsupportedEncodingException {
        if (file == null || !file.exists()) {
            return null;
        }
        if (charset == null) {
            charset = IOConstants.DEFAULT_CHARSET;
        }
        return new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), charset), autoFlush);
    }
}
