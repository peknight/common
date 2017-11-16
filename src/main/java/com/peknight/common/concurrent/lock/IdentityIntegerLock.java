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
package com.peknight.common.concurrent.lock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/11/3.
 */
public class IdentityIntegerLock {
    private final ConcurrentHashMap<Integer, ReentrantLock> LOCK_MAP = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer, Semaphore> ACCESS_MAP = new ConcurrentHashMap<>();

    private boolean fair = false;

    public IdentityIntegerLock() {}

    public IdentityIntegerLock(boolean fair) {
        this.fair = fair;
    }

    public void lock(int id) {
        LOCK_MAP.putIfAbsent(id, new ReentrantLock(fair));
        LOCK_MAP.get(id).lock();
    }

    public void unlock(int id) {
        ReentrantLock lock = LOCK_MAP.get(id);
        if (lock != null) {
            lock.unlock();
        }
    }

    public boolean isLocked(int id) {
        ReentrantLock lock = LOCK_MAP.get(id);
        if (lock == null) {
            return false;
        } else {
            return lock.isLocked();
        }
    }

    public void acquire(int id) {
        ACCESS_MAP.putIfAbsent(id, new Semaphore(1, fair));
        Semaphore semaphore = ACCESS_MAP.get(id);
        semaphore.acquireUninterruptibly();
        semaphore.drainPermits();
    }

    public void release(int id) {
        ACCESS_MAP.putIfAbsent(id, new Semaphore(0, fair));
        ACCESS_MAP.get(id).release();
    }

    public boolean isAvailable(int id) {
        Semaphore semaphore = ACCESS_MAP.get(id);
        if (semaphore == null) {
            return true;
        } else {
            return semaphore.availablePermits() > 0;
        }
    }

    public void remove(int id) {
        LOCK_MAP.remove(id);
        ACCESS_MAP.remove(id);
    }
}
