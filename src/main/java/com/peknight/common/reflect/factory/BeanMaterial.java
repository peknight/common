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
package com.peknight.common.reflect.factory;

import com.peknight.common.string.JsonUtils;
import com.peknight.common.string.StringUtils;
import com.peknight.common.validation.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Bean创建材料
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/8/9.
 */
public class BeanMaterial<T, E extends T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanMaterial.class);

    protected E bean;

    protected Class<T> declaredClass;

    protected Class<E> actualClass;

    protected String beanName;

    protected String beanValue;

    protected ConstructorMaterial<E> beanConstructor;

    public BeanMaterial(Class<T> declaredClass, Class<E> actualClass, String beanName, String beanValue, ConstructorMaterial<E> beanConstructor) {
        Assert.notAllNull("Class Can Not Be Null", declaredClass, actualClass);
        Assert.notAllNull("Value Or Constructor Can Not Be Null", beanValue, beanConstructor);
        this.declaredClass = declaredClass;
        this.actualClass = actualClass;
        this.beanName = beanName;
        this.beanValue = beanValue;
        this.beanConstructor = beanConstructor;
        if (declaredClass == null) {
            this.declaredClass = (Class<T>) actualClass;
        }
        if (actualClass == null) {
            this.actualClass = (Class<E>) declaredClass;
        }
    }

    public E getBean() throws BeanCreationException {
        parseBeanValue();
        invokeConstructor();
        customParser();
        if (bean == null) {
            throw new BeanCreationException();
        }
        if (!StringUtils.isEmpty(beanName)) {
            BeanContext.put(beanName, bean);
        }
        return bean;
    }

    public E parseBeanValue() {
        if (bean == null && beanValue != null) {
            if (beanValue.matches(BeanContext.RAW_REG)) {
                bean = (E) BeanContext.getByRawName(beanValue);
            } else {
                try {
                    bean = JsonUtils.read(beanValue, actualClass);
                } catch (IOException e) {
                    LOGGER.error("Parse Bean Value Error! {}", e.getMessage(), e);
                }
            }
        }
        return bean;
    }

    public E invokeConstructor() throws BeanCreationException {
        if (bean == null && beanConstructor != null) {
            bean = beanConstructor.getBean();
        }
        return bean;
    }

    public E customParser() throws BeanCreationException {
        return bean;
    }

    public Class<T> getDeclaredClass() {
        return declaredClass;
    }

    public Class<E> getActualClass() {
        return actualClass;
    }

    public String getBeanName() {
        return beanName;
    }

    public String getBeanValue() {
        return beanValue;
    }

    public ConstructorMaterial<E> getBeanConstructor() {
        return beanConstructor;
    }
}
