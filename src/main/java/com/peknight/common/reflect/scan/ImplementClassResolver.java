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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 * 使用Spring包扫描机制实现获取某类型的所有子类
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/8/4.
 */
public class ImplementClassResolver extends PackageResolver<Set<Class>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImplementClassResolver.class);

    private Class clazz;

    public ImplementClassResolver(Class clazz) {
        this.clazz = clazz;
        this.targetObject = new HashSet<>();
    }

    @Override
    public void resolve(Resource[] resources, MetadataReaderFactory metadataReaderFactory, ClassNameFilter classNameFilter) throws IOException {
        for (Resource resource : resources) {
            MetadataReader reader = metadataReaderFactory.getMetadataReader(resource);
            String className = reader.getClassMetadata().getClassName();
            if (classNameFilter == null || classNameFilter.classNameFilter(className)) {
                try {
                    Class tempClass = Class.forName(className);
                    int modifier = tempClass.getModifiers();
                    if (clazz.isAssignableFrom(tempClass) && !Modifier.isInterface(modifier) && !Modifier.isAbstract(modifier)) {
                        targetObject.add(tempClass);
                    }
                } catch (ClassNotFoundException e) {
                    LOGGER.error("This will not happened: {}", e.getMessage(), e);
                    continue;
                } catch (Throwable e) {
                    LOGGER.error("Parse Class Name Error[{}]: {}", className, e.getMessage(), e);
                    continue;
                }
            }
        }
    }
}
