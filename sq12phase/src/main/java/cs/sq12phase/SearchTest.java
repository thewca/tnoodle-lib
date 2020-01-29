package cs.sq12phase;

public class SearchTest {

    static void OptimalSolverTest() {
        long t = System.nanoTime();
        Search s = new Search();
        java.util.Random gen = new java.util.Random(42L);
        int targetLength = 11;
        int scrambleLength = 11;
        for (int x = 0; x < 1000; x++) {
            FullCube fc = new FullCube();
            int shape = fc.getShapeIdx();
            int lm = -1;
            for (int i = 0; i < scrambleLength; i++) {
                int move;
                do {
                    move = gen.nextInt(3);
                } while (move == lm || move == 1 && lm == 2);
                lm = move;
                if (move == 0) {
                    s.move[i] = 0;
                    fc.doMove(0);
                } else if (move == 1) {
                    int m = Shape.TopMove[shape] & 0xf;
                    s.move[i] = m;
                    fc.doMove(m);
                } else {
                    int m = Shape.BottomMove[shape] & 0xf;
                    s.move[i] = -m;
                    fc.doMove(-m);
                }
                shape = fc.getShapeIdx();
            }
            s.verbose = 0;
            System.out.println("Scramble: " + s.move2string(scrambleLength));
            String sol = s.solutionOpt(fc, targetLength);
            System.out.println("Solution: " + sol);
            System.out.println(
                String.format("%.2fms\n",
                              (System.nanoTime() - t) / 1000000.0 / (x + 1)));
        }
    }

    static void RandomSolvingTest() {
        long t = System.nanoTime();
        Search s = new Search();
        java.util.Random gen = new java.util.Random(42L);
        for (int x = 0; x < 1000; x++) {
            String sol = s.solution(FullCube.randomCube(gen), Search.INVERSE_SOLUTION);
            System.out.println(sol);
            System.out.println(
                String.format("%.2fms",
                              (System.nanoTime() - t) / 1000000.0 / (x + 1)));
        }
    }

    public static void main(String[] args) {
        long t = System.nanoTime();

        new Search().solution(new FullCube(""));
        System.out.println((System.nanoTime() - t) / 1e9 + " seconds to initialize");

        RandomSolvingTest();
        OptimalSolverTest();
    }
}
