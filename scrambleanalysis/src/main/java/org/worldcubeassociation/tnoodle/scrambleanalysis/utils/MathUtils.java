package org.worldcubeassociation.tnoodle.scrambleanalysis.utils;

import org.apache.commons.math3.util.CombinatoricsUtils;

public class MathUtils {

    public static long nCp(int n, int p) {
        return CombinatoricsUtils.binomialCoefficient(n, p);
    }
}
