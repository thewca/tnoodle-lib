package org.worldcubeassociation.tnoodle.scrambleanalysis.statistics;

import java.util.Arrays;

import static org.worldcubeassociation.tnoodle.scrambleanalysis.utils.MathUtils.nCp;

public class Distribution {

    private static final int edges = 12;
    private static final int corners = 8;

    /**
     * This is the expected probability distribution for edge orientation
     * considering random state.
     *
     * @return An array whose size is 7. On the index 0, the chance of 0 pairs
     * oriented; on the index 1, the probability for 1 misoriented pair; on
     * the index 2, the probability for 2 misoriented pairs;
     */
    public static double[] expectedEdgesOrientationProbability() {

        long[] array = new long[7];

        long total = 0L;
        for (int i = 0; i < array.length; i++) {
            long binom = nCp(12, 2 * i);

            array[i] = binom;
            total += binom;
        }

        double[] expected = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            expected[i] = 1.0 * array[i] / total;
        }
        return expected;
    }

    /**
     * @param N number of trials
     * @return An array[12] in which all elements have the same value N/12.
     */
    public static long[] expectedEdgesFinalPosition(long N) {
        long[] array = new long[edges];
        Arrays.fill(array, N / edges);

        return array;
    }

    /**
     * This is the expected probability distribution for corner orientation
     * considering random state. We assign 0 for oriented corner, 1 for corners
     * twisted clockwise, 2 for counter clockwise. In a valid cube, the sum of the
     * orientation is a multiple of 3.
     *
     * @return An array whose size is 6. On the index 0, the probability of sum 0 in
     * corner orientation. On the index 1, the probability of sum 3 = 3 * 1.
     * On the index 2, the probability of sum 6 = 3 * 2.
     */
    public static double[] expectedCornersOrientationProbability() {
        long partial;
        long total = 0L;

        // Corners must sum 0, 3, 6, ..., 15
        long[] array = new long[6];

        for (int i = 0; i < array.length; i++) {
            partial = 0;
            int sum = 3 * i;
            for (int j = 0; j < corners; j++) {
                for (int k = 0; k < corners; k++) {
                    // if j + k > 8, then the second nCp will always be 0. Adds nothing to the sum.
                    if (j + k <= 8 && j * 2 + k * 1 == sum) {
                        partial += nCp(8, j) * nCp(8 - j, k);
                    }
                }
            }
            total += partial;
            array[i] = partial;
        }

        double[] expected = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            expected[i] = 1.0 * array[i] / total;
        }

        return expected;
    }

    /**
     * @param N number of trials
     * @return An array[8] in which all elements have the same value N/8.
     */
    public static long[] expectedCornersFinalPosition(long N) {
        long[] array = new long[corners];
        Arrays.fill(array, N / corners);

        return array;
    }

    /**
     * @return The minimum sample size for our tests.
     */
    public static long minimumSampleSize() {
        long min = 0;

        // Actually, this is fixed to 6144, but it's nice to have a way to know where
        // does this comes from.

        // Minimum number required so we have at least 3 expected result for edges or corners.
        // Some places say we must have at least 3 results.
        double[] expectedEdges = expectedEdgesOrientationProbability();
        for (double item : expectedEdges) {
            long number = Math.round(3.0 / item);
            min = Math.max(number, min);
        }

        // Minimum number required so we have at least 1 expected result for corners.
        double[] expectedCorners = expectedCornersOrientationProbability();
        for (double item : expectedCorners) {
            long number = Math.round(1.0 / item);
            min = Math.max(number, min);
        }

        return min;
    }
}
