package org.worldcubeassociation.tnoodle.puzzle;

import java.util.Random;

public class SkewbSolver {

    private static final int N_MOVES = 4;

    private static final int[] fact = { 1, 1, 1, 3, 12, 60, 360 };//fact[x] = x!/2
    private static final int TWIST_ORIENTATIONS = 2187; // 3^7, orientation of last corner is implicitly defined
    public static final int FREE_CORNER_PERM = fact[4];
    private static final int CENTER_PERM = fact[6];
    private static final int SKEWB_PERMUTATIONS = FREE_CORNER_PERM * CENTER_PERM;
    private static final char[][] permmv = new char[SKEWB_PERMUTATIONS][N_MOVES];
    private static final char[][] twstmv = new char[TWIST_ORIENTATIONS][N_MOVES];
    private static final byte[] permprun = new byte[SKEWB_PERMUTATIONS];
    private static final byte[] twstprun = new byte[TWIST_ORIENTATIONS];

    private static final int MAX_SOLUTION_LENGTH = 12;

    public SkewbSolver() {}

    private static final byte[][] cornerpermmv = new byte[][] {
        { 6, 5, 10, 1 }, { 9, 7, 4, 2 }, { 3, 11, 8, 0 }, { 10, 1, 6, 5 },
        { 0, 8, 11, 3 }, { 7, 9, 2, 4 }, { 4, 2, 9, 7 }, { 11, 3, 0, 8 },
        { 1, 10, 5, 6 }, { 8, 0, 3, 11 }, { 2, 4, 7, 9 }, { 5, 6, 1, 10 } };

    private static final byte[] ori = new byte[] { 0, 1, 2, 0, 2, 1, 1, 2, 0,
        2, 1, 0 };

    private static int getpermmv(int idx, int move) {
        int centerindex = idx / FREE_CORNER_PERM;
        int cornerindex = idx % FREE_CORNER_PERM;
        int val = 0x543210;
        int parity = 0;
        int[] centerperm = new int[6];
        for (int i = 0; i < 5; i++) {
            int p = fact[5 - i];
            int v = centerindex / p;
            centerindex -= v * p;
            parity ^= v;
            v <<= 2;
            centerperm[i] = (val >> v) & 0xf;
            int m = (1 << v) - 1;
            val = (val & m) + ((val >> 4) & ~m);
        }
        if ((parity & 1) == 0) {
            centerperm[5] = val;
        } else {
            centerperm[5] = centerperm[4];
            centerperm[4] = val;
        }
        int t;
        if (move == 0) {
            t = centerperm[0];
            centerperm[0] = centerperm[1];
            centerperm[1] = centerperm[3];
            centerperm[3] = t;
        } else if (move == 1) {
            t = centerperm[0];
            centerperm[0] = centerperm[4];
            centerperm[4] = centerperm[2];
            centerperm[2] = t;
        } else if (move == 2) {
            t = centerperm[1];
            centerperm[1] = centerperm[2];
            centerperm[2] = centerperm[5];
            centerperm[5] = t;
        } else if (move == 3) {
            t = centerperm[3];
            centerperm[3] = centerperm[5];
            centerperm[5] = centerperm[4];
            centerperm[4] = t;
        }
        return packCenterPerm(centerperm) * FREE_CORNER_PERM + cornerpermmv[cornerindex][move];
    }

    private static int gettwstmv(int idx, int move) {
        int[] fixedtwst = new int[4];
        int[] twst = new int[4];
        for (int i = 0; i < 4; i++) {
            fixedtwst[i] = idx % 3;
            idx /= 3;
        }
        for (int i = 0; i < 3; i++) {
            twst[i] = idx % 3;
            idx /= 3;
        }
        twst[3] = (6 - twst[0] - twst[1] - twst[2]) % 3;
        fixedtwst[move] = (fixedtwst[move] + 1) % 3;
        int t;
        switch (move) {
            case 0:
                t = twst[0];
                twst[0] = twst[2] + 2;
                twst[2] = twst[1] + 2;
                twst[1] = t + 2;
                break;
            case 1:
                t = twst[0];
                twst[0] = twst[1] + 2;
                twst[1] = twst[3] + 2;
                twst[3] = t + 2;
                break;
            case 2:
                t = twst[0];
                twst[0] = twst[3] + 2;
                twst[3] = twst[2] + 2;
                twst[2] = t + 2;
                break;
            case 3:
                t = twst[1];
                twst[1] = twst[2] + 2;
                twst[2] = twst[3] + 2;
                twst[3] = t + 2;
                break;
            default:
        }
        return packCornerOrient(twst, fixedtwst);
    }
    static {
        init();
    }
    private static void init() {
        for (int i = 0; i < SKEWB_PERMUTATIONS; i++) {
            permprun[i] = -1;
            for (int j = 0; j < 4; j++) {
                permmv[i][j] = (char) getpermmv(i, j);
            }
        }
        for (int i = 0; i < TWIST_ORIENTATIONS; i++) {
            twstprun[i] = -1;
            for (int j = 0; j < 4; j++) {
                twstmv[i][j] = (char) gettwstmv(i, j);
            }
        }
        permprun[0] = 0;
        for (int l = 0; l < 6; l++) {
            for (int p = 0; p < SKEWB_PERMUTATIONS; p++) {
                if (permprun[p] == l) {
                    for (int m = 0; m < 4; m++) {
                        int q = p;
                        for (int c = 0; c < 2; c++) {
                            q = permmv[q][m];
                            if (permprun[q] == -1) {
                                permprun[q] = (byte) (l + 1);
                            }
                        }
                    }
                }
            }
        }
        twstprun[0] = 0;
        for (int l = 0; l < 6; l++) {
            for (int p = 0; p < TWIST_ORIENTATIONS; p++) {
                if (twstprun[p] == l) {
                    for (int m = 0; m < 4; m++) {
                        int q = p;
                        for (int c = 0; c < 2; c++) {
                            q = twstmv[q][m];
                            if (twstprun[q] == -1) {
                                twstprun[q] = (byte) (l + 1);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Converts the list of centers into a number representing the (even) permutation of the edges.
     * @param centers centers representation (permutation only)
     * @return        an integer between 0 and 360 representing the (even) permutation of 6 elements
     */
    public static int packCenterPerm(int[] centers) {
        int idx = 0;
        int val = 0x543210;
        // Stopping early (normally you would expect i < 5)
        //   because the sixth element is implicitly defined by the first five
        for (int i = 0; i < 4; i++) {
            int v = centers[i] << 2;
            idx = (6 - i) * idx + ((val >> v) & 0xf);
            val -= 0x111110 << v;
        }
        return idx;
    }

    /**
     * Converts the list of free corners into a number representing the (even) permutation of the corners.
     * @param freeCorners free corners representation (permutation only)
     * @return            an integer between 0 and 12 representing the (even) permutation of 4 elements
     */
    public static int packCornerPerm(int[] freeCorners) {
        int idx = 0;
        int val = 0x3210;
        // Stopping early (normally you would expect i < 3)
        //   because the sixth element is implicitly defined by the first five
        for (int i = 0; i < 2; i++) {
            int v = freeCorners[i] << 2;
            idx = (4 - i) * idx + ((val >> v) & 0xf);
            val -= 0x1110 << v;
        }
        return idx;
    }

    /**
     * Converts the list of corners into a number representing the orientation of the corners.
     * @param freeCorners    orientations for the free corners (the ones that actually swap places)
     * @param fixedCorners   orientations for the fixed corners (the ones that only have orientation in FCN)
     * @return               an integer between 0 and 2186 representing the orientation of 4 elements
     */
    public static int packCornerOrient(int[] freeCorners, int[] fixedCorners) {
        int idx = 0;
        for (int i = 2; i >= 0; i--) {
            idx = idx * 3 + freeCorners[i] % 3;
        }
        for (int i = 3; i >= 0; i--) {
            idx = idx * 3 + fixedCorners[i];
        }
        return idx;
    }

    protected int search(int depth, int perm, int twst, int maxl, int lm, int[] sol, Random randomizeMoves) {
        if (maxl == 0) {
            if (perm == 0 && twst == 0) {
                return depth;
            } else {
                return -1;
            }
        }
        if (permprun[perm] > maxl || twstprun[twst] > maxl) {
            return -1;
        }
        int randomOffset = randomizeMoves.nextInt(N_MOVES);
        for (int m = 0; m < N_MOVES; m++) {
            int randomMove = (m + randomOffset) % N_MOVES;
            if (randomMove != lm) {
                int p = perm;
                int s = twst;
                for (int a = 0; a < 2; a++) {
                    p = permmv[p][randomMove];
                    s = twstmv[s][randomMove];
                    int searchResult = search(depth + 1, p, s, maxl - 1, randomMove, sol, randomizeMoves);
                    if (searchResult != -1) {
                        sol[depth] = randomMove * 2 + a;
                        return searchResult;
                    }
                }
            }
        }
        return -1;
    }

    public static class SkewbSolverState {
        public int perm;
        public int twst;
        public boolean isSolvable() {
            return ori[perm % 12] == (twst + twst / 3 + twst / 9 + twst / 27) % 3;
        }
    }

    public SkewbSolverState randomState(Random r) {
        SkewbSolverState state = new SkewbSolverState();
        state.perm = r.nextInt(SKEWB_PERMUTATIONS);
        do {
            state.twst = r.nextInt(TWIST_ORIENTATIONS);
        } while (!state.isSolvable());
        return state;
    }

    public String solveIn(SkewbSolverState state, int length) {
        return solve(state, length, false, false);
    }

    public String generateExactly(SkewbSolverState state, int length) {
        return solve(state, length, true, true);
    }

    private String solve(SkewbSolverState state, int desiredLength, boolean exactLength, boolean inverse) {
        Random r = new Random();
        int[] sol = new int[MAX_SOLUTION_LENGTH];

        int solutionLength = -1;
        int length = exactLength ? desiredLength : 0;

        while (length <= desiredLength) {
            solutionLength = search(0, state.perm, state.twst, length, -1, sol, r);

            if (solutionLength != -1) {
                break;
            }

            length++;
        }

        if (solutionLength == -1) {
            return null;
        }

        if (solutionLength == 0) {
            return "";
        }

        String fcnSolution = getSolution(sol, solutionLength);

        if (inverse) {
            String[] moves = fcnSolution.split(" ");
            StringBuilder scramble = new StringBuilder(fcnSolution.length());

            for (int i = moves.length - 1; i >= 0; i--) {
                String move = moves[i];

                // Skewb can only ever have 120 or -120 degree turns,
                //   so this is an exhaustive either-or.
                if (move.endsWith("'")) {
                    scramble.append(move, 0, move.length() - 1);
                } else {
                    scramble.append(move).append("'");
                }

                scramble.append(" ");
            }

            return scramble.toString().trim();
        }

        return fcnSolution;
    }

    /**
     * The solver is written in jaap's notation. Now we're going to convert the result to FCN(fixed corner notation):
     * Step one, the puzzle is rotated by z2, which will bring "R L D B" (in jaap's notation) to "L R F U" (in FCN, F has not
     *     been defined, now we define it as the opposite corner of B)
     * Step two, convert F to B by rotation [F' B]. When an F found in the move sequence, it is replaced immediately by B and other 3 moves
     *     should be swapped. For example, if the next move is R, we should turn U instead. Because the R corner is at U after rotation.
     *     In another word, "F R" is converted to "B U". The correctness can be easily verified and the procedure is recursable.
     */
    private String getSolution(int[] sol, int solutionLength) {
        StringBuilder sb = new StringBuilder();
        String[] move2str = { "L", "R", "B", "U" };//RLDB (in jaap's notation) rotated by z2
        for (int i = 0; i < solutionLength; i++) {
            int axis = sol[i] >> 1;
            int pow = sol[i] & 1;
            if (axis == 2) {//step two.
                for (int p=0; p<=pow; p++) {
                    String temp = move2str[0];
                    move2str[0] = move2str[1];
                    move2str[1] = move2str[3];
                    move2str[3] = temp;
                }
            }
            sb.append(move2str[axis]).append((pow == 1) ? "'" : "");
            sb.append(" ");
        }
        return sb.toString().trim();
    }
}

