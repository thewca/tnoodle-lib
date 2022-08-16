package org.worldcubeassociation.tnoodle.scrambleanalysis.statistics;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class DistributionTest {
    @Test
    public void minimumSampleSizeTest() {
        assertTrue(Distribution.minimumSampleSize() > 0);
        assertEquals(Distribution.minimumSampleSize(), 6144);
    }
}
