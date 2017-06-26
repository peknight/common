/**
 * MIT License
 *
 * Copyright (c) 2017-2027 PeKnight(JKpeknight@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.peknight.common.collection;

import org.springframework.util.Assert;

import java.util.Comparator;

/**
 * 数组工具类
 *
 * Created by PeKnight on 2017/6/1.
 */
public final class ArrayUtils {
    private ArrayUtils() {}

    /**
     * 判断可排序数组中是否包含某个元素
     */
    public static boolean sortedContains(int key, int... array) {
        return sortedIndexOf(key, array) != -1;
    }

    /**
     * 返回元素在排序后的数组中的下标，如果数组中不含此元素，返回-1
     * 使用二分查找法
     */
    public static int sortedIndexOf(int key, int... array) {
        Assert.isTrue(isSorted(array), "Not Sorted!");
        int lo = 0;
        int hi = array.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if      (key < array[mid]) hi = mid - 1;
            else if (key > array[mid]) lo = mid + 1;
            else return mid;
        }
        return -1;
    }

    /**
     * 判断可排序数组中是否包含某个元素
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean sortedContains(Comparable<T> key, Comparable<T>... array) {
        return sortedIndexOf(key, array) != -1;
    }

    /**
     * 返回元素在排序后的数组中的下标，如果数组中不含此元素，返回-1
     * 使用二分查找法
     */
    @SuppressWarnings("unchecked")
    public static <T> int sortedIndexOf(Comparable<T> key, Comparable<T>... array) {
        Assert.isTrue(isSorted(array), "Not Sorted!");
        int lo = 0;
        int hi = array.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if      (key.compareTo((T) array[mid]) < 0) hi = mid - 1;
            else if (key.compareTo((T) array[mid]) > 0) lo = mid + 1;
            else return mid;
        }
        return -1;
    }

    /**
     * 判断数组中是否包含某个元素
     */
    public static boolean contains(int key, int... array) {
        return indexOf(key, array) != -1;
    }

    /**
     * 返回元素在数组中的下标，如果数组中不含此元素，返回-1
     */
    public static int indexOf(int key, int... array) {
        int length = array.length;
        for (int i = 0; i < length; i++) {
            if (key == array[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 判断数组中是否包含某个元素
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean contains(T key, T... array) {
        return indexOf(key, array) != -1;
    }

    /**
     * 返回元素在数组中的下标，如果数组中不含此元素，返回-1
     */
    @SuppressWarnings("unchecked")
    public static <T> int indexOf(T key, T... array) {
        int length = array.length;
        for (int i = 0; i < length; i++) {
            if (array[i].equals(key)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 创建subset数组中各个元素在superset数组中对应下标的数组
     */
    public static int[] getSubsetIndexs(int[] subset, int[] superset) {
        int[] indexs = new int[subset.length];
        for (int i = 0; i < subset.length; i++) {
            int index = indexOf(superset, subset[i]);
            if (index == -1) {
                throw new IllegalArgumentException("数据内容不匹配, 无法创建下标映射表");
            }
            indexs[i] = index;
        }
        return indexs;
    }

    /**
     * 将数组arr初始化
     */
    public static void initSerialArray(int[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
    }

    /**
     * 将数组arr中下标为start+1到end-1的数据向左移一位
     * 同时将下标为start的数据放在下标end-1处
     * @param array 待操作的数据
     * @param start 移位起始下标
     * @param end 移位终止下标（不包括）
     */
    public static void arrayLeftShift(int[] array, int start, int end) {
        int temp = array[start];
        for (int i = start; i < end-1; i++) {
            array[i] = array[i+1];
        }
        array[end-1] = temp;
    }

    /**
     * 将数组arr中下标为start到end-2的数据向右移一位
     * 同时将下标为end-1的数据放在下标start处
     * @param array 待操作的数据
     * @param start 移位起始下标
     * @param end 移位终止下标（不包括）
     */
    public static void arrayRightShift(int[] array, int start, int end) {
        int temp = array[end-1];
        for (int i = end-1; i > start; i--) {
            array[i] = array[i-1];
        }
        array[start] = temp;
    }

    /***************************************************************************
     *  Helper sorting functions.
     ***************************************************************************/

    // is v < w ?
    private static boolean less(Comparable v, Comparable w) {
        return v.compareTo(w) < 0;
    }

    // is v < w ?
    private static boolean less(Object v, Object w, Comparator comparator) {
        return comparator.compare(v, w) < 0;
    }

    // exchange a[i] and a[j]
    private static void exch(Object[] a, int i, int j) {
        Object swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

    // exchange a[i] and a[j]  (for indirect sort)
    private static void exch(int[] a, int i, int j) {
        int swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

    /***************************************************************************
     *  Check if array is sorted - useful for debugging.
     ***************************************************************************/
    private static boolean isSorted(Comparable[] a) {
        return isSorted(a, 0, a.length - 1);
    }

    // is the array sorted from a[lo] to a[hi]
    private static boolean isSorted(Comparable[] a, int lo, int hi) {
        for (int i = lo+1; i <= hi; i++)
            if (less(a[i], a[i-1])) return false;
        return true;
    }

    private static boolean isSorted(Object[] a, Comparator comparator) {
        return isSorted(a, 0, a.length - 1, comparator);
    }

    // is the array sorted from a[lo] to a[hi]
    private static boolean isSorted(Object[] a, int lo, int hi, Comparator comparator) {
        for (int i = lo + 1; i <= hi; i++)
            if (less(a[i], a[i-1], comparator)) return false;
        return true;
    }

    private static boolean isSorted(int[] a) {
        return isSorted(a, 0, a.length - 1);
    }

    // is the array sorted from a[lo] to a[hi]  (for indirect sort)
    private static boolean isSorted(int[] a, int lo, int hi) {
        for (int i = lo+1; i <= hi; i++)
            if (a[i] < a[i-1]) return false;
        return true;
    }
}
