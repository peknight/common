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

import com.peknight.common.springframework.context.ApplicationContextHolder;
import com.peknight.common.springframework.context.CommonSpringBeanFilter;
import com.peknight.common.springframework.context.SpringBeanFilter;
import com.peknight.common.validation.Assert;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 对象容器
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/8/8.
 */
public final class BeanContext {

    private BeanContext() {}

    public static final String BEAN_NAME_REG = "^([a-zA-Z\\$_][\\w\\$]*)(\\.[a-zA-Z\\$_][\\w\\$]*)*$";

    private static final Map<String, Object> BEAN_CONTEXT = new HashMap<>();

    public static void put(String name, Object bean) {
        Assert.notNull("Name And Bean Can Not Be Null", name, bean);
        if (!name.matches(BEAN_NAME_REG)) {
            throw new IllegalArgumentException(name);
        }
        BEAN_CONTEXT.put(name, bean);
    }

    public static Object get(String name) {
        return BEAN_CONTEXT.get(name);
    }

    public static <T, E extends T> List<E> get(Class<T> tClass) {
        List<E> beanList = new ArrayList<>();
        for (Map.Entry<String, Object> bean : BEAN_CONTEXT.entrySet()) {
            if (tClass.isAssignableFrom(bean.getValue().getClass())) {
                beanList.add((E) bean.getValue());
            }
        }
        return beanList;
    }

    public static Object remove(String name) {
        return BEAN_CONTEXT.remove(name);
    }

    public static List<String> listKey() {
        Set<String> keySet = BEAN_CONTEXT.keySet();
        List<String> keyList = new ArrayList<>(keySet.size());
        keyList.addAll(keySet);
        return keyList;
    }

    public static void addSpringBeans() {
        ApplicationContext context = ApplicationContextHolder.getApplicationContext();
        if (context == null) {
            return;
        }
        String[] beanNames = context.getBeanDefinitionNames();
        SpringBeanFilter springBeanFilter = new CommonSpringBeanFilter();
        for (String beanName : beanNames) {
            if (springBeanFilter.beanNameFilter(beanName)) {
                BEAN_CONTEXT.put(beanName, context.getBean(beanName));
            }
        }
    }
}