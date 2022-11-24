package cs.cube555;

import static cs.cube555.Util.*;

class Phase2Search extends PhaseSearch {

	static int[] VALID_MOVES = new int[] {
	    Ux1, Ux2, Ux3, Fx1, Fx2, Fx3, Dx1, Dx2, Dx3, Bx1, Bx2, Bx3,
	    ux2, rx1, rx2, rx3, fx2, dx2, lx1, lx2, lx3, bx2
	};

	static long[] SKIP_MOVES = genSkipMoves(VALID_MOVES);

	static int[][] TCenterMove;
	static int[][] XCenterMove;
	static PruningTable prunTCenter;
	static PruningTable prunXCenter;

	static void init() {
		initCenterMove();
		initCenterPrun();
	}

	static void initCenterMove() {
		Phase2Center ct = new Phase2Center();
		TCenterMove = new int[12870][VALID_MOVES.length];
		XCenterMove = new int[12870][VALID_MOVES.length];
		for (int i = 0; i < 12870; i++) {
			for (int m = 0; m < VALID_MOVES.length; m++) {
				ct.setTCenter(i);
				ct.setXCenter(i);
				ct.doMove(m);
				TCenterMove[i][m] = ct.getTCenter();
				XCenterMove[i][m] = ct.getXCenter();
			}
		}
	}

	static void initCenterPrun() {
		int[][] EParityMove = new int[2][VALID_MOVES.length];
		for (int i = 0; i < VALID_MOVES.length; i++) {
			EParityMove[0][i] = 0 ^ Phase2Center.eParityDiff[i];
			EParityMove[1][i] = 1 ^ Phase2Center.eParityDiff[i];
		}
		prunTCenter = new PruningTable(TCenterMove, EParityMove, null, null, "Phase2TCenter");
		prunXCenter = new PruningTable(XCenterMove, EParityMove, null, null, "Phase2XCenter");
	}

	static class Phase2Node extends Node {
		int tCenter;
		int xCenter;
		int eParity;
		int getPrun() {
			return Math.max(prunTCenter.getPrun(tCenter, eParity),
			                prunXCenter.getPrun(xCenter, eParity));
		}
		int doMovePrun(Node node0, int move, int maxl) {
			Phase2Node node = (Phase2Node) node0;
			tCenter = TCenterMove[node.tCenter][move];
			xCenter = XCenterMove[node.xCenter][move];
			eParity = node.eParity ^ Phase2Center.eParityDiff[move];
			return getPrun();
		}
	}

	Phase2Search() {
		super.VALID_MOVES = VALID_MOVES;
		super.MIN_BACK_DEPTH = 5;
		for (int i = 0; i < searchNode.length; i++) {
			searchNode[i] = new Phase2Node();
		}
	}

	Node[] initFrom(CubieCube cc) {
		Phase2Center ct = new Phase2Center();
		for (int i = 0; i < 16; i++) {
			ct.xCenter[i] = cc.xCenter[i] == 0 || cc.xCenter[i] == 3 ? 0 : -1;
			ct.tCenter[i] = cc.tCenter[i] == 0 || cc.tCenter[i] == 3 ? 0 : -1;
		}
		Phase2Node node = new Phase2Node();
		node.xCenter = ct.getXCenter();
		node.tCenter = ct.getTCenter();
		node.eParity = getParity(cc.wEdge);
		return new Node[] {node};
	}
}