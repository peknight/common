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
package com.peknight.common.reflect.material;

import com.peknight.common.reflect.util.MethodUtils;
import com.peknight.common.string.StringUtils;
import com.peknight.common.validation.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 方法创建材料
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/8/9.
 */
public class MethodMaterial<T> {
    private BeanMaterial<T, T> invoker;

    private Object returnValue;

    private String returnBeanName;

    private Method method;

    private List<BeanMaterial> paramList;

    public MethodMaterial(Class<T> tClass, BeanMaterial<T, T> invoker, String methodName, List<BeanMaterial> paramList, String returnBeanName) throws NoSuchMethodException {
        Assert.notNull("Param Can Not Be Null", tClass, methodName, paramList);
        this.invoker = invoker;
        this.paramList = paramList;
        this.returnBeanName = returnBeanName;
        Class[] parameterTypes = MethodUtils.getParameterTypes(paramList);
        method = MethodUtils.getMethod(tClass, methodName, parameterTypes);
    }

    public Object invokeMethod() throws BeanCreationException, InvocationTargetException, IllegalAccessException {
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }
        returnValue = method.invoke(invoker == null ? null : invoker.getBean(), MethodUtils.getArgs(paramList));
        if (!StringUtils.isEmpty(returnBeanName)) {
            BeanContext.put(returnBeanName, returnValue);
        }
        return returnValue;
    }

    public Object getReturnValue() throws IllegalAccessException, BeanCreationException, InvocationTargetException {
        if (returnValue == null) {
            invokeMethod();
        }
        return returnValue;
    }
}
