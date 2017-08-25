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
package com.peknight.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务运行状态，分为 开启/未开启 初始化/未初始化 运行/未运行 繁忙/空闲 销毁/未销毁 异常/正常 六个状态
 * 其中异常/正常状态可以出现在服务运行的任意阶段，如果状态为异常，那么状态一定是被关闭（未开启）的
 * 只有服务处于开启状态且已初始化，才可能为运行状态
 * 只有服务处于运行状态，才可能为繁忙状态
 * 如果服务处于销毁状态，那么一定不会处于开启/运行/繁忙状态
 * 服务可以未初始化就被销毁
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/8/11.
 */
public class State implements Comparable<State> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(State.class);

    public static final byte NEW = 0;

    public static final byte OPEN = 1 << 0;

    public static final byte INIT = 1 << 1;

    public static final byte RUNNING = 1 << 2;

    public static final byte BUSY = 1 << 3;

    public static final byte FINALIZE = 1 << 4;

    public static final byte ERROR = 1 << 5;

    protected byte state;

    public State() {
        this.state = NEW;
    }

    public byte getState() {
        return state;
    }

    public boolean isOpen() {
        return (state & OPEN) == OPEN;
    }

    public boolean isInit() {
        return (state & ERROR) != ERROR && (state & INIT) == INIT;
    }

    public boolean isRunning() {
        return (state & ERROR) != ERROR && (state & RUNNING) == RUNNING;
    }

    public boolean isBusy() {
        return (state & ERROR) != ERROR && (state & BUSY) == BUSY;
    }

    public boolean isFinalize() {
        return (state & ERROR) != ERROR && (state & FINALIZE) == FINALIZE;
    }

    public boolean isError() {
        return (state & ERROR) == ERROR;
    }

    public boolean setOpen(boolean isOpen) {
        if ((state & (INIT | RUNNING | BUSY | FINALIZE | ERROR)) != 0) {
            LOGGER.warn("Can Not Open");
            return false;
        } else {
            state = (byte) (isOpen ? state | OPEN : state | ERROR);
            return true;
        }
    }

    public boolean setInit(boolean isInit) {
        if ((state & (RUNNING | BUSY | FINALIZE | ERROR)) != 0 ) {
            LOGGER.warn("Can Not Init");
            return false;
        } else {
            state = (byte) (isInit ? state | OPEN | INIT : state | INIT | ERROR & ~OPEN);
            return true;
        }
    }

    public boolean setRunning(boolean isRunning) {
        if ((state & (BUSY | FINALIZE | ERROR)) != 0) {
            LOGGER.warn("Can Not Set Running");
            return false;
        } else {
            state = (byte) (isRunning ? state | OPEN | INIT | RUNNING : state | OPEN | INIT & ~RUNNING);
            return true;
        }
    }

    public boolean setBusy(boolean isBusy) {
        if ((state & (FINALIZE | ERROR)) != 0) {
            LOGGER.warn("Can Not Set Busy");
            return false;
        } else {
            state = (byte) (isBusy ? state | OPEN | INIT | RUNNING | BUSY : state | OPEN | INIT | RUNNING & ~BUSY);
            return true;
        }
    }

    public boolean setFinalize(boolean isFinalize) {
        if ((state & OPEN) != OPEN) {
            LOGGER.warn("Not Opened");
            return false;
        } else {
            state = (byte) (isFinalize ? state & ~OPEN & ~RUNNING & ~BUSY | FINALIZE : state & ~OPEN & ~RUNNING & ~BUSY | FINALIZE | ERROR);
            return true;
        }
    }

    public boolean setError(boolean isError) {
        state = (byte) (isError ? state & ~OPEN | ERROR : state & ~ERROR);
        return true;
    }

    public boolean refresh() {
        state = NEW;
        return true;
    }

    public String info() {
        if ((state & (ERROR | BUSY)) == (ERROR | BUSY)) {
            return "ERROR [BUSY]";
        } else if ((state & (ERROR | RUNNING)) == (ERROR | RUNNING)) {
            return "ERROR [RUNNING]";
        } else if ((state & (ERROR | FINALIZE)) == (ERROR | FINALIZE)) {
            return "ERROR [FINALIZE]";
        } else if (state == (ERROR | INIT)) {
            return "ERROR [INIT]";
        } else if (state == ERROR) {
            return "ERROR [OPEN]";
        } else if ((state & FINALIZE) == FINALIZE) {
            return "FINALIZED";
        } else if ((state & BUSY) == BUSY) {
            return "BUSY";
        } else if ((state & RUNNING) == RUNNING) {
            return "RUNNING";
        } else if ((state & INIT) == INIT) {
            return "INIT";
        } else if ((state & OPEN) == OPEN) {
            return "OPEN";
        } else if (state == NEW) {
            return "NEW";
        } else {
            LOGGER.error("Impossible!? state={}", Integer.toBinaryString(state));
            return "Impossible!? state=" + Integer.toBinaryString(state);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State state1 = (State) o;

        return state == state1.state;
    }

    private int priority() {
        if ((state & ERROR) == ERROR) {
            return 0;
        }
        if ((state & FINALIZE) == FINALIZE) {
            return 1 << 0;
        }
        if ((state & BUSY) == BUSY) {
            return 1 << 1;
        }
        if ((state & RUNNING) == RUNNING) {
            return 1 << 5;
        }
        if ((state & INIT) == INIT) {
            return 1 << 4;
        }
        if ((state & OPEN) == OPEN) {
            return 1 << 3;
        }
        return 1 << 2;
    }

    @Override
    public int compareTo(State o) {
        int oPriority = -1;
        if (o != null) {
            oPriority = o.priority();
        }
        return this.priority() - oPriority;
    }

    @Override
    public int hashCode() {
        return (int) state;
    }

    @Override
    public String toString() {
        return info();
    }
}
