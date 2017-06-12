package com.peknight.common.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 *
 * IO工具类
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/6/12.
 */
public final class InputUtils {
    private InputUtils() {}

    public static BufferedReader getBufferedReader(String fileName) throws FileNotFoundException, UnsupportedEncodingException {
        return getBufferedReader(fileName, IOConstants.DEFAULT_CHARSET);
    }

    public static BufferedReader getBufferedReader(String fileName, String charset) throws FileNotFoundException, UnsupportedEncodingException {
        if (fileName == null || !new File(fileName).exists()) {
            return null;
        }
        if (charset == null) {
            charset = IOConstants.DEFAULT_CHARSET;
        }
        return new BufferedReader(new InputStreamReader(new FileInputStream(fileName), charset));
    }

    public static BufferedReader getBufferedReader(File file) throws FileNotFoundException, UnsupportedEncodingException {
        return getBufferedReader(file, IOConstants.DEFAULT_CHARSET);
    }

    public static BufferedReader getBufferedReader(File file, String charset) throws FileNotFoundException, UnsupportedEncodingException {
        if (file == null || !file.exists()) {
            return null;
        }
        if (charset == null) {
            charset = IOConstants.DEFAULT_CHARSET;
        }
        return new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
    }
}
