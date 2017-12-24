package com.peknight.common.logging;

import com.peknight.common.string.StringUtils;

import java.util.Objects;

/**
 * 返回值为空的方法可以将返回值替换为此类型，便于切面观察方法执行情况
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/11/22.
 */
public class CommonResult<T> {
    public static final String SUCCESS_MESSAGE = "Success";

    private int code;
    private String message;
    private T value;

    public CommonResult() {
    }

    public CommonResult(T value) {
        this.code = 0;
        this.message = SUCCESS_MESSAGE;
        this.value = value;
    }

    public CommonResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public CommonResult(int code, String message, T value) {
        this.code = code;
        this.message = message;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        if (code == 0) {
            if (Objects.equals(SUCCESS_MESSAGE, message)) {
                return StringUtils.toString(value);
            } else {
                return "[" + message + "] " + StringUtils.toString(value);
            }
        } else {
            return "[" + code + ": " + message + "] " + value;
        }
    }
}
