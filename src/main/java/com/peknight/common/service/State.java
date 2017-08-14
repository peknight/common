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
 *
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/8/11.
 */
public class State {

    private static final Logger LOGGER = LoggerFactory.getLogger(State.class);

    public static final byte NEW = 0;

    public static final byte OPEN = 1 << 0;

    public static final byte INIT = 1 << 1;

    public static final byte RUNNING = 1 << 2;

    public static final byte BUSY = 1 << 3;

    public static final byte FINALIZE = 1 << 4;

    public static final byte ERROR = 1 << 5;

    private byte state;

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

    public void setOpen(boolean isOpen) {
        if ((state & (INIT | RUNNING | BUSY | FINALIZE | ERROR)) != 0) {
            LOGGER.warn("Can Not Open");
        } else {
            state = (byte) (isOpen ? OPEN : ERROR);
        }
    }

    public void setInit(boolean isInit) {
        if ((state & (RUNNING | BUSY | FINALIZE | ERROR)) != 0 ) {
            LOGGER.warn("Can Not Init");
        } else {
            state = (byte) (isInit ? OPEN | INIT : INIT | ERROR);
        }
    }

    public void setRunning(boolean isRunning) {
        if ((state & (BUSY | FINALIZE | ERROR)) != 0) {
            LOGGER.warn("Can Not Set Running");
        } else {
            state = (byte) (isRunning ? OPEN | INIT | RUNNING : OPEN | INIT);
        }
    }

    public void setBusy(boolean isBusy) {
        if ((state & (FINALIZE | ERROR)) != 0) {
            LOGGER.warn("Can Not Set Busy");
        } else {
            state = (byte) (isBusy ? OPEN | INIT | RUNNING | BUSY : OPEN | INIT | RUNNING);
        }
    }

    public void setFinalize(boolean isFinalize) {
        if ((state & OPEN) != OPEN) {
            LOGGER.warn("Not Opened");
        } else {
            state = (byte) (isFinalize ? state & ~OPEN & ~RUNNING & ~BUSY | FINALIZE : state & ~OPEN & ~RUNNING & ~BUSY | FINALIZE | ERROR);
        }
    }

    public void setError(boolean isError) {
        state = (byte) (isError ? state & ~OPEN | ERROR : state & ~ERROR);
    }

    public void refresh() {
        state = NEW;
    }

    public String info() {
        if ((state & (ERROR | BUSY)) == (ERROR | BUSY)) {
            return "ERROR WHEN [BUSY]";
        } else if ((state & (ERROR | RUNNING)) == (ERROR | RUNNING)) {
            return "ERROR WHEN [RUNNING]";
        } else if ((state & (ERROR | FINALIZE)) == (ERROR | FINALIZE)) {
            return "ERROR WHEN [FINALIZE]";
        } else if (state == (ERROR | INIT)) {
            return "ERROR WHEN [INIT]";
        } else if (state == ERROR) {
            return "ERROR WHEN [OPEN]";
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

    @Override
    public int hashCode() {
        return (int) state;
    }

    @Override
    public String toString() {
        return info();
    }
}
