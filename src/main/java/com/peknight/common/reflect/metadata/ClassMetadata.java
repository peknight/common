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
package com.peknight.common.reflect.metadata;

import com.peknight.common.reflect.util.ClassUtils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 类信息
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/8/3.
 */
public class ClassMetadata<T> {
    private Class<T> tClass;

    private List<ClassMetadata> componentClassMetadataList;

    private Set<ClassMetadata> implementClassMetadataSet;

    private Set<ConstructorMetadata<T>> constructorMetadataSet;

    private List<T> enumValues;

    ClassMetadata(Class<T> tClass) {
        this(tClass, null);
    }

    ClassMetadata(Class<T> tClass, List<ClassMetadata> componentClassMetadataList) {
        this.tClass = tClass;
        this.componentClassMetadataList = componentClassMetadataList;
    }

    public Class<T> getDeclaredClass() {
        return this.tClass;
    }

    public List<ClassMetadata> getComponentClassMetadataList() {
        if (componentClassMetadataList != null) {
            return componentClassMetadataList;
        } else if (tClass.isArray()) {
            componentClassMetadataList = new ArrayList<>(1);
            componentClassMetadataList.add(MetadataContext.getClassMetadata(ClassUtils.getFinalComponentClass(tClass)));
            return componentClassMetadataList;
        } else {
            return null;
        }
    }

    public Set<ClassMetadata> getImplementClassMetadataSet(String... basePackages) throws IOException {
        if (implementClassMetadataSet != null) {
            return implementClassMetadataSet;
        } else {
            Set<Class> implementClassSet =  ClassUtils.listImplementClass(tClass, basePackages);
            if (implementClassSet == null) {
                return null;
            } else {
                implementClassMetadataSet = new HashSet<>();
                for (Class implementClass : implementClassSet) {
                    implementClassMetadataSet.add(MetadataContext.getClassMetadata(implementClass));
                }
                return implementClassMetadataSet;
            }
        }
    }

    public Set<ConstructorMetadata<T>> getConstructorMetadataSet() {
        if (constructorMetadataSet != null) {
            return constructorMetadataSet;
        } else if (ClassUtils.isPlainValue(tClass) || tClass.isInterface()) {
            return null;
        } else {
            Constructor<T>[] constructors = (Constructor<T>[]) tClass.getConstructors();
            int length = constructors.length;
            if (length == 0) {
                return null;
            } else {
                constructorMetadataSet = new HashSet<>();
                for (Constructor<T> constructor : constructors) {
                    constructorMetadataSet.add(MetadataContext.getConstructorMetadata(constructor));
                }
                return constructorMetadataSet;
            }
        }
    }

    public List<T> getEnumValues() {
        if (enumValues != null) {
            return enumValues;
        } else if (tClass.isEnum()) {
            T[] enumValueArray = tClass.getEnumConstants();
            enumValues = new ArrayList<>(enumValueArray.length);
            for (T enumValue : enumValueArray) {
                enumValues.add(enumValue);
            }
            return enumValues;
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassMetadata<?> that = (ClassMetadata<?>) o;

        if (!tClass.equals(that.tClass)) return false;
        return componentClassMetadataList != null ? componentClassMetadataList.equals(that.componentClassMetadataList) : that.componentClassMetadataList == null;
    }

    @Override
    public int hashCode() {
        int result = tClass.hashCode();
        result = 31 * result + (componentClassMetadataList != null ? componentClassMetadataList.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClassMetadata{" +
                "tClass=" + tClass +
                ", componentClassMetadataList=" + componentClassMetadataList +
                ", implementClassMetadataSet=" + implementClassMetadataSet +
                ", constructorMetadataSet=" + constructorMetadataSet +
                ", enumValues=" + enumValues +
                '}';
    }
}
