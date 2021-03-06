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
package com.peknight.common.reflect.scan;

import java.util.HashSet;
import java.util.Set;

/**
 * 常规类名过滤，过滤内容见PACKAGE_IGNORE
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/8/8.
 */
public class CommonClassNameFilter implements ClassNameFilter {

    private static final Set<String> PACKAGE_IGNORE = new HashSet<>();

    static {
        PACKAGE_IGNORE.add("ch.qos");
        PACKAGE_IGNORE.add("com.intellij");
        PACKAGE_IGNORE.add("com.oracle");
        PACKAGE_IGNORE.add("com.sun");
        PACKAGE_IGNORE.add("javafx");
        PACKAGE_IGNORE.add("jdk");
        PACKAGE_IGNORE.add("oracle");
        PACKAGE_IGNORE.add("org.aspectj.weaver");
        PACKAGE_IGNORE.add("org.springframework");
        PACKAGE_IGNORE.add("sun");
    }

    @Override
    public boolean classNameFilter(String className) {
        if (className.matches(".*\\$\\d+$")) {
            return false;
        }
        for (String ignore : PACKAGE_IGNORE) {
            if (className.startsWith(ignore)) {
                return false;
            }
        }
        return true;
    }
}
