package levigibson.fto3phase;

import java.util.*;
import java.util.stream.IntStream;

import static levigibson.fto3phase.FtoCoord.*;

public class Search {

    /**
     * Main search function! This search algorithm will produce a human-readable string that
     * when applied to a solved FTO, will match the FTO provided.
     * @param randomState random state to solve to
     * @return human-readable scramble
     */
    public synchronized String solution(FtoCubie randomState){
        if (!FtoCoord.getInitialized())
            FtoCoord.init();

        ArrayList<int[]> g1Candidates = g1Iterate(randomState);

        int[] g1AndG2Solution = g2Iterate(randomState, g1Candidates);

        FtoCubie g2Fto = new FtoCubie();
        randomState.applyMovesInto(g1AndG2Solution, g2Fto);

        int[] g3Solution = g3Iterate(g2Fto);

        int[] fullSolution = IntStream.concat(Arrays.stream(g1AndG2Solution), Arrays.stream(g3Solution)).toArray();
        String fullSolutionStr = Util.moveArrayToInvertedString(fullSolution);

        if (!validateSolution(fullSolutionStr, randomState)){
            throw new RuntimeException("CRITICAL: Found solution does not match random state");
        }

        return fullSolutionStr;
    }

    public boolean validateSolution(String moves, FtoCubie randomState){
        FtoCubie test = Util.fromAlg(moves);
        return (randomState.equals(test));
    }

    //After each phase, the moveset required to solve the FTO is reduced
    //See Kociemba's Algorithm
    public static final int[] G1_MOVESET = {FtoCubie.R, FtoCubie.RP,
        FtoCubie.L, FtoCubie.LP, FtoCubie.B, FtoCubie.BP,
        FtoCubie.U, FtoCubie.UP, FtoCubie.D, FtoCubie.DP,
        FtoCubie.F, FtoCubie.FP, FtoCubie.BR, FtoCubie.BRP,
        FtoCubie.BL, FtoCubie.BLP};
    public static final int[] G2_MOVESET = {FtoCubie.R, FtoCubie.RP,
        FtoCubie.L, FtoCubie.LP, FtoCubie.B, FtoCubie.BP,
        FtoCubie.U, FtoCubie.UP, FtoCubie.D, FtoCubie.DP};
    public static final int[] G3_MOVESET = {FtoCubie.R, FtoCubie.RP,
        FtoCubie.L, FtoCubie.LP, FtoCubie.B, FtoCubie.BP,FtoCubie.D, FtoCubie.DP};

    private static final int MIN_G1_CANDIDATES = 500;

    private ArrayList<int[]> g1Iterate(FtoCubie cubie){

        ArrayList<int[]> candidates = new ArrayList<>();
        ArrayDeque<Integer> moves = new ArrayDeque<>();

        int edge = cubie.g1PackEdges();
        int tri = cubie.g1PackTriangles();
        for (int depth = 0; depth < Integer.MAX_VALUE; depth++) {
            g1Search(depth, 0, edge, tri, candidates, moves);
            if (candidates.size() >= MIN_G1_CANDIDATES) break;
        }

        return candidates;
    }

    private int[] g2Iterate(FtoCubie cubie, ArrayList<int[]> candidates){
        int[][] states = buildStatesFromCandidates(cubie, candidates);
        int[] lengths = candidates.stream()
            .mapToInt(candidate -> candidate.length)
            .toArray();

        ArrayDeque<Integer> moves = new ArrayDeque<>();

        for (int depth = 0; depth < Integer.MAX_VALUE; depth++) {
            for (int c = 0; c < states.length; c++) {

                int searchDepth = depth - lengths[c];
                if (searchDepth < 0) continue;

                int[] s = states[c];

                int[] res = g2Search(searchDepth, 0, s[0], s[1], s[2], s[3], s[4], s[5], moves);
                if (res != null){
                    int[] g1sol = candidates.get(c);
                    int[] g2sol = res;

                    return IntStream.concat(Arrays.stream(g1sol), Arrays.stream(g2sol)).toArray();
                }
            }

        }

        throw new RuntimeException("Could not find Phase 2 solution");
    }

    private int[] g3Iterate(FtoCubie cubie){
        int edges = cubie.g3PackEdges();
        int corners = cubie.g3PackCorners();

        ArrayDeque<Integer> moves = new ArrayDeque<>();

        for (int depth = 0; depth < Integer.MAX_VALUE; depth++) {
            int[] res = g3Search(depth, 0, edges, corners, moves);
            if (res != null){
                return res;
            }
        }

        throw new RuntimeException();
    }

    private void g1Search(int depth, int maxl, int edge, int tri, ArrayList<int[]> candidates, ArrayDeque<Integer> moves){

        int prun = FtoCoord.g1Prun(edge, tri);

        if (depth < prun)
            return;

        if (prun == 0 && depth == 0){
            candidates.add(Arrays.copyOf(moves.stream().mapToInt(Integer::intValue).toArray(), maxl));
            return;
        }

        if (depth <= 0){
            return;
        }

        for (int move : G1_MOVESET) {

            if (maxl > 0 && !isValidMove(moves.getLast(), move)) {
                continue;
            }

            moves.addLast(move);

            g1Search(depth-1, maxl+1,
                FtoCoord.g1TurnEdges(edge, move), FtoCoord.g1TurnTriangles(tri, move), candidates, moves);

            moves.removeLast();
        }
    }

    private int[] g2Search(int depth, int maxl, int edge, int tri, int tp0, int tp1, int tp2, int tp3, ArrayDeque<Integer> moves){

        int triplePrun = FtoCoord.g2PrunTriple(tp0, tp1, tp2, tp3);
        if (depth < triplePrun)
            return null;

        int txEPrun = FtoCoord.g2PrunTxE(edge, tri);
        if (depth < txEPrun){
            return null;
        }

        if (triplePrun == 0 && txEPrun == 0){
            return moves.stream().mapToInt(Integer::intValue).toArray();
        }

        if (depth == 0){
            return null;
        }

        for (int move : G2_MOVESET) {
            if (maxl > 0 && !isValidMove(moves.getLast(), move)) {
                continue;
            }

            moves.addLast(move);

            int[] res = g2Search(depth-1, maxl+1,
                g2TurnEdges(edge, move),
                g2TurnTris(tri, move),
                g2TurnTriples(tp0, move),
                g2TurnTriples(tp1, move),
                g2TurnTriples(tp2, move),
                g2TurnTriples(tp3, move),
                moves
            );

            moves.removeLast();

            if (res != null)
                return res;
        }

        return null;
    }

    private int[] g3Search(int depth, int maxl, int edges, int corners, ArrayDeque<Integer> moves){
        int cornerPrun = FtoCoord.g3PrunCorners(corners);
        if (depth < cornerPrun) return null;

        if (corners == SOLVED_G3_CORNERS &&
            edges == SOLVED_G3_EDGES){

            return moves.stream().mapToInt(Integer::intValue).toArray();
        }

        if (depth == 0){
            return null;
        }

        for (int move : G3_MOVESET) {
            if (maxl > 0 && !isValidMove(moves.getLast(), move)) {
                continue;
            }

            moves.addLast(move);

            int[] res = g3Search(depth-1, maxl+1,
                g3TurnEdges(edges, move),
                g3TurnCorners(corners, move), moves);

            moves.removeLast();

            if (res != null)
                return res;
        }

        return null;
    }

    private static int[][] buildStatesFromCandidates(FtoCubie cubie, ArrayList<int[]> candidates){
        //6 = number of indices used to represent the g1->g2 fto
        int[][] states = new int[candidates.size()][6];
        FtoCubie candidateCubie = new FtoCubie();

        for (int i = 0; i < candidates.size(); i++) {
            int[] moves = candidates.get(i);

            cubie.applyMovesInto(moves, candidateCubie);

            states[i][0] = candidateCubie.g2PackEdges();
            states[i][1] = candidateCubie.g2PackTriangles();
            states[i][2] = candidateCubie.g2PackTriples(0);
            states[i][3] = candidateCubie.g2PackTriples(1);
            states[i][4] = candidateCubie.g2PackTriples(2);
            states[i][5] = candidateCubie.g2PackTriples(3);
        }

        return states;
    }

    private static boolean isValidMove(int lastMove, int move){
        int la = lastMove / 2;
        int ca = move / 2;
        return ((invalidMoves[la] >> ca) & 1) != 1;
    }

    private static final int[] invalidMoves;

    private static int[] initInvalidMoveTable() {
        int numAxes = 8;
        int[] invalid = new int[numAxes];

        for (int a1 = 0; a1 < numAxes; a1++) {
            invalid[a1] = 1 << a1;
            for (int a2 = 0; a2 < a1; a2++) {
                FtoCubie fto1 = new FtoCubie(); fto1.turn(a1 * 2); fto1.turn(a2 * 2);
                FtoCubie fto2 = new FtoCubie(); fto2.turn(a2 * 2); fto2.turn(a1 * 2);
                if (fto1.equals(fto2)) {
                    invalid[a1] |= 1 << a2;
                }
            }
        }
        return invalid;
    }

    static {
        invalidMoves = initInvalidMoveTable();
    }
}
