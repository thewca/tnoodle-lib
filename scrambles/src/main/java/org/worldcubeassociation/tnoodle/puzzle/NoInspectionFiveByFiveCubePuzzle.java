package org.worldcubeassociation.tnoodle.puzzle;

import java.util.Random;
import org.worldcubeassociation.tnoodle.scrambles.PuzzleStateAndGenerator;
import org.worldcubeassociation.tnoodle.scrambles.InvalidMoveException;
import org.worldcubeassociation.tnoodle.scrambles.AlgorithmBuilder;
import org.timepedia.exporter.client.Export;

@Export
public class NoInspectionFiveByFiveCubePuzzle extends CubePuzzle {
    public NoInspectionFiveByFiveCubePuzzle() {
        super(5);
    }

    @Override
    public PuzzleStateAndGenerator generateRandomMoves(Random r) {
        CubeMove[][] randomOrientationMoves = getRandomOrientationMoves(size /2);
        CubeMove[] randomOrientation = randomOrientationMoves[r.nextInt(randomOrientationMoves.length)];
        PuzzleStateAndGenerator psag = super.generateRandomMoves(r);
        psag = applyOrientation(this, randomOrientation, psag, true);
        return psag;
    }

    public static PuzzleStateAndGenerator applyOrientation(CubePuzzle puzzle, CubeMove[] randomOrientation, PuzzleStateAndGenerator psag, boolean discardRedundantMoves) {
        if(randomOrientation.length == 0) {
            // No reorientation required
            return psag;
        }

        // Append reorientation to scramble.
        try {
            AlgorithmBuilder ab = new AlgorithmBuilder(puzzle, AlgorithmBuilder.MergingMode.NO_MERGING);
            ab.appendAlgorithm(psag.generator);
            // Check if our reorientation is going to cancel with the last
            // turn of our scramble. If it does, then we just discard
            // that last turn of our scramble. This ensures we have a scramble
            // with no redundant turns, and I can't see how it could hurt the
            // quality of our scrambles to do this.
            String firstReorientMove = randomOrientation[0].toString();
            while(ab.isRedundant(firstReorientMove)) {
                assert discardRedundantMoves;
                AlgorithmBuilder.IndexAndMove im = ab.findBestIndexForMove(firstReorientMove, AlgorithmBuilder.MergingMode.CANONICALIZE_MOVES);
                ab.popMove(im.index);
            }
            for(CubeMove cm : randomOrientation) {
                ab.appendMove(cm.toString());
            }

            psag = ab.getStateAndGenerator();
            return psag;
        } catch(InvalidMoveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getShortName() {
        return "555ni";
    }

    @Override
    public String getLongName() {
        return "5x5x5 no inspection";
    }
}
