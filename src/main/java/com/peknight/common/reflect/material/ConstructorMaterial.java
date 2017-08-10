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
import com.peknight.common.validation.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * 构造方法创建材料
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/8/9.
 */
public class ConstructorMaterial<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConstructorMaterial.class);

    private T bean;

    private Constructor<T> constructor;

    private List<BeanMaterial> paramList;

    public ConstructorMaterial(Class<T> tClass, List<BeanMaterial> paramList) throws NoSuchMethodException {
        Assert.notNull("Param Can Not Be Null", tClass, paramList);
        this.paramList = paramList;
        Class[] parameterTypes = MethodUtils.getParameterTypes(paramList);
        constructor = MethodUtils.getConstructor(tClass, parameterTypes);
    }

    public T getBean() throws BeanCreationException {
        if (bean == null) {
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            try {
                bean = constructor.newInstance(MethodUtils.getArgs(paramList));
            } catch (ReflectiveOperationException e) {
                LOGGER.error("Invode Constructor Error! {}", e.getMessage(), e);
                throw new BeanCreationException(e);
            }
        }
        return bean;
    }

    public Constructor<T> getConstructor() {
        return constructor;
    }
}
