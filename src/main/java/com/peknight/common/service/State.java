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

import com.peknight.common.enums.IntegerEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntBinaryOperator;

/**
 * 服务运行状态，分为 开启/未开启 初始化/未初始化 运行/未运行 繁忙/空闲 销毁/未销毁 警告/良好 异常/正常 七个状态
 * 其中异常/正常状态可以出现在服务运行的任意阶段，如果状态为异常，那么状态一定是被关闭（未开启）的
 * 只有服务处于开启状态且已初始化，才可能为运行状态
 * 只有服务处于运行状态，才可能为繁忙状态
 * 如果服务处于销毁状态，那么一定不会处于开启/运行/繁忙状态
 * 服务可以未初始化就被销毁
 * 警告/良好 状态可以在任意状态下设置，不会影响其他状态
 *
 * 本类大量使用原子类型进行原子操作以保证线程安全。
 *
 * IntBinaryOperator类型对象中的left参数表示State的当前状态值、right参数实际上为一个boolean：0 - false, 1 - true;
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

    protected final AtomicInteger state = new AtomicInteger(NEW);

    /**
     * 同步真实状态
     * 扩展本类时，可以重写此方法
     *
     * @return 最新的状态值
     */
    protected int syncState() {
        return state.get();
    }

    public boolean isOpen() {
        return (syncState() & OPEN) == OPEN;
    }

    protected static final IntBinaryOperator SET_OPEN_FUNCTION = (left, right) -> {
        if ((left & (INIT | RUNNING | BUSY | FINALIZED | ERROR)) != 0) {
            return left;
        } else {
            return right != 0 ? left | OPEN : (left | ERROR) & ~OPEN;
        }
    };

    public boolean setOpen(boolean isOpen) {
        syncState();
        int stateValue = state.accumulateAndGet(isOpen ? 1 : 0, SET_OPEN_FUNCTION);
        if (isOpen) {
            if ((stateValue & OPEN) == OPEN) {
                return true;
            } else {
                LOGGER.warn("[{}] Can Not Open", info());
                return false;
            }
        } else {
            return (stateValue & OPEN) != OPEN;
        }
    }

    public boolean isInit() {
        int stateValue = syncState();
        return (stateValue & ERROR) != ERROR && (stateValue & INIT) == INIT;
    }

    protected static final IntBinaryOperator SET_INIT_FUNCTION = (left, right) -> {
        if ((left & (RUNNING | BUSY | FINALIZED | ERROR)) != 0 ) {
            return left;
        } else {
            return right != 0 ? left | (OPEN | INIT) : (left | (INIT | ERROR)) & ~OPEN;
        }
    };

    public boolean setInit(boolean isInit) {
        syncState();
        int stateValue = state.accumulateAndGet(isInit ? 1 : 0, SET_INIT_FUNCTION);
        if (isInit) {
            if ((stateValue & ERROR) != ERROR && (stateValue & INIT) == INIT) {
                return true;
            } else {
                LOGGER.warn("[{}] Can Not Init", info());
                return false;
            }
        } else {
            return (stateValue & ERROR) == ERROR || (stateValue & INIT) != INIT;
        }
    }

    public boolean isRunning() {
        int stateValue = syncState();
        return (stateValue & ERROR) != ERROR && (stateValue & RUNNING) == RUNNING;
    }

    protected static final IntBinaryOperator SET_RUNNING_FUNCTION = (left, right) -> {
        if ((left & (FINALIZED | ERROR)) != 0) {
            return left;
        } else {
            return right != 0 ? left | (OPEN | INIT | RUNNING) : (left | (OPEN | INIT)) & (~RUNNING & ~BUSY);
        }
    };

    public boolean setRunning(boolean isRunning) {
        syncState();
        int stateValue = state.accumulateAndGet(isRunning ? 1 : 0, SET_RUNNING_FUNCTION);
        if (isRunning) {
            if ((stateValue & ERROR) != ERROR && (stateValue & RUNNING) == RUNNING) {
                return true;
            } else {
                LOGGER.warn("[{}] Can Not Set Running", info());
                return false;
            }
        } else {
            return (stateValue & ERROR) == ERROR || (stateValue & RUNNING) != RUNNING;
        }
    }

    public boolean isBusy() {
        int stateValue = syncState();
        return (stateValue & ERROR) != ERROR && (stateValue & BUSY) == BUSY;
    }

    protected static final IntBinaryOperator SET_BUSY_FUNCTION = (left, right) -> {
        if ((left & (FINALIZED | ERROR)) != 0) {
            return left;
        } else {
            return right != 0 ? left | (OPEN | INIT | RUNNING | BUSY) : (left | (OPEN | INIT | RUNNING)) & ~BUSY;
        }
    };

    public boolean setBusy(boolean isBusy) {
        syncState();
        int stateValue = state.accumulateAndGet(isBusy ? 1 : 0, SET_BUSY_FUNCTION);
        if (isBusy) {
            if ((stateValue & ERROR) != ERROR && (stateValue & BUSY) == BUSY) {
                return true;
            } else {
                LOGGER.warn("[{}] Can Not Set Busy", info());
                return false;
            }
        } else {
            return (stateValue & ERROR) == ERROR || (stateValue & BUSY) != BUSY;
        }
    }

    public boolean isFinalized() {
        int stateValue = syncState();
        return (stateValue & ERROR) != ERROR && (stateValue & FINALIZED) == FINALIZED;
    }

    protected static final IntBinaryOperator SET_FINALIZED_FUNCTION = (left, right) -> {
        if ((left & OPEN) != OPEN) {
            return left;
        } else {
            return right != 0 ? (left & (~OPEN & ~RUNNING & ~BUSY)) | FINALIZED : (left & ( ~OPEN & ~RUNNING & ~BUSY)) | (FINALIZED | ERROR);
        }
    };

    public boolean setFinalized(boolean isFinalized) {
        syncState();
        int stateValue = state.accumulateAndGet(isFinalized ? 1 : 0, SET_FINALIZED_FUNCTION);
        if (isFinalized) {
            if ((stateValue & ERROR) != ERROR && (stateValue & FINALIZED) == FINALIZED) {
                return true;
            } else {
                LOGGER.warn("[{}] Not Opened", info());
                return false;
            }
        } else {
            return (stateValue & ERROR) == ERROR || (stateValue & FINALIZED) != FINALIZED;
        }
    }

    public boolean isWarn() {
        return (syncState() & WARN) == WARN;
    }

    protected static final IntBinaryOperator SET_WARN_FUNCTION = (left, right) -> right != 0 ? left | WARN : left & ~WARN;

    public boolean setWarn(boolean isWarn) {
        syncState();
        int stateValue = state.accumulateAndGet(isWarn ? 1 : 0, SET_WARN_FUNCTION);
        if (isWarn) {
            if ((stateValue & WARN) == WARN) {
                return true;
            } else {
                return false;
            }
        } else {
            return (stateValue & WARN) != WARN;
        }
    }

    public boolean isError() {
        return (syncState() & ERROR) == ERROR;
    }

    protected static final IntBinaryOperator SET_ERROR_FUNCTION = (left, right) -> right != 0 ? (left & ~OPEN) | ERROR : left & ~ERROR;

    public boolean setError(boolean isError) {
        syncState();
        int stateValue = state.accumulateAndGet(isError ? 1 : 0, SET_ERROR_FUNCTION);
        if (isError) {
            if ((stateValue & ERROR) == ERROR) {
                return true;
            } else {
                return false;
            }
        } else {
            return (stateValue & ERROR) != ERROR;
        }
    }

    public boolean refresh() {
        state.set(NEW);
        return true;
    }

    public String info() {
        return StateEnum.findByValue(syncState()).getInfo();
    }

    public String simpleInfo() {
        return StateEnum.findByValue(syncState()).getSimpleInfo();
    }

    public int priority() {
        return StateEnum.findByValue(syncState()).getPriority();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State oState = (State) o;

        return syncState() == oState.syncState();
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
        return syncState();
    }

    @Override
    public String toString() {
        return info();
    }

    private enum StateEnum implements IntegerEnum {
        NEW(State.NEW, 1 << 5, "NEW", "N"),
        NEW_WARN(State.NEW | State.WARN, 1 << 4, "new", "n"),
        OPEN(State.OPEN, 1 << 7, "OPEN", "O"),
        OPEN_WARN(State.OPEN | State.WARN, 1 << 6, "open", "o"),
        INIT(State.OPEN | State.INIT, 1 << 9, "INIT", "I"),
        INIT_WARN(State.OPEN | State.INIT | State.WARN, 1 << 8, "init", "i"),
        RUNNING(State.OPEN | State.INIT | State.RUNNING, 1 << 13, "RUNNING", "R"),
        RUNNING_WARN(State.OPEN | State.INIT | State.RUNNING | State.WARN, 1 << 12, "running", "r"),
        BUSY(State.OPEN | State.INIT | State.RUNNING | State.BUSY, 1 << 11, "BUSY", "B"),
        BUSY_WARN(State.OPEN | State.INIT | State.RUNNING | State.BUSY | State.WARN, 1 << 10, "busy", "b"),
        ABORTION(State.FINALIZED, 1 << 1, "ABORTION", "A"),
        ABORTION_WARN(State.FINALIZED | State.WARN, 1 << 0, "abortion", "a"),
        FINALIZED(State.INIT | State.FINALIZED, 1 << 3, "FINALIZED", "F"),
        FINALIZED_WARN(State.INIT | State.FINALIZED | State.WARN, 1 << 2, "finalized", "f"),
        ERROR_OPEN(State.ERROR, 0, "ERROR-OPEN", "EO"),
        ERROR_OPEN_WARN(State.WARN | State.ERROR, -1 << 0, "ERROR-open", "Eo"),
        ERROR_INIT(State.INIT | State.ERROR, -1 << 1, "ERROR-INIT", "EI"),
        ERROR_INIT_WARN(State.INIT | State.WARN | State.ERROR, -1 << 2, "ERROR-init", "Ei"),
        ERROR_RUNNING(State.INIT | State.RUNNING | State.ERROR, -1 << 3, "ERROR-RUNNING", "ER"),
        ERROR_RUNNING_WARN(State.INIT | State.RUNNING | State.WARN | State.ERROR, -1 << 4, "ERROR-running", "Er"),
        ERROR_BUSY(State.INIT | State.RUNNING | State.BUSY | State.ERROR, -1 << 5, "ERROR-BUSY", "EB"),
        ERROR_BUSY_WARN(State.INIT | State.RUNNING | State.BUSY | State.WARN | State.ERROR, -1 << 6, "ERROR-busy", "Eb"),
        ERROR_ABORTION(State.FINALIZED | State.ERROR, -1 << 7, "ERROR-ABORTION", "EA"),
        ERROR_ABORTION_WARN(State.FINALIZED | State.WARN | State.ERROR, -1 << 8, "ERROR-abortion", "Ea"),
        ERROR_FINALIZED(State.INIT | State.FINALIZED | State.ERROR, -1 << 9, "ERROR-FINALIZED", "EF"),
        ERROR_FINALIZED_WARN(State.INIT | State.FINALIZED | State.WARN | State.ERROR, -1 << 10, "ERROR-finalized", "Ef"),
        NO_SUCH_STATE(State.OPEN | State.INIT | State.RUNNING | State.BUSY | State.FINALIZED | State.WARN | State.ERROR,
                -1 << 11, "No Such State", "NSS");

        private final int state;
        private final int priority;
        private final String info;
        private final String simpleInfo;

        StateEnum(int state, int priority, String info, String simpleInfo) {
            this.state = state;
            this.priority = priority;
            this.info = info;
            this.simpleInfo = simpleInfo;
        }

        @Override
        public int getValue() {
            return state;
        }

        public int getState() {
            return state;
        }

        public int getPriority() {
            return priority;
        }

        public String getInfo() {
            return info;
        }

        public String getSimpleInfo() {
            return simpleInfo;
        }

        public static StateEnum findByValue(int state) {
            int basicState = state &
                    (State.OPEN | State.INIT | State.RUNNING | State.BUSY | State.FINALIZED | State.WARN | State.ERROR);
            switch (basicState) {
                case State.NEW:
                    return NEW;
                case State.NEW | State.WARN:
                    return NEW_WARN;
                case State.OPEN:
                    return OPEN;
                case State.OPEN | State.WARN:
                    return OPEN_WARN;
                case State.OPEN | State.INIT:
                    return INIT;
                case State.OPEN | State.INIT | State.WARN:
                    return INIT_WARN;
                case State.OPEN | State.INIT | State.RUNNING:
                    return RUNNING;
                case State.OPEN | State.INIT | State.RUNNING | State.WARN:
                    return RUNNING_WARN;
                case State.OPEN | State.INIT | State.RUNNING | State.BUSY:
                    return BUSY;
                case State.OPEN | State.INIT | State.RUNNING | State.BUSY | State.WARN:
                    return BUSY_WARN;
                case State.FINALIZED:
                    return ABORTION;
                case State.FINALIZED | State.WARN:
                    return ABORTION_WARN;
                case State.INIT | State.FINALIZED:
                    return FINALIZED;
                case State.INIT | State.FINALIZED | State.WARN:
                    return FINALIZED_WARN;
                case State.ERROR:
                    return ERROR_OPEN;
                case State.WARN | State.ERROR:
                    return ERROR_OPEN_WARN;
                case State.INIT | State.ERROR:
                    return ERROR_INIT;
                case State.INIT | State.WARN | State.ERROR:
                    return ERROR_INIT_WARN;
                case State.INIT | State.RUNNING | State.ERROR:
                    return ERROR_RUNNING;
                case State.INIT | State.RUNNING | State.WARN | State.ERROR:
                    return ERROR_RUNNING_WARN;
                case State.INIT | State.RUNNING | State.BUSY | State.ERROR:
                    return ERROR_BUSY;
                case State.INIT | State.RUNNING | State.BUSY | State.WARN | State.ERROR:
                    return ERROR_BUSY_WARN;
                case State.FINALIZED | State.ERROR:
                    return ERROR_ABORTION;
                case State.FINALIZED | State.WARN | State.ERROR:
                    return ERROR_ABORTION_WARN;
                case State.INIT | State.FINALIZED | State.ERROR:
                    return ERROR_FINALIZED;
                case State.INIT | State.FINALIZED | State.WARN | State.ERROR:
                    return ERROR_FINALIZED_WARN;
                default:
                    LOGGER.error("No Such State [{}]", basicState);
                    return NO_SUCH_STATE;
            }
        }
    }
}