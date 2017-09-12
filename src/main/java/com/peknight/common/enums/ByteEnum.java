package com.peknight.common.enums;

/**
 * Byte类型枚举接口
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/8/11.
 */
public interface ByteEnum<E extends Enum<E>> {
    byte getValue();
}
