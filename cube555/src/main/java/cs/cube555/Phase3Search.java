package cs.cube555;

import static cs.cube555.Util.*;

class Phase3Search extends PhaseSearch {

	static int[] VALID_MOVES = new int[] {
	    Ux1, Ux2, Ux3, Rx1, Rx2, Rx3, Fx1, Fx2, Fx3, Dx1, Dx2, Dx3, Lx1, Lx2, Lx3, Bx1, Bx2, Bx3,
	    ux2, rx2, fx2, dx2, lx2, bx2
	};

	static int[][] SymMove;

	static long[] SKIP_MOVES = genSkipMoves(VALID_MOVES);
	static int NEXT_AXIS = 0x12492;

	static int[][] CenterMove;
	static int[][] MEdgeMove;
	static int[][] MEdgeConj;
	static PruningTable CenterMEdgePrun;

	static int[][] WEdgeSymMove;
	static int[] WEdgeSym2Raw;
	static int[] WEdgeSelfSym;
	static int[] WEdgeRaw2Sym;

	static void init() {
		initWEdgeSymMove();
		initMEdgeMove();
		initCenterMove();
		initPrun();
	}

	static void initWEdgeSymMove() {
		Phase3Edge edge = new Phase3Edge();
		int symCnt = 0;
		WEdgeSym2Raw = new int[86048];
		WEdgeSelfSym = new int[86048];
		WEdgeRaw2Sym = new int[2704156];
		for (int i = 0; i < WEdgeRaw2Sym.length; i++) {
			if (WEdgeRaw2Sym[i] != 0) {
				continue;
			}
			edge.setWEdge(i);
			for (int sym = 0; sym < 32; sym++) {
				int idx = edge.getWEdge();
				WEdgeRaw2Sym[idx] = symCnt << 5 | sym;
				if (idx == i) {
					WEdgeSelfSym[symCnt] |= 1 << sym;
				}
				edge.doConj(0);
				if ((sym & 3) == 3) {
					edge.doConj(1);
				}
				if ((sym & 7) == 7) {
					edge.doConj(2);
				}
				if ((sym & 0xf) == 0xf) {
					edge.doConj(3);
				}
			}
			WEdgeSym2Raw[symCnt] = i;
			symCnt++;
		}
		WEdgeSymMove = new int[symCnt][VALID_MOVES.length];
		for (int i = 0; i < symCnt; i++) {
			for (int m = 0; m < VALID_MOVES.length; m++) {
				edge.setWEdge(WEdgeSym2Raw[i]);
				edge.doMove(m);
				WEdgeSymMove[i][m] = WEdgeRaw2Sym[edge.getWEdge()];
			}
		}
	}

	static void initMEdgeMove() {
		Phase3Edge edge = new Phase3Edge();
		MEdgeMove = new int[2048][VALID_MOVES.length];
		MEdgeConj = new int[2048][32];
		for (int i = 0; i < 2048; i++) {
			for (int m = 0; m < VALID_MOVES.length; m++) {
				edge.setMEdge(i);
				edge.doMove(m);
				MEdgeMove[i][m] = edge.getMEdge();
			}

			edge.setMEdge(i);
			for (int sym = 0; sym < 32; sym++) {
				MEdgeConj[i][CubieCube.SymMultInv[0][sym & 0xf] | sym & 0x10] = edge.getMEdge();
				edge.doConj(0);
				if ((sym & 3) == 3) {
					edge.doConj(1);
				}
				if ((sym & 7) == 7) {
					edge.doConj(2);
				}
				if ((sym & 0xf) == 0xf) {
					edge.doConj(3);
				}
			}
		}
	}

	static void initCenterMove() {
		Phase3Center center = new Phase3Center();
		CenterMove = new int[1225][VALID_MOVES.length];
		for (int i = 0; i < 1225; i++) {
			for (int m = 0; m < VALID_MOVES.length; m++) {
				center.setCenter(i);
				center.doMove(m);
				CenterMove[i][m] = center.getCenter();
			}
		}
	}

	static PruningTable WMEdgeSymPrun;

	static void initPrun() {
		CenterMEdgePrun = new PruningTable(
		    CenterMove, MEdgeMove,
		    Phase3Center.SOLVED_CENTER, new int[] {0, 2047},
		    "Phase3CenterMEdge");

		final int[] mEdgeFlip = new int[1];
		WMEdgeSymPrun = new PruningTable(new SymCoord() {
			{
				N_IDX = 86048;
				N_MOVES = VALID_MOVES.length;
				N_SYM = 16;
				SelfSym = WEdgeSelfSym;
			}
			int getMoved(int move) {
				int val = WEdgeSymMove[idx][move];
				mEdgeFlip[0] = (val & 0x10) == 0 ? 0 : 0x7ff;
				return val >> 1 & ~0xf | val & 0xf;
			}
		}, new RawCoord() {
			{
				N_IDX = 2048;
			}
			int getMoved(int move) {
				return MEdgeMove[idx][move] ^ mEdgeFlip[0];
			}
			int getConj(int idx, int conj) {
				return MEdgeConj[idx][conj];
			}
		}, null, "Phase3MWEdgeSym");
	}

	static class Phase3Node extends Node {
		int center;
		int mEdge;
		int wEdge;
		int getPrun() {
			return Math.max(
			           CenterMEdgePrun.getPrun(center, mEdge),
			           WMEdgeSymPrun.getPrun(wEdge >> 5, MEdgeConj[mEdge][wEdge & 0x1f]));
		}
		int doMovePrun(Node node0, int move, int maxl) {
			Phase3Node node = (Phase3Node) node0;
			center = CenterMove[node.center][move];
			mEdge = MEdgeMove[node.mEdge][move];
			wEdge = WEdgeSymMove[node.wEdge >> 5][SymMove[node.wEdge & 0xf][move]] ^ (node.wEdge & 0x10);
			wEdge = wEdge & ~0xf | CubieCube.SymMult[wEdge & 0xf][node.wEdge & 0xf];
			return getPrun();
		}
	}

	Phase3Search() {
		super.VALID_MOVES = VALID_MOVES;
		for (int i = 0; i < searchNode.length; i++) {
			searchNode[i] = new Phase3Node();
		}
	}

	Node[] initFrom(CubieCube cc) {
		if (SymMove == null) {
			SymMove = CubieCube.getSymMove(VALID_MOVES, 16);
		}
		Phase3Center ct = new Phase3Center();
		Phase3Edge ed = new Phase3Edge();
		for (int i = 0; i < 8; i++) {
			ct.xCenter[i] = cc.xCenter[16 + (i & 4) + (i + 1) % 4] == 1 ? 0 : -1;
			ct.tCenter[i] = cc.tCenter[16 + (i & 4) + (i + 1) % 4] == 1 ? 0 : -1;
		}
		int center = ct.getCenter();
		java.util.ArrayList<Node> nodes = new java.util.ArrayList<Node>();
		for (int filter = 8; nodes.size() == 0; filter++) {
			for (int idx = 0; idx < 1024; idx++) {
				Phase3Node node = new Phase3Node();
				int flip = idx << 1 | (Integer.bitCount(idx) & 1);
				flip = (flip ^ 0xfff) << 12 | flip;
				for (int i = 0; i < 12; i++) {
					ed.mEdge[i] = cc.mEdge[i] & 1;
					ed.mEdge[i] ^= flip >> (cc.mEdge[i] >> 1) & 1;
				}
				for (int i = 0; i < 24; i++) {
					ed.wEdge[i] = (flip >> cc.wEdge[i] & 1) == 0 ? 0 : -1;
				}
				node.mEdge = ed.getMEdge();
				node.wEdge = WEdgeRaw2Sym[ed.getWEdge()];
				node.center = center;
				if (node.getPrun() > filter) {
					continue;
				}
				nodes.add(node);
			}
		}
		return nodes.toArray(new Node[0]);
	}

	public static void main(String[] args) {
		initMEdgeMove();
		initCenterMove();
		initWEdgeSymMove();
		initPrun();
	}
}