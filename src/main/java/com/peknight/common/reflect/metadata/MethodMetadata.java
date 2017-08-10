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

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 方法信息
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/8/8.
 */
public class MethodMetadata {

    private Method method;

    private ClassMetadata returnClassMetadata;

    private List<ClassMetadata> paramList;

    MethodMetadata(Method method) {
        this.method = method;
    }

    public Method getDeclaredMethod() {
        return this.method;
    }

    public List<ClassMetadata> getParamList() {
        if (paramList == null) {
            Type[] genericParameterTypes = method.getGenericParameterTypes();
            int length = genericParameterTypes.length;
            paramList = new ArrayList<>(length);
            for (Type parameterType : genericParameterTypes) {
                paramList.add(MetadataContext.getClassMetadata(parameterType));
            }
        }
        return paramList;
    }

    public ClassMetadata getReturnClassMetadata() {
        if (returnClassMetadata == null) {
            returnClassMetadata = MetadataContext.getClassMetadata(method.getGenericReturnType());
        }
        return returnClassMetadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodMetadata that = (MethodMetadata) o;

        return method.equals(that.method);
    }

    @Override
    public int hashCode() {
        return method.hashCode();
    }

    @Override
    public String toString() {
        return "MethodMetadata{" +
                "method=" + method +
                ", returnClassMetadata=" + returnClassMetadata +
                ", paramList=" + paramList +
                '}';
    }
}