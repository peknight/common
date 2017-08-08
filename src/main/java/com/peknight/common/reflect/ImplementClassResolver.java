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
package com.peknight.common.reflect;

import com.peknight.common.io.OutputUtils;
import com.peknight.common.logging.LogUtils;
import com.peknight.common.reflect.scanner.PackageResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/8/4.
 */
public class ImplementClassResolver extends PackageResolver<List<Class>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImplementClassResolver.class);

    private static final PrintWriter writer = getWriter();

    private static PrintWriter getWriter() {
        try {
            return OutputUtils.getPrintWriter("D:/Error.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Class clazz;

    public ImplementClassResolver(Class clazz) {
        this.clazz = clazz;
        this.targetObject = new ArrayList<>();
    }

    @Override
    public void resolve(Resource[] resources, MetadataReaderFactory metadataReaderFactory) throws IOException {
        for (Resource resource : resources) {
            MetadataReader reader = metadataReaderFactory.getMetadataReader(resource);
            String className = null;
            try {
                className = reader.getClassMetadata().getClassName();
                if (className.startsWith("java")) {
                    LogUtils.info(className);
                    Class tempClass = Class.forName(className);
                    int modifier = tempClass.getModifiers();
                    if (clazz.isAssignableFrom(tempClass) && !Modifier.isInterface(modifier) && !Modifier.isAbstract(modifier)) {
                        targetObject.add(tempClass);
                    }
                }
            } catch (ClassNotFoundException e) {
                LOGGER.error("This will not happened: {}", e.getMessage(), e);
                continue;
            } catch (Throwable e) {
                LOGGER.error("Parse Class Name Error[{}]: {}", className, e.getMessage(), e);
                writer.println(className);
                continue;
            }
        }
    }
}
