package cs.cube555;

import static cs.cube555.Util.*;

class Phase4Search extends PhaseSearch {
	static int[] VALID_MOVES = new int[] {
	    Ux1, Ux2, Ux3, Rx2, Fx2, Dx1, Dx2, Dx3, Lx2, Bx2,
	    ux2, rx2, fx2, dx2, lx2, bx2
	};

	static long[] SKIP_MOVES = genSkipMoves(VALID_MOVES);

	static int[][] MEdgeMove = new int[70][VALID_MOVES.length];
	static int[][] HEdgeMove = new int[70 * 1680][VALID_MOVES.length];
	static int[][] LEdgeMove = new int[70 * 1680][VALID_MOVES.length];
	static int[][] HEdgeConj = new int[70 * 1680][4];
	static int[] RLCenter2Half;
	static int[] Half2RLCenter;
	static int[][] UDCenterMove;
	static int[][] UDCenterConj;
	static int[][] RLCenterMove;
	static int[][] RLCenterConj;
	static int[][] MLEdgeSymMove;
	static int[] MLEdgeSym2RawF;
	static int[] MLEdgeSelfSym;
	static int[] MLEdgeRaw2Sym;
	static int[] MLEdgeMirror;
	static PruningTable EdgePrunSym;
	static PruningTable CenterPrun;
	static PruningTable MLEdgeSymUDCenterPrun;
	static PruningTable MLEdgeSymRLCenterPrun;

	static void init() {
		initCenterMove();
		initEdgeMove();
		initMLEdgeSymMove();
		initPrun();
	}

	static void initEdgeMove() {
		Phase4Edge edge = new Phase4Edge();
		for (int mEdge = 0; mEdge < 70; mEdge++) {
			for (int i = 0; i < 1680; i++) {
				for (int m = 0; m < VALID_MOVES.length; m++) {
					edge.setMEdge(mEdge);
					edge.setHEdge(i);
					edge.setLEdge(i);
					edge.doMove(m);
					MEdgeMove[mEdge][m] = edge.getMEdge();
					HEdgeMove[mEdge * 1680 + i][m] = edge.getHEdge();
					LEdgeMove[mEdge * 1680 + i][m] = edge.getLEdge();
				}

				edge.setMEdge(mEdge);
				edge.setHEdge(i);
				for (int sym = 0; sym < 4; sym++) {
					HEdgeConj[mEdge * 1680 + i][sym] = edge.getHEdge();
					edge.doConj(0);
					if ((sym & 1) == 1) {
						edge.doConj(1);
					}
				}
			}
		}
	}

	static void initMLEdgeSymMove() {
		Phase4Edge edge = new Phase4Edge();

		MLEdgeMirror = new int[70 * 1680];
		for (int i = 0; i < MLEdgeMirror.length; i++) {
			edge.setMEdge(i % 70);
			edge.setLEdge(i / 70);
			edge.doConj(2);
			MLEdgeMirror[i] = edge.getHEdge() * 70 + edge.getMEdge();
		}

		int symCnt = 0;
		MLEdgeSym2RawF = new int[29616 * 4];
		MLEdgeSelfSym = new int[29616];
		MLEdgeRaw2Sym = new int[70 * 1680];
		for (int i = 0; i < MLEdgeRaw2Sym.length; i++) {
			if (MLEdgeRaw2Sym[i] != 0) {
				continue;
			}
			edge.setMEdge(i % 70);
			edge.setLEdge(i / 70);
			for (int sym = 0; sym < 4; sym++) {
				int idx = edge.getLEdge() * 70 + edge.getMEdge();
				MLEdgeRaw2Sym[idx] = symCnt << 2 | sym;
				MLEdgeSym2RawF[symCnt << 2 | sym] = idx;
				if (idx == i) {
					MLEdgeSelfSym[symCnt] |= 1 << sym;
				}
				edge.doConj(0);
				if ((sym & 1) == 1) {
					edge.doConj(1);
				}
			}
			symCnt++;
		}
		MLEdgeSymMove = new int[symCnt][VALID_MOVES.length];
		for (int i = 0; i < symCnt; i++) {
			for (int m = 0; m < VALID_MOVES.length; m++) {
				edge.setMEdge(MLEdgeSym2RawF[i << 2] % 70);
				edge.setLEdge(MLEdgeSym2RawF[i << 2] / 70);
				edge.doMove(m);
				MLEdgeSymMove[i][m] = MLEdgeRaw2Sym[edge.getLEdge() * 70 + edge.getMEdge()];
			}
		}

		final int[] mEdge = new int[2];
		EdgePrunSym = new PruningTable(new SymCoord() {
			{
				N_IDX = 29616;
				N_MOVES = VALID_MOVES.length;
				N_SYM = 4;
				SelfSym = MLEdgeSelfSym;
			}
			@Override
			void set(int idx) {
				this.idx = idx;
				mEdge[0] = MLEdgeSym2RawF[idx << 2] % 70;
			}
			int getMoved(int move) {
				mEdge[1] = MLEdgeSym2RawF[MLEdgeSymMove[idx][move]] % 70;
				return MLEdgeSymMove[idx][move];
			}
		}, new RawCoord() {
			{
				N_IDX = 1680;
			}
			@Override
			int getMoved(int move) {
				return Phase4Search.HEdgeMove[mEdge[0] * 1680 + idx][move];
			}
			@Override
			int getConj(int idx, int conj) {
				return Phase4Search.HEdgeConj[mEdge[1] * 1680 + idx][conj];
			}
		}, null, "Phase4EdgeSym");
	}

	static void initCenterMove() {
		Phase4Center center = new Phase4Center();
		RLCenter2Half = new int[2450];
		Half2RLCenter = new int[216];
		for (int i = 0; i < RLCenter2Half.length; i++) {
			RLCenter2Half[i] = -1;
		}
		int tail = 0;
		Half2RLCenter[0] = 0;
		RLCenter2Half[0] = tail++;
		while (tail < 216) {
			for (int i = 0; i < RLCenter2Half.length; i++) {
				if (RLCenter2Half[i] == -1) {
					continue;
				}
				for (int m = 10; m < VALID_MOVES.length; m++) {
					center.setRLCenter(i);
					center.doMove(m);
					int idx = center.getRLCenter();
					if (RLCenter2Half[idx] == -1) {
						Half2RLCenter[tail] = idx;
						RLCenter2Half[idx] = tail++;
					}
				}
			}
		}
		UDCenterMove = new int[4900][VALID_MOVES.length];
		RLCenterMove = new int[216][VALID_MOVES.length];
		UDCenterConj = new int[4900][8];
		RLCenterConj = new int[216][8];
		for (int i = 0; i < 4900; i++) {
			for (int m = 0; m < VALID_MOVES.length; m++) {
				center.setUDCenter(i);
				center.doMove(m);
				UDCenterMove[i][m] = center.getUDCenter();
			}
			center.setUDCenter(i);
			for (int sym = 0; sym < 8; sym++) {
				UDCenterConj[i][sym] = center.getUDCenter();
				center.doConj(0);
				if ((sym & 1) == 1) {
					center.doConj(1);
				}
				if ((sym & 3) == 3) {
					center.doConj(2);
				}
			}
		}
		for (int i = 0; i < 216; i++) {
			for (int m = 0; m < VALID_MOVES.length; m++) {
				center.setRLCenter(Half2RLCenter[i]);
				center.doMove(m);
				RLCenterMove[i][m] = RLCenter2Half[center.getRLCenter()];
			}
			center.setRLCenter(Half2RLCenter[i]);
			for (int sym = 0; sym < 8; sym++) {
				RLCenterConj[i][sym] = RLCenter2Half[center.getRLCenter()];
				center.doConj(0);
				if ((sym & 1) == 1) {
					center.doConj(1);
				}
				if ((sym & 3) == 3) {
					center.doConj(2);
				}
			}
		}
	}

	static void initPrun() {
		int[] UDSOLVED = new int[] {0, 1895, 1967, 2905, 2977, 4876};
		int[] RLSOLVED = new int[UDSOLVED.length / 2];
		for (int i = 0; i < UDSOLVED.length / 2; i++) {
			RLSOLVED[i] = RLCenter2Half[UDSOLVED[i]];
		}
		SymCoord MLEdgeSymCoord = new TableSymCoord(MLEdgeSymMove, MLEdgeSelfSym, 4);
		CenterPrun = new PruningTable(RLCenterMove, UDCenterMove, RLSOLVED, UDSOLVED, "Phase4Center");
		MLEdgeSymUDCenterPrun = new PruningTable(MLEdgeSymCoord, new TableRawCoord(UDCenterMove, UDCenterConj), packSolved(null, UDSOLVED), "Phase4MLEdgeSymUDCenter");
		MLEdgeSymRLCenterPrun = new PruningTable(MLEdgeSymCoord, new TableRawCoord(RLCenterMove, RLCenterConj), packSolved(null, RLSOLVED), "Phase4MLEdgeSymRLCenter");
	}

	static class Phase4Node extends Node {
		int rlCenter;
		int udCenter;
		int mEdge;
		int lEdge;
		int hEdge;
		int getPrun() {
			int mlEdges = MLEdgeRaw2Sym[lEdge * 70 + mEdge];
			int mhEdges = MLEdgeRaw2Sym[MLEdgeMirror[hEdge * 70 + mEdge]];
			int prun = CenterPrun.getPrun(rlCenter, udCenter);
			prun = Math.max(prun,
			                EdgePrunSym.getPrun(mlEdges >> 2, HEdgeConj[mEdge * 1680 + hEdge][mlEdges & 0x3]));
			prun = Math.max(prun,
			                MLEdgeSymRLCenterPrun.getPrun(mlEdges >> 2, RLCenterConj[rlCenter][mlEdges & 3]));
			prun = Math.max(prun,
			                MLEdgeSymRLCenterPrun.getPrun(mhEdges >> 2, RLCenterConj[rlCenter][mhEdges & 3 | 4]));
			prun = Math.max(prun,
			                MLEdgeSymUDCenterPrun.getPrun(mlEdges >> 2, UDCenterConj[udCenter][mlEdges & 3]));
			return Math.max(prun,
			                MLEdgeSymUDCenterPrun.getPrun(mhEdges >> 2, UDCenterConj[udCenter][mhEdges & 3 | 4]));
		}
		int doMovePrun(Node node0, int move, int maxl) {
			Phase4Node node = (Phase4Node) node0;
			rlCenter = RLCenterMove[node.rlCenter][move];
			udCenter = UDCenterMove[node.udCenter][move];
			mEdge = MEdgeMove[node.mEdge][move];
			lEdge = LEdgeMove[node.mEdge * 1680 + node.lEdge][move];
			hEdge = HEdgeMove[node.mEdge * 1680 + node.hEdge][move];
			int mlEdges = MLEdgeRaw2Sym[lEdge * 70 + mEdge];
			int mhEdges = MLEdgeRaw2Sym[MLEdgeMirror[hEdge * 70 + mEdge]];

			if (maxl <= EdgePrunSym.getPrun(mlEdges >> 2, HEdgeConj[mEdge * 1680 + hEdge][mlEdges & 0x3])) {
				return maxl;
			} else if (maxl <= MLEdgeSymRLCenterPrun.getPrun(mlEdges >> 2, RLCenterConj[rlCenter][mlEdges & 3])) {
				return maxl;
			} else if (maxl <= MLEdgeSymRLCenterPrun.getPrun(mhEdges >> 2, RLCenterConj[rlCenter][mhEdges & 3 | 4])) {
				return maxl;
			} else if (maxl <= MLEdgeSymUDCenterPrun.getPrun(mlEdges >> 2, UDCenterConj[udCenter][mlEdges & 3])) {
				return maxl;
			} else if (maxl <= MLEdgeSymUDCenterPrun.getPrun(mhEdges >> 2, UDCenterConj[udCenter][mhEdges & 3 | 4])) {
				return maxl;
			} else if (maxl <= CenterPrun.getPrun(rlCenter, udCenter)) {
				return maxl;
			}
			return maxl - 1;
		}
	}

	Phase4Search() {
		super.VALID_MOVES = VALID_MOVES;
		for (int i = 0; i < searchNode.length; i++) {
			searchNode[i] = new Phase4Node();
		}
	}

	Node[] initFrom(CubieCube cc) {
		Phase4Edge edge = new Phase4Edge();
		Phase4Center center = new Phase4Center();

		for (int i = 0; i < 8; i++) {
			center.udxCenter[i] = cc.xCenter[i] == 0 ? 0 : -1;
			center.udtCenter[i] = cc.tCenter[i] == 0 ? 0 : -1;
			center.rlxCenter[i] = cc.xCenter[16 + (i & 4) + (i + 1) % 4] == 1 ? 0 : -1;
			center.rltCenter[i] = cc.tCenter[16 + (i & 4) + (i + 1) % 4] == 1 ? 0 : -1;
		}
		int rlCenter = RLCenter2Half[center.getRLCenter()];
		int udCenter = center.getUDCenter();

		int maskY = 0;
		for (int i = 0; i < 4; i++) {
			maskY |= 1 << (cc.wEdge[8 + i] % 12);
			maskY |= 1 << (cc.wEdge[8 + i + 12] % 12);
			maskY |= 1 << (cc.mEdge[8 + i] >> 1);
		}
		maskY ^= 0xfff;
		int bitCnt = Integer.bitCount(maskY);

		Node[] nodes = new Node[Cnk[bitCnt][4]];
		// System.out.println(nodes.length);
		int idx = 0;
		for (int mask = maskY; mask != 0; mask = mask - 1 & maskY) {
			if (Integer.bitCount(mask) != 4) {
				continue;
			}
			for (int i = 0; i < 8; i++) {
				int e = cc.mEdge[i] >> 1;
				edge.mEdge[i] = (mask >> e & 1) == 0 ? -1 : Integer.bitCount(mask & ((1 << e) - 1));
				e = cc.wEdge[i] % 12;
				edge.lEdge[i] = (mask >> e & 1) == 0 ? -1 : Integer.bitCount(mask & ((1 << e) - 1));
				e = cc.wEdge[i + 12] % 12;
				edge.hEdge[i] = (mask >> e & 1) == 0 ? -1 : Integer.bitCount(mask & ((1 << e) - 1));
			}
			edge.isStd = false;
			Phase4Node node = new Phase4Node();
			node.mEdge = edge.getMEdge();
			node.lEdge = edge.getLEdge();
			node.hEdge = edge.getHEdge();
			node.rlCenter = rlCenter;
			node.udCenter = udCenter;
			nodes[idx] = node;
			idx++;
		}

		return nodes;
	}
}