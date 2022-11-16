package org.worldcubeassociation.tnoodle.scrambleanalysis;

import org.junit.jupiter.api.Test;
import org.worldcubeassociation.tnoodle.puzzle.CubePuzzle;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CubeTestTest {
    private final CubePuzzle randomMoveThreeByThree = new CubePuzzle(3);

    @Test
    public void test() throws Exception {
        // NOTE: There is a very, very slim chance that the random move scrambles
        // may "accidentally" be as good as random state scrambles, making this test fail.
        // When this happens, we should pause and ponder about the qualities of our
        // random state solver, rather than simply ignoring a false-positive test.
        int N = 20000;

        List<String> scrambles = randomMovesScrambles(N);
        List<CubePuzzle.CubeState> representations = ScrambleProvider.convertToCubeStates(scrambles);

        assertFalse(CubeTest.testScrambles(representations));
    }

    private List<String> randomMovesScrambles(int N) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            result.add(randomMovesScramble());
        }
        return result;
    }

    private String randomMovesScramble() {
        return randomMoveThreeByThree.generateScramble();
    }
}
