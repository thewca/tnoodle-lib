package org.worldcubeassociation.tnoodle.puzzle;

import org.worldcubeassociation.tnoodle.scrambles.AlgorithmBuilder;
import org.worldcubeassociation.tnoodle.scrambles.InvalidMoveException;
import org.worldcubeassociation.tnoodle.scrambles.Puzzle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SquareOnePuzzleTest {
    @Test
    public void testMergingMode() throws InvalidMoveException {
        Puzzle sq1 = new SquareOnePuzzle();
        AlgorithmBuilder ab = new AlgorithmBuilder(sq1, AlgorithmBuilder.MergingMode.CANONICALIZE_MOVES);

        assertEquals(ab.getTotalCost(), 0);

        ab.appendMove("(1,0)");
        assertEquals(ab.getTotalCost(), 1);

        ab.appendMove("(2,0)");
        assertEquals(ab.getTotalCost(), 1);

        ab.appendMove("(0,-1)");
        assertEquals(ab.getTotalCost(), 1);

        ab.appendMove("/");
        assertEquals(ab.getTotalCost(), 2);

        ab.appendMove("/");
        assertEquals(ab.getTotalCost(), 1);

        Puzzle.PuzzleState state = ab.getState();

        String solution = state.solveIn(1);
        assertEquals(solution, "(-3,1)");

        solution = state.solveIn(2);
        assertEquals(solution, "(-3,1)");
    }

    @Test
    public void testSlashabilitySolutions() throws InvalidMoveException {
        Puzzle sq1 = new SquareOnePuzzle();

        // slashability is (-1,0) which then cancels into (-3,0)
        String cancelsWithSlashability = "(3,0) / (4,0)";

        String solution = solveScrambleStringIn(sq1, cancelsWithSlashability, 3);
        assertNotNull(solution);

        // slashability is (-1, 0) which trivially doesn't cancel the / move
        String doesntCancelSlashability = "(3,0) / (1,0)";

        solution = solveScrambleStringIn(sq1, doesntCancelSlashability, 3);
        assertNotNull(solution);
    }

    private String solveScrambleStringIn(Puzzle puzzle, String scramble, int n) throws InvalidMoveException {
        AlgorithmBuilder ab = new AlgorithmBuilder(puzzle, AlgorithmBuilder.MergingMode.CANONICALIZE_MOVES);
        ab.appendAlgorithm(scramble);

        return ab.getState().solveIn(n);
    }
}
