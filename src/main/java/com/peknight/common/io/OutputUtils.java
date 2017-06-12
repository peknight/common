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
    public static final String KEY_SEPARATOR = "\t";

    public static final String MAP_SEPARATOR = "\t";

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
