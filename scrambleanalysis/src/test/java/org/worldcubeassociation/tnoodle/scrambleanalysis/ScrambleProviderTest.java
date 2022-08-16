package org.worldcubeassociation.tnoodle.scrambleanalysis;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.worldcubeassociation.tnoodle.puzzle.ThreeByThreeCubePuzzle;

public class ScrambleProviderTest {
    @Test
    public void test() {
        // Ew, not null tests.
        assertNotNull(ScrambleProvider.generateWcaScrambles(2));
    }
}
