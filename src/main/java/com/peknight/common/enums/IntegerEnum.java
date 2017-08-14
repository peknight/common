package com.peknight.common.enums;

/**
 * Int类型枚举接口
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/8/11.
 */
public interface IntegerEnum<E extends Enum<E>> {
    int getValue();
    E findByValue(int value);
}
