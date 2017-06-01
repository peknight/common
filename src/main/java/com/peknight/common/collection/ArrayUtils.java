package com.peknight.common.collection;

import java.util.Arrays;

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
        Arrays.sort(array);
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
        Arrays.sort(array);
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
}
