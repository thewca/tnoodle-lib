package org.worldcubeassociation.tnoodle.scrambleanalysis.utils;

import java.util.Arrays;

public class StringUtils {
    /**
     * Give two string, compares them ignoring order of chars.
     * UFR == FRU == FRU
     *
     * @param st1 The first string
     * @param st2 The second string
     * @return The comparison result
     */
    public static boolean stringCompareIgnoringOrder(String st1, String st2) {
        if (st1.length() != st2.length()) {
            return false;
        }

        char[] chars1 = st1.toCharArray();
        char[] chars2 = st2.toCharArray();

        Arrays.sort(chars1);
        Arrays.sort(chars2);

        return Arrays.equals(chars1, chars2);
    }

}
