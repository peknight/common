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

import com.peknight.common.springframework.context.ApplicationContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;

/**
 * 基于Spring包扫描机制实现的包扫描
 *
 * {@link org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider findCandidateComponents(java.lang.String)}
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/8/3.
 */
public class PackageScanner<T> {

    private static final String RESOURCE_PATTERN = "/**/*.class";

    private ResourcePatternResolver resourcePatternResolver;

    private MetadataReaderFactory metadataReaderFactory;

    private PackageResolver<T> packageResolver;

    private ClassNameFilter classNameFilter;

    public PackageScanner(PackageResolver<T> packageResolver) {
        this(packageResolver, null, null);
    }

    public PackageScanner(PackageResolver<T> packageResolver, ResourceLoader resourceLoader) {
        this(packageResolver, resourceLoader, null);
    }

    public PackageScanner(PackageResolver<T> packageResolver, ClassNameFilter classNameFilter) {
        this(packageResolver, null, classNameFilter);
    }

    public PackageScanner(PackageResolver<T> packageResolver, ResourceLoader resourceLoader, ClassNameFilter classNameFilter) {
        if (resourceLoader == null) {
            if (ApplicationContextHolder.getApplicationContext() != null) {
                this.resourcePatternResolver = new PathMatchingResourcePatternResolver(ApplicationContextHolder.getApplicationContext());
            } else {
                this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
            }
        } else {
            this.resourcePatternResolver = new PathMatchingResourcePatternResolver(resourceLoader);
        }
        this.metadataReaderFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);
        this.packageResolver = packageResolver;
        this.classNameFilter = classNameFilter;
    }

    public void resolveBasePackage(String basePackage) throws IOException {
        String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                + ClassUtils.convertClassNameToResourcePath(basePackage)
                + RESOURCE_PATTERN;
        Resource[] resources = resourcePatternResolver.getResources(pattern);
        packageResolver.resolve(resources, metadataReaderFactory, classNameFilter);
    }

    public void resolveBasePackages(String... basePackages) throws IOException {
        for (String basePackage : basePackages) {
            resolveBasePackage(basePackage);
        }
    }
}