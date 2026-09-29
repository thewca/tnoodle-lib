package levigibson.fto3phase;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class FtoCubieTest {

    protected static long masterSeed;
    protected static Random random;

    @BeforeAll
    public static void setupMasterSeed() {
        String customSeed = System.getProperty("test.seed");

        if (customSeed != null && !customSeed.isEmpty()) {
            masterSeed = Long.parseLong(customSeed);
        } else {
            masterSeed = new Random().nextLong();
        }

        System.out.println("--> Using Master Seed: " + masterSeed);
        random = new Random(masterSeed);
    }

    private static final int[] CW_MOVES = {
        FtoCubie.R, FtoCubie.L, FtoCubie.B, FtoCubie.D,
        FtoCubie.U, FtoCubie.F, FtoCubie.BR, FtoCubie.BL
    };
    private static final int[] CCW_MOVES = {
        FtoCubie.RP, FtoCubie.LP, FtoCubie.BP, FtoCubie.DP,
        FtoCubie.UP, FtoCubie.FP, FtoCubie.BRP, FtoCubie.BLP
    };
    private static final String[] NAMES = {
        "R", "L", "B", "D", "U", "F", "BR", "BL"
    };

    @Test
    void testAllMoves3ReturnToSolved() {
        for (int i = 0; i < CW_MOVES.length; i++) {
            FtoCubie cube = new FtoCubie();
            cube.turn(CW_MOVES[i]);
            cube.turn(CW_MOVES[i]);
            cube.turn(CW_MOVES[i]);
            assertEquals(new FtoCubie(), cube, NAMES[i] + "^3 should return to solved");
        }
    }

    @Test
    void testAllMovesFollowedByInverseReturnToSolved() {
        for (int i = 0; i < CW_MOVES.length; i++) {
            FtoCubie cube = new FtoCubie();
            cube.turn(CW_MOVES[i]);
            cube.turn(CCW_MOVES[i]);
            assertEquals(new FtoCubie(), cube, NAMES[i] + " followed by " + NAMES[i] + "' should return to solved");
        }
    }

    @Test
    void testEdgeIndex(){
        for (int i = 0; i < 10000; i++) {
            FtoCubie ftoCubie = new FtoCubie();
            for (int j = 0; j < 100; j++) {
                ftoCubie.turn(random.nextInt(16));
            }

            int idx = ftoCubie.packAllEdges();
            FtoCubie testCube = new FtoCubie();

            testCube.setAllEdges(idx);
            assertEquals(idx, testCube.packAllEdges());
            assertArrayEquals(ftoCubie.getEdges(), testCube.getEdges());
        }
    }

    @Test
    void testTriangleIndex(){
        for (int i = 0; i < 10000; i++) {
            FtoCubie ftoCubie = new FtoCubie();
            for (int j = 0; j < 100; j++) {
                ftoCubie.turn(random.nextInt(16));
            }

            int idx = ftoCubie.packAllTriangles(0);
            FtoCubie testCube = new FtoCubie();

            testCube.setAllTriangles(idx, 0);
            assertEquals(idx, testCube.packAllTriangles(0));
            assertArrayEquals(ftoCubie.getTrianglesUFBrBl(), testCube.getTrianglesUFBrBl());
        }
    }

    @Test
    void testCornerPermutationIndex(){
        for (int i = 0; i < 10000; i++) {
            FtoCubie ftoCubie = new FtoCubie();
            for (int j = 0; j < 100; j++) {
                ftoCubie.turn(random.nextInt(16));
            }

            int idx = ftoCubie.packAllCornerPermutation();
            FtoCubie testCube = new FtoCubie();

            testCube.setAllCornerPermutation(idx);
            assertEquals(idx, testCube.packAllCornerPermutation());
            assertArrayEquals(ftoCubie.getCornerPerm(), testCube.getCornerPerm());
        }
    }

    @Test
    void testCornerOrientationIndex(){
        for (int i = 0; i < 10000; i++) {
            FtoCubie ftoCubie = new FtoCubie();
            for (int j = 0; j < 100; j++) {
                ftoCubie.turn(random.nextInt(16));
            }

            int idx = ftoCubie.packAllCornerOrientation();
            FtoCubie testCube = new FtoCubie();

            testCube.setAllCornerOrientation(idx);
            assertEquals(idx, testCube.packAllCornerOrientation());
            assertArrayEquals(ftoCubie.getCornerOri(), testCube.getCornerOri());
        }
    }

    @Test
    void testG1EdgeIndex(){
        for (int i = 0; i < 100000; i++) {
            FtoCubie ftoCubie = new FtoCubie();
            for (int j = 0; j < 100; j++) {
                ftoCubie.turn(random.nextInt(16));
            }

            int idx = ftoCubie.g1PackEdges();
            FtoCubie testCube = new FtoCubie();

            testCube.g1SetEdges(idx);
            assertEquals(idx, testCube.g1PackEdges());
        }
    }

    @Test
    void testPhaseOneTriangleIndex(){
        for (int i = 0; i < 10000; i++) {
            FtoCubie ftoCubie = new FtoCubie();
            for (int j = 0; j < 100; j++) {
                ftoCubie.turn(random.nextInt(16));
            }

            int idx = ftoCubie.g1PackTriangles();
            FtoCubie testCube = new FtoCubie();

            testCube.g1SetTriangles(idx);
            assertEquals(idx, testCube.g1PackTriangles());
        }
    }

    @Test
    void testEveryMoveHasCorrectInverse() {
        for (int trial = 0; trial < 50; trial++) {
            FtoCubie state = new FtoCubie();
            Random r = new Random(trial);
            for (int i = 0; i < 50; i++) {
                state.turn(random.nextInt(16));
            }

            for (int move = 0; move < 16; move += 2) {
                int cw = move;
                int ccw = move + 1;

                // CW then CCW should return to original state
                FtoCubie result = new FtoCubie(state);
                result.turn(cw);
                result.turn(ccw);
                assertArrayEquals(state.getCornerPerm(), result.getCornerPerm(),
                    "trial " + trial + " move " + cw + "+" + ccw + ": cp mismatch");
                assertArrayEquals(state.getCornerOri(), result.getCornerOri(),
                    "trial " + trial + " move " + cw + "+" + ccw + ": co mismatch");
                assertArrayEquals(state.getEdges(), result.getEdges(),
                    "trial " + trial + " move " + cw + "+" + ccw + ": edges mismatch");
                assertArrayEquals(state.getTrianglesUFBrBl(), result.getTrianglesUFBrBl(),
                    "trial " + trial + " move " + cw + "+" + ccw + ": centers1 mismatch");
                assertArrayEquals(state.getTrianglesRLBD(), result.getTrianglesRLBD(),
                    "trial " + trial + " move " + cw + "+" + ccw + ": centers2 mismatch");

                // CCW then CW should also return to original state
                result = new FtoCubie(state);
                result.turn(ccw);
                result.turn(cw);
                assertArrayEquals(state.getCornerPerm(), result.getCornerPerm(),
                    "trial " + trial + " move " + ccw + "+" + cw + ": cp mismatch");
                assertArrayEquals(state.getCornerOri(), result.getCornerOri(),
                    "trial " + trial + " move " + ccw + "+" + cw + ": co mismatch");
                assertArrayEquals(state.getEdges(), result.getEdges(),
                    "trial " + trial + " move " + ccw + "+" + cw + ": edges mismatch");
                assertArrayEquals(state.getTrianglesUFBrBl(), result.getTrianglesUFBrBl(),
                    "trial " + trial + " move " + ccw + "+" + cw + ": centers1 mismatch");
                assertArrayEquals(state.getTrianglesRLBD(), result.getTrianglesRLBD(),
                    "trial " + trial + " move " + ccw + "+" + cw + ": centers2 mismatch");
            }
        }
    }

    @Test
    void testPhaseTwoTrisIndex(){
        int[] safeMoves = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}; // R,RP,L,LP,B,BP,D,DP,U,UP
        for (int i = 0; i < 10000; i++) {
            FtoCubie ftoCubie = new FtoCubie();
            for (int j = 0; j < 100; j++) {
                ftoCubie.turn(safeMoves[random.nextInt(safeMoves.length)]);
            }

            int idx = ftoCubie.g2PackTriangles();
            FtoCubie testCube = new FtoCubie();

            testCube.g2SetTriangles(idx);
            assertEquals(idx, testCube.g2PackTriangles());
            assertArrayEquals(ftoCubie.getTrianglesRLBD(), testCube.getTrianglesRLBD());
        }
    }

    @Test
    void testPhaseTwoEdgesIndex(){
        int[] safeMoves = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}; // R,RP,L,LP,B,BP,D,DP,U,UP
        for (int i = 0; i < 10000; i++) {
            FtoCubie ftoCubie = new FtoCubie();
            for (int j = 0; j < 100; j++) {
                ftoCubie.turn(safeMoves[random.nextInt(safeMoves.length)]);
            }

            int idx = ftoCubie.g2PackEdges();
            FtoCubie testCube = new FtoCubie();

            testCube.g2SetEdges(idx);
            assertEquals(idx, testCube.g2PackEdges());
        }
    }

    @Test
    void testPhaseTwoTriples(){
        int[] safeMoves = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}; // R,RP,L,LP,B,BP,D,DP,U,UP
        for (int i = 0; i < 10000; i++) {
            FtoCubie ftoCubie = new FtoCubie();
            for (int j = 0; j < 100; j++) {
                ftoCubie.turn(safeMoves[random.nextInt(safeMoves.length)]);
            }

            for (int color = 0; color < 4; color++) {
                int idx = ftoCubie.g2PackTriples(color);
                FtoCubie testCube = new FtoCubie();

                testCube.g2SetTriples(idx, color);
                assertEquals(idx, testCube.g2PackTriples(color));
            }

        }
    }

    @Test
    void testPhaseThreeCorners(){
        int[] safeMoves = Search.G3_MOVESET;
        for (int i = 0; i < 10000; i++) {
            FtoCubie ftoCubie = new FtoCubie();
            for (int j = 0; j < 100; j++) {
                ftoCubie.turn(safeMoves[random.nextInt(safeMoves.length)]);
            }

            int idx = ftoCubie.g3PackCorners();
            FtoCubie testCube = new FtoCubie();

            testCube.g3SetCorners(idx);
            assertEquals(idx, testCube.g3PackCorners());

        }
    }

    @Test
    void testSolvedStartingStates(){
        FtoCubie solvedG1 = Util.fromAlg("R U L D B U B L D B R L B D");
        FtoCubie solvedG2 = Util.fromAlg("R L D B U B L B R L B");
        FtoCubie solvedG3 = Util.fromAlg("");

        Search search = new Search();
        //solution() will panic if it doesn't work
        search.solution(solvedG1);
        search.solution(solvedG2);
        search.solution(solvedG3);
    }

    @Test
    void performanceTest(){
        int n = 100;

        long start = System.currentTimeMillis();
        int totalMoves = 0;

        Search search = new Search();
        for (int i = 0; i < n; i++) {
            FtoCubie rs = FtoCubie.randomCube(random);
            String solution = search.solution(rs);
            int sollen = solution.split(" ").length;
            System.out.print(solution);
            System.out.println("(" + sollen + ")");

            totalMoves += sollen;
        }

        System.out.println("Average Time per Scramble: " + ((System.currentTimeMillis() - start) / n) + "ms");
        System.out.println("Average Moves per Scramble: " + (float)(totalMoves) / (float)n);
    }
}
