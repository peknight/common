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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Metadata上下文
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/8/8.
 */
public final class MetadataContext {

    private MetadataContext() {}

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataContext.class);

    private static final Map<String, ClassMetadata> CLASS_METADATA_CONTEXT = new HashMap<>();
    private static final Map<Constructor, ConstructorMetadata> CONSTRUCTOR_METADATA_CONTEXT = new HashMap<>();
    private static final Map<Method, MethodMetadata> METHOD_METADATA_CONTEXT = new HashMap<>();

    public static ClassMetadata getClassMetadata(Type type) {
        String typeName = type.getTypeName();
        if (CLASS_METADATA_CONTEXT.containsKey(typeName)) {
            return CLASS_METADATA_CONTEXT.get(typeName);
        } else {
            ClassMetadata classMetadata = null;
            if (ParameterizedType.class.isAssignableFrom(type.getClass())) {
                ParameterizedType parameterizedType = ((ParameterizedType) type);
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                List<ClassMetadata> componentClassMetadataList = new ArrayList<>(actualTypeArguments.length);
                for (Type actualTypeArgument : actualTypeArguments) {
                    componentClassMetadataList.add(getClassMetadata(actualTypeArgument));
                }
                classMetadata = new ClassMetadata((Class) parameterizedType.getRawType(), componentClassMetadataList);
            } else if (Class.class.isAssignableFrom(type.getClass())) {
                classMetadata = new ClassMetadata((Class) type);
            } else {
                LOGGER.error("What Is This Type?! {}", type.getClass().getName());
                return null;
            }
            CLASS_METADATA_CONTEXT.put(typeName, classMetadata);
            classMetadata.getComponentClassMetadataList();
            classMetadata.getConstructorMetadataSet();
            classMetadata.getEnumValues();
            return classMetadata;
        }
    }

    public static ConstructorMetadata getConstructorMetadata(Constructor constructor) {
        if (CONSTRUCTOR_METADATA_CONTEXT.containsKey(constructor)) {
            return CONSTRUCTOR_METADATA_CONTEXT.get(constructor);
        } else {
            ConstructorMetadata constructorMetadata = new ConstructorMetadata(constructor);
            CONSTRUCTOR_METADATA_CONTEXT.put(constructor, constructorMetadata);
            constructorMetadata.getParamList();
            return constructorMetadata;
        }
    }

    public static MethodMetadata getMethodMetadata(Method method) {
        if (METHOD_METADATA_CONTEXT.containsKey(method)) {
            return METHOD_METADATA_CONTEXT.get(method);
        } else {
            MethodMetadata methodMetadata = new MethodMetadata(method);
            METHOD_METADATA_CONTEXT.put(method, methodMetadata);
            methodMetadata.getParamList();
            methodMetadata.getReturnClassMetadata();
            return methodMetadata;
        }
    }
}
