package org.worldcubeassociation.tnoodle.puzzle;

import java.util.Random;

import org.worldcubeassociation.tnoodle.scrambles.AlgorithmBuilder;
import org.worldcubeassociation.tnoodle.scrambles.AlgorithmBuilder.MergingMode;
import org.worldcubeassociation.tnoodle.scrambles.InvalidMoveException;
import org.worldcubeassociation.tnoodle.scrambles.InvalidScrambleException;
import org.worldcubeassociation.tnoodle.scrambles.PuzzleStateAndGenerator;
import org.worldcubeassociation.tnoodle.puzzle.TwoByTwoSolver.TwoByTwoState;
import org.timepedia.exporter.client.Export;

@Export
public class TwoByTwoCubePuzzle extends CubePuzzle {
    private static final int TWO_BY_TWO_MIN_SCRAMBLE_LENGTH = 11;

    private TwoByTwoSolver twoSolver = null;
    public TwoByTwoCubePuzzle() {
        super(2);
        wcaMinScrambleDistance = 4;
        twoSolver = new TwoByTwoSolver();
    }

    @Override
    public PuzzleStateAndGenerator generateRandomMoves(Random r) {
        TwoByTwoState state = twoSolver.randomState(r);
        String scramble = twoSolver.generateExactly(state, TWO_BY_TWO_MIN_SCRAMBLE_LENGTH);
        assert scramble.split(" ").length == TWO_BY_TWO_MIN_SCRAMBLE_LENGTH;

        AlgorithmBuilder ab = new AlgorithmBuilder(this, MergingMode.CANONICALIZE_MOVES);
        try {
            ab.appendAlgorithm(scramble);
        } catch (InvalidMoveException e) {
            throw new RuntimeException(new InvalidScrambleException(scramble, e));
        }
        return ab.getStateAndGenerator();
    }

    protected String solveIn(PuzzleState ps, int n) {
        CubeState cs = (CubeState) ps;
        String solution = twoSolver.solveIn(cs.toTwoByTwoState(), n);
        return solution;
    }
}
