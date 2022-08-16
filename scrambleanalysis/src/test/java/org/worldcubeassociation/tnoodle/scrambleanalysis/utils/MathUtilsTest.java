package org.worldcubeassociation.tnoodle.scrambleanalysis.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class MathUtilsTest {

    @Test
    public void test() {
        assertEquals(MathUtils.nCp(8, 2), 28);
    }

}
