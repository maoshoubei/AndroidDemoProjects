package com.example.demoapp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListUtil {
    public static void clearList(List<?> list) {
        if (list != null) {
            list.clear();
        }
    }

    /**
     * Check if list is null or zero-size.
     * @param list
     * @return
     */
    public static boolean isEmpty(List<?> list) {
        if ((list == null) || (list.size() == 0)) {
            return true;
        }
        return false;
    }

    /**
     * Check if array is null or zero-length.
     * @param array
     * @return
     */
    public static <T> boolean isEmpty(T[] array) {
        if ((array == null) || (array.length == 0)) {
            return true;
        }
        return false;
    }

    /**
     * If list is not null, return list itself. Otherwise return a new ArrayList.
     * @param list
     * @return
     */
    public static <T> List<T> getSafeList(List<T> list) {
        if (list == null) {
            list = new ArrayList<T>();
        }

        return list;
    }

    public static <T extends Comparable<? super T>> void sort(List<T> list, boolean isAscending) {
        Collections.sort(list);
        if (!isAscending) {
            Collections.reverse(list);
        }
    }

    /**
     * Convert an array to List.
     * @param array
     * @return
     */
    public static <T> List<T> toList(T[] array) {
        return Arrays.asList(array);
    }

    /**
     * Convert an array to List.
     * 
     * @param list source
     * @param target An empty array to put the elements. Can not be null.
     * @return An array with elements from list
     */
    public static <T> T[] toArray(List<T> list, T[] target) {
        return list.toArray(target);
    }


    public static boolean isInArray(String[] arr, String targetValue) {
        Set<String> set = new HashSet<>(Arrays.asList(arr));
        return set.contains(targetValue);
    }

    public static String listToString(List<String> list) {
        if (list == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (String string : list) {
            if (first) {
                first = false;
            } else {
                result.append(",");
            }
            result.append(string);
        }
        return result.toString();

    }

//    深度复制list
    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked") List<T> dest = (List<T>) in.readObject();
        return dest;
    }

}
