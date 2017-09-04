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

    public static final byte FINALIZED = 1 << 4;

    public static final byte WARN = 1 << 5;

    public static final byte ERROR = 1 << 6;

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

    public boolean isFinalized() {
        return (state & ERROR) != ERROR && (state & FINALIZED) == FINALIZED;
    }

    public boolean isWarn() {
        return (state & WARN) == WARN;
    }

    public boolean isError() {
        return (state & ERROR) == ERROR;
    }

    public boolean setOpen(boolean isOpen) {
        if ((state & (INIT | RUNNING | BUSY | FINALIZED | ERROR)) != 0) {
            LOGGER.warn("Can Not Open");
            return false;
        } else {
            state = (byte) (isOpen ? state | OPEN : state | ERROR);
            return true;
        }
    }

    public boolean setInit(boolean isInit) {
        if ((state & (RUNNING | BUSY | FINALIZED | ERROR)) != 0 ) {
            LOGGER.warn("Can Not Init");
            return false;
        } else {
            state = (byte) (isInit ? state | OPEN | INIT : (state | INIT | ERROR) & ~OPEN);
            return true;
        }
    }

    public boolean setRunning(boolean isRunning) {
        if ((state & (FINALIZED | ERROR)) != 0) {
            LOGGER.warn("Can Not Set Running");
            return false;
        } else {
            state = (byte) (isRunning ? state | OPEN | INIT | RUNNING : (state | OPEN | INIT) & ~RUNNING & ~BUSY);
            return true;
        }
    }

    public boolean setBusy(boolean isBusy) {
        if ((state & (FINALIZED | ERROR)) != 0) {
            LOGGER.warn("Can Not Set Busy");
            return false;
        } else {
            state = (byte) (isBusy ? state | OPEN | INIT | RUNNING | BUSY : (state | OPEN | INIT | RUNNING) & ~BUSY);
            return true;
        }
    }

    public boolean setFinalized(boolean isFinalized) {
        if ((state & OPEN) != OPEN) {
            LOGGER.warn("Not Opened");
            return false;
        } else {
            state = (byte) (isFinalized ? (state & ~OPEN & ~RUNNING & ~BUSY) | FINALIZED : (state & ~OPEN & ~RUNNING & ~BUSY) | FINALIZED | ERROR);
            return true;
        }
    }

    public boolean setWarn(boolean isWarn) {
        state = (byte) (isWarn ? state | WARN : state & ~WARN);
        return true;
    }

    public boolean setError(boolean isError) {
        state = (byte) (isError ? (state & ~OPEN) | ERROR : state & ~ERROR);
        return true;
    }

    public boolean refresh() {
        state = NEW;
        return true;
    }

    public String info() {
        int basicState = state & (OPEN | INIT | RUNNING | BUSY | FINALIZED | WARN | ERROR);
        switch (basicState) {
            case NEW:
                return "NEW";
            case NEW | WARN:
                return "NEW [WARN]";
            case OPEN:
                return "OPEN";
            case OPEN | WARN:
                return "OPEN [WARN]";
            case OPEN | INIT:
                return "INIT";
            case OPEN | INIT | WARN:
                return "INIT [WARN]";
            case OPEN | INIT | RUNNING:
                return "RUNNING";
            case OPEN | INIT | RUNNING | WARN:
                return "RUNNING [WARN]";
            case OPEN | INIT | RUNNING | BUSY:
                return "BUSY";
            case OPEN | INIT | RUNNING | BUSY | WARN:
                return "BUSY [WARN]";
            case FINALIZED:
                return "FINALIZED [NO INIT]";
            case FINALIZED | WARN:
                return "FINALIZED [WARN, NO INIT]";
            case INIT | FINALIZED:
                return "FINALIZED";
            case INIT | FINALIZED | WARN:
                return "FINALIZED [WARN]";
            case ERROR:
                return "ERROR [OPEN]";
            case WARN | ERROR:
                return "ERROR [OPEN, WARN]";
            case INIT | ERROR:
                return "ERROR [INIT]";
            case INIT | WARN | ERROR:
                return "ERROR [INIT, WARN]";
            case INIT | RUNNING | ERROR:
                return "ERROR [RUNNING]";
            case INIT | RUNNING | WARN | ERROR:
                return "ERROR [RUNNING, WARN]";
            case INIT | RUNNING | BUSY | ERROR:
                return "ERROR [BUSY]";
            case INIT | RUNNING | BUSY | WARN | ERROR:
                return "ERROR [BUSY, WARN]";
            case FINALIZED | ERROR:
                return "ERROR [FINALIZED, NO INIT]";
            case FINALIZED | WARN | ERROR:
                return "ERROR [FINALIZED, WARN, NO INIT]";
            case INIT | FINALIZED | ERROR:
                return "ERROR [FINALIZED]";
            case INIT | FINALIZED | WARN | ERROR:
                return "ERROR [FINALIZED, WARN]";
            default:
                LOGGER.error("No Such State [{}]", state);
                return "NO SUCH STATE";
        }
    }

    public String simpleInfo() {
        int basicState = state & (OPEN | INIT | RUNNING | BUSY | FINALIZED | WARN | ERROR);
        switch (basicState) {
            case NEW:
                return "N";
            case NEW | WARN:
                return "n";
            case OPEN:
                return "O";
            case OPEN | WARN:
                return "o";
            case OPEN | INIT:
                return "I";
            case OPEN | INIT | WARN:
                return "i";
            case OPEN | INIT | RUNNING:
                return "R";
            case OPEN | INIT | RUNNING | WARN:
                return "r";
            case OPEN | INIT | RUNNING | BUSY:
                return "B";
            case OPEN | INIT | RUNNING | BUSY | WARN:
                return "b";
            case FINALIZED:
                return "D";
            case FINALIZED | WARN:
                return "d";
            case INIT | FINALIZED:
                return "F";
            case INIT | FINALIZED | WARN:
                return "f";
            case ERROR:
                return "EO";
            case WARN | ERROR:
                return "Eo";
            case INIT | ERROR:
                return "EI";
            case INIT | WARN | ERROR:
                return "Ei";
            case INIT | RUNNING | ERROR:
                return "ER";
            case INIT | RUNNING | WARN | ERROR:
                return "Er";
            case INIT | RUNNING | BUSY | ERROR:
                return "EB";
            case INIT | RUNNING | BUSY | WARN | ERROR:
                return "Eb";
            case FINALIZED | ERROR:
                return "ED";
            case FINALIZED | WARN | ERROR:
                return "Ed";
            case INIT | FINALIZED | ERROR:
                return "EF";
            case INIT | FINALIZED | WARN | ERROR:
                return "Ef";
            default:
                LOGGER.error("No Such State [{}]", state);
                return "NSS";
        }
    }

    public int priority() {
        int basicState = state & (OPEN | INIT | RUNNING | BUSY | FINALIZED | WARN | ERROR);
        switch (basicState) {
            case OPEN | INIT | RUNNING:
                return 1 << 13;
            case OPEN | INIT | RUNNING | WARN:
                return 1 << 12;
            case OPEN | INIT | RUNNING | BUSY:
                return 1 << 11;
            case OPEN | INIT | RUNNING | BUSY | WARN:
                return 1 << 10;
            case OPEN | INIT:
                return 1 << 9;
            case OPEN | INIT | WARN:
                return 1 << 8;
            case OPEN:
                return 1 << 7;
            case OPEN | WARN:
                return 1 << 6;
            case NEW:
                return 1 << 5;
            case NEW | WARN:
                return 1 << 4;
            case INIT | FINALIZED:
                return 1 << 3;
            case INIT | FINALIZED | WARN:
                return 1 << 2;
            case FINALIZED:
                return 1 << 1;
            case FINALIZED | WARN:
                return 1 << 0;
            case ERROR:
                return 0;
            case WARN | ERROR:
                return -1 << 0;
            case INIT | ERROR:
                return -1 << 1;
            case INIT | WARN | ERROR:
                return -1 << 2;
            case INIT | RUNNING | ERROR:
                return -1 << 3;
            case INIT | RUNNING | WARN | ERROR:
                return -1 << 4;
            case INIT | RUNNING | BUSY | ERROR:
                return -1 << 5;
            case INIT | RUNNING | BUSY | WARN | ERROR:
                return -1 << 6;
            case FINALIZED | ERROR:
                return -1 << 7;
            case FINALIZED | WARN | ERROR:
                return -1 << 8;
            case INIT | FINALIZED | ERROR:
                return -1 << 9;
            case INIT | FINALIZED | WARN | ERROR:
                return -1 << 10;
            default:
                LOGGER.error("No Such State [{}]", state);
                return -1 << 11;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State oState = (State) o;

        return state == oState.state;
    }

    @Override
    public int compareTo(State o) {
        int oPriority = -1 << 12;
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