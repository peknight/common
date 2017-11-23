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
package com.peknight.common.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.peknight.common.string.JsonUtils;

/**
 * 返回值为空的方法可以将返回值替换为此类型，便于切面观察方法执行情况
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/11/22.
 */
public class CommonResult<T> {
    private int code;
    private String message;
    private T value;

    public CommonResult() {
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
        try {
            return JsonUtils.write(this);
        } catch (JsonProcessingException e) {
            return "{" +
                    "\"code\":" + code +
                    ", \"message\":\"" + message + '\"' +
                    ", \"value\":" + value +
                    '}';
        }
    }
}
