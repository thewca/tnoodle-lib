package org.worldcubeassociation.tnoodle.scrambleanalysis;

import static org.worldcubeassociation.tnoodle.scrambleanalysis.CubeHelper.cornerOrientationSum;
import static org.worldcubeassociation.tnoodle.scrambleanalysis.CubeHelper.countMisorientedEdges;
import static org.worldcubeassociation.tnoodle.scrambleanalysis.CubeHelper.getFinalPositionOfCorner;
import static org.worldcubeassociation.tnoodle.scrambleanalysis.CubeHelper.getFinalPositionOfEdge;
import static org.worldcubeassociation.tnoodle.scrambleanalysis.CubeHelper.hasParity;
import static org.worldcubeassociation.tnoodle.scrambleanalysis.statistics.Distribution.expectedCornersFinalPosition;
import static org.worldcubeassociation.tnoodle.scrambleanalysis.statistics.Distribution.expectedCornersOrientationProbability;
import static org.worldcubeassociation.tnoodle.scrambleanalysis.statistics.Distribution.expectedEdgesFinalPosition;
import static org.worldcubeassociation.tnoodle.scrambleanalysis.statistics.Distribution.expectedEdgesOrientationProbability;

import java.util.List;

import org.apache.commons.math3.stat.inference.AlternativeHypothesis;
import org.apache.commons.math3.stat.inference.BinomialTest;
import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.worldcubeassociation.tnoodle.puzzle.CubePuzzle;
import org.worldcubeassociation.tnoodle.scrambleanalysis.statistics.Distribution;

public class CubeTest {

    private static final int edges = 12;
    private static final int corners = 8;

    public static boolean testScrambles(List<CubePuzzle.CubeState> scrambles)
        throws RepresentationException {

        int N = scrambles.size();

        long minimumSampleSize = Distribution.minimumSampleSize();
        if (N < Distribution.minimumSampleSize()) {
            throw new IllegalArgumentException("Minimum sample size is " + minimumSampleSize);
        }

        long[] misorientedEdgesList = new long[7];
        long[][] finalEdgesPosition = new long[edges][edges];

        long[] misorientedCornersList = new long[6]; // Sum is 0, 3, 6, ..., 15.
        long[][] finalCornersPosition = new long[corners][corners];

        int parity = 0;

        for (CubePuzzle.CubeState cubeState : scrambles) {
            String representation = cubeState.toFaceCube();

            int misorientedEdges = countMisorientedEdges(representation);
            int cornerSum = cornerOrientationSum(representation);

            misorientedEdgesList[misorientedEdges / 2]++;
            misorientedCornersList[cornerSum / 3]++;

            for (int j = 0; j < edges; j++) {
                int finalPosition = getFinalPositionOfEdge(representation, j);
                finalEdgesPosition[j][finalPosition]++;
            }

            for (int j = 0; j < corners; j++) {
                int finalPosition = getFinalPositionOfCorner(representation, j);
                finalCornersPosition[j][finalPosition]++;
            }

            if (hasParity(representation)) {
                parity++;
            }
        }

        ChiSquareTest cst = new ChiSquareTest();
        double alpha = 0.01;

        double[] expectedEdges = expectedEdgesOrientationProbability();
        boolean randomEO = !cst.chiSquareTest(expectedEdges, misorientedEdgesList, alpha);
        System.out.println("Random EO? " + randomEO);

        boolean edgesRandomPosition = true;
        long[] expectedEdgesFinalPosition = expectedEdgesFinalPosition(N);
        for (long[] item : finalEdgesPosition) {
            if (cst.chiSquareTestDataSetsComparison(expectedEdgesFinalPosition, item, alpha)) {
                edgesRandomPosition = false;
                break;
            }
        }
        System.out.println("Edges in random position? " + edgesRandomPosition);

        double[] expectedCorners = expectedCornersOrientationProbability();
        boolean randomCO = !cst.chiSquareTest(expectedCorners, misorientedCornersList, alpha);
        System.out.println("Random CO? " + randomCO);

        boolean cornersRandomPosition = true;
        long[] expectedCornersFinalPosition = expectedCornersFinalPosition(N);
        for (long[] item : finalCornersPosition) {
            if (cst.chiSquareTestDataSetsComparison(expectedCornersFinalPosition, item, alpha)) {
                cornersRandomPosition = false;
                break;
            }
        }
        System.out.println("Corners in random position? " + cornersRandomPosition);

        BinomialTest bt = new BinomialTest();
        double probability = 0.5;
        boolean randomParity = !bt.binomialTest(N, parity, probability, AlternativeHypothesis.TWO_SIDED, alpha);
        System.out.println("Random parity? " + randomParity);

        return randomEO && edgesRandomPosition && randomCO && cornersRandomPosition && randomParity;
    }

}
