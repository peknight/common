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

import java.util.List;
import java.util.Map;

/**
 * Map创建材料
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/8/9.
 */
public class MapMaterial<T extends Map, E extends T> extends BeanMaterial<T, E> {

    private List<List<BeanMaterial>> components;

    public MapMaterial(Class<T> declaredClass, Class<E> actualClass, String beanName, String beanValue, ConstructorMaterial<E> beanConstructor, MethodMaterial beanMethod, List<List<BeanMaterial>> components) {
        super(declaredClass, actualClass, beanName, beanValue, beanConstructor, beanMethod);
        this.components = components;
    }

    @Override
    public E customParser() throws BeanCreationException {
        if (bean != null && components != null) {
            for (List<BeanMaterial> component : components) {
                if (component.size() != 2) {
                    throw new BeanCreationException();
                } else {
                    bean.put(component.get(0).getBean(), component.get(1).getBean());
                }
            }
        }
        return bean;
    }
}
