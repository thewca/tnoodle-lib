package cs.sq12phase;


public class Search {
    public static final int INVERSE_SOLUTION = 0x2;

    static final int FACE_TURN_METRIC = 0;
    static final int WCA_TURN_METRIC = 1;
    static final int METRIC = WCA_TURN_METRIC; // only available for optimal solver

    private static final int PRUN_INC = METRIC == WCA_TURN_METRIC ? 2 : 1;

    int[] move = new int[100];
    FullCube c = null;
    FullCube d = new FullCube("");
    Square sq = new Square();
    int length1;
    int movelen1;
    int maxlen2;
    int verbose;
    String sol_string;

    static int getNParity(int idx, int n) {
        int p = 0;
        for (int i = n - 2; i >= 0; i--) {
            p ^= idx % (n - i);
            idx /= (n - i);
        }
        return p & 1;
    }

    static {
        Shape.init();
        Square.init();
    }

    public String solution(FullCube c, int verbose) {
        this.c = c;
        this.verbose = verbose;
        sol_string = null;
        int shape = c.getShapeIdx();
        for (length1 = Shape.ShapePrun[shape]; length1 < 100; length1++) {
            maxlen2 = Math.min(31 - length1, 17);
            if (idaPhase1(shape, Shape.ShapePrun[shape], length1, 0, -1)) {
                break;
            }
        }
        return sol_string;
    }

    public String solution(FullCube c) {
        return solution(c, 0);
    }

    public String solutionOpt(FullCube c, int maxl, int verbose) {
        this.c = c;
        this.verbose = verbose;
        sol_string = null;
        int shape = c.getShapeIdx();
        for (length1 = Shape.ShapePrunOpt[shape] * PRUN_INC; length1 <= maxl * PRUN_INC; length1 += PRUN_INC) {
            if (phase1Opt(shape, Shape.ShapePrunOpt[shape], length1, 0, -1, 0)) {
                break;
            }
        }
        return sol_string;
    }

    public String solutionOpt(FullCube c, int maxl) {
        return solutionOpt(c, maxl, 0);
    }

    static int count0xf(int val) {
        val &= val >> 1;
        val &= val >> 2;
        return Integer.bitCount(val & 0x11111111);
    }

    boolean phase1Opt(int shape, int prunvalue, int maxl, int depth, int lm, int lastTurns) {
        int i = count0xf((lastTurns ^ ~0x000000) & 0xff00ff)
                - count0xf((lastTurns ^ ~0x666666) & 0xff00ff);
        if (i < 0 || i == 0 && (lastTurns >> 20 & 0xf) >= 6) {
            return false;
        }

        if (maxl / PRUN_INC == 0) {
            movelen1 = depth;
            if (isSolvedInPhase1()) {
                return true;
            }
            if (maxl == 0) {
                return false;
            }
        }
        //try each possible move. First twist;
        if (lm != 0) {
            int shapex = Shape.TwistMove[shape];
            int prun = Shape.ShapePrunOpt[shapex];
            if (prun < maxl / PRUN_INC) {
                move[depth] = 0;
                int next_maxl = (maxl / PRUN_INC - 1) * PRUN_INC;
                if (phase1Opt(shapex, prun, next_maxl, depth + 1, 0, lastTurns << 8)) {
                    return true;
                }
            }
        }

        //Try top layer
        int shapex = shape;
        if (lm <= 0) {
            int m = 0;
            while (true) {
                m += Shape.TopMove[shapex];
                shapex = m >> 4;
                m &= 0xf;
                if (m >= 12) {
                    break;
                }
                int prun = Shape.ShapePrunOpt[shapex];
                if (prun * PRUN_INC > (maxl + PRUN_INC - 1)) {
                    break;
                } else if (prun * PRUN_INC < (maxl + PRUN_INC - 1)) {
                    move[depth] = m;
                    if (phase1Opt(shapex, prun, maxl - 1, depth + 1, 1, lastTurns | m << 4)) {
                        return true;
                    }
                }
            }
        }

        shapex = shape;
        //Try bottom layer
        if (lm <= 1) {
            int m = 0;
            while (true) {
                m += Shape.BottomMove[shapex];
                shapex = m >> 4;
                m &= 0xf;
                if (m >= 12) {
                    break;
                }
                int prun = Shape.ShapePrunOpt[shapex];
                if (prun * PRUN_INC > (maxl + PRUN_INC - 1)) {
                    break;
                } else if (prun * PRUN_INC < (maxl + PRUN_INC - 1)) {
                    move[depth] = -m;
                    if (phase1Opt(shapex, prun, maxl - 1, depth + 1, 2, lastTurns | m)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    boolean idaPhase1(int shape, int prunvalue, int maxl, int depth, int lm) {
        if (prunvalue == 0 && maxl < 4) {
            movelen1 = depth;
            return maxl == 0 && initPhase2();
        }

        //try each possible move. First twist;
        if (lm != 0) {
            int shapex = Shape.TwistMove[shape];
            int prun = Shape.ShapePrun[shapex];
            if (prun < maxl) {
                move[depth] = 0;
                if (idaPhase1(shapex, prun, maxl - 1, depth + 1, 0)) {
                    return true;
                }
            }
        }
        //Try top layer
        if (lm <= 0) {
            int m = 0;
            int shapex = shape;
            while (true) {
                m += Shape.TopMove[shapex];
                shapex = m >> 4;
                m &= 0xf;
                if (m >= 12) {
                    break;
                }
                int prun = Shape.ShapePrun[shapex];
                if (prun > maxl) {
                    break;
                } else if (prun < maxl) {
                    move[depth] = m;
                    if (idaPhase1(shapex, prun, maxl - 1, depth + 1, 1)) {
                        return true;
                    }
                }
            }
        }

        //Try bottom layer
        if (lm <= 1) {
            int m = 0;
            int shapex = shape;
            while (true) {
                m += Shape.BottomMove[shapex];
                shapex = m >> 4;
                m &= 0xf;
                if (m >= 6) {
                    break;
                }
                int prun = Shape.ShapePrun[shapex];
                if (prun > maxl) {
                    break;
                } else if (prun < maxl) {
                    move[depth] = -m;
                    if (idaPhase1(shapex, prun, maxl - 1, depth + 1, 2)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    boolean isSolvedInPhase1() {
        d.copy(c);
        for (int i = 0; i < movelen1; i++) {
            d.doMove(move[i]);
        }
        boolean isSolved = d.isSolved();
        if (isSolved) {
            sol_string = move2string(movelen1);
        }
        return isSolved;
    }

    boolean initPhase2() {
        d.copy(c);
        for (int i = 0; i < movelen1; i++) {
            d.doMove(move[i]);
        }
        assert Shape.ShapePrun[d.getShapeIdx()] == 0;
        d.getSquare(sq);

        int edge = sq.edgeperm;
        int corner = sq.cornperm;
        int ml = sq.ml;

        int prun = Math.max(Square.SquarePrun[sq.edgeperm << 1 | ml],
                            Square.SquarePrun[sq.cornperm << 1 | ml]);

        for (int i = prun; i < maxlen2; i++) {
            if (idaPhase2(edge, corner, sq.topEdgeFirst, sq.botEdgeFirst, ml, i, movelen1, 0)) {
                sol_string = move2string(i + movelen1);
                return true;
            }
        }

        return false;
    }

    String move2string(int len) {
        StringBuffer s = new StringBuffer();
        int[] outputMoves = new int[len];
        if ((verbose & INVERSE_SOLUTION) != 0) {
            for (int i = len - 1; i >= 0; i--) {
                outputMoves[len - 1 - i] = move[i] > 0 ? (12 - move[i]) : move[i] < 0 ? (-12  - move[i]) : move[i];
            }
        } else {
            for (int i = 0; i < len; i++) {
                outputMoves[i] = move[i];
            }
        }

        int top = 0, bottom = 0;
        for (int i = 0; i < len; i++) {
            int val = outputMoves[i];
            if (val > 0) {
                top = (val > 6) ? (val - 12) : val;
            } else if (val < 0) {
                bottom = (-val > 6) ? (-val - 12) : -val;
            } else {
                if (top == 0 && bottom == 0) {
                    s.append(" / ");
                } else {
                    s.append('(').append(top).append(",").append(bottom).append(") / ");
                }
                top = 0;
                bottom = 0;
            }
        }
        if (top != 0 || bottom != 0) {
            s.append('(').append(top).append(",").append(bottom).append(")");
        }
        return s.toString();
    }

    boolean idaPhase2(int edge, int corner, boolean topEdgeFirst, boolean botEdgeFirst, int ml, int maxl, int depth, int lm) {
        if (maxl == 0 && !topEdgeFirst && botEdgeFirst) {
            assert edge == 0 && corner == 0 && ml == 0;
            return true;
        }

        //try each possible move. First twist;
        if (lm != 0 && topEdgeFirst == botEdgeFirst) {
            int edgex = Square.TwistMove[edge];
            int cornerx = Square.TwistMove[corner];

            if (Square.SquarePrun[edgex << 1 | (1 - ml)] < maxl && Square.SquarePrun[cornerx << 1 | (1 - ml)] < maxl) {
                move[depth] = 0;
                if (idaPhase2(edgex, cornerx, topEdgeFirst, botEdgeFirst, 1 - ml, maxl - 1, depth + 1, 0)) {
                    return true;
                }
            }
        }

        //Try top layer
        if (lm <= 0) {
            boolean topEdgeFirstx = !topEdgeFirst;
            int edgex = topEdgeFirstx ? Square.TopMove[edge] : edge;
            int cornerx = topEdgeFirstx ? corner : Square.TopMove[corner];
            int m = topEdgeFirstx ? 1 : 2;
            int prun1 = Square.SquarePrun[edgex << 1 | ml];
            int prun2 = Square.SquarePrun[cornerx << 1 | ml];
            while (m < 12 && prun1 <= maxl && prun1 <= maxl) {
                if (prun1 < maxl && prun2 < maxl) {
                    move[depth] = m;
                    if (idaPhase2(edgex, cornerx, topEdgeFirstx, botEdgeFirst, ml, maxl - 1, depth + 1, 1)) {
                        return true;
                    }
                }
                topEdgeFirstx = !topEdgeFirstx;
                if (topEdgeFirstx) {
                    edgex = Square.TopMove[edgex];
                    prun1 = Square.SquarePrun[edgex << 1 | ml];
                    m += 1;
                } else {
                    cornerx = Square.TopMove[cornerx];
                    prun2 = Square.SquarePrun[cornerx << 1 | ml];
                    m += 2;
                }
            }
        }

        if (lm <= 1) {
            boolean botEdgeFirstx = !botEdgeFirst;
            int edgex = botEdgeFirstx ? Square.BottomMove[edge] : edge;
            int cornerx = botEdgeFirstx ? corner : Square.BottomMove[corner];
            int m = botEdgeFirstx ? 1 : 2;
            int prun1 = Square.SquarePrun[edgex << 1 | ml];
            int prun2 = Square.SquarePrun[cornerx << 1 | ml];
            while (m < (maxl > 6 ? 6 : 12) && prun1 <= maxl && prun1 <= maxl) {
                if (prun1 < maxl && prun2 < maxl) {
                    move[depth] = -m;
                    if (idaPhase2(edgex, cornerx, topEdgeFirst, botEdgeFirstx, ml, maxl - 1, depth + 1, 2)) {
                        return true;
                    }
                }
                botEdgeFirstx = !botEdgeFirstx;
                if (botEdgeFirstx) {
                    edgex = Square.BottomMove[edgex];
                    prun1 = Square.SquarePrun[edgex << 1 | ml];
                    m += 1;
                } else {
                    cornerx = Square.BottomMove[cornerx];
                    prun2 = Square.SquarePrun[cornerx << 1 | ml];
                    m += 2;
                }
            }
        }
        return false;
    }
}
