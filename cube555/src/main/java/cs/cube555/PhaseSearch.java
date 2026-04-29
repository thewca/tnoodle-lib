package cs.cube555;

import java.util.ArrayList;
import java.util.TreeSet;
import static cs.cube555.Util.*;

class PhaseSearch {
	SolutionChecker callback = null;
	int[] solution = new int[255];
	int ccidx;

	class PhaseEntry implements Comparable<PhaseEntry> {
		Node node;
		int prun;
		int cumCost;
		int estCost;
		int ccidx;

		@Override public int compareTo(PhaseEntry entry) {
			if (this == entry) {
				return 0;
			}
			if (estCost != entry.estCost) {
				return estCost - entry.estCost;
			}
			if (cumCost != entry.cumCost) {
				return cumCost - entry.cumCost;
			}
			return 1;
		}
	}

	void solve(SolvingCube[] cc, SolutionChecker callback) {
		solve(cc, callback, Integer.MAX_VALUE);
	}

	void solve(SolvingCube[] cc, SolutionChecker callback, int trySize) {
		if (SKIP_MOVES == null) {
			SKIP_MOVES = genSkipMoves(VALID_MOVES);
			NEXT_AXIS = genNextAxis(VALID_MOVES);
		}
		this.callback = callback;
		long startTime = System.nanoTime();

		TreeSet<PhaseEntry> entries = new TreeSet<PhaseEntry>();
		for (ccidx = 0; ccidx < cc.length; ccidx++) {
			Node[] nodes = initFrom(cc[ccidx]);
			int cumCost = cc[ccidx].length();
			for (int i = 0; i < nodes.length; i++) {
				PhaseEntry entry = new PhaseEntry();
				entry.node = nodes[i];
				entry.prun = nodes[i].getPrun();
				entry.cumCost = cumCost;
				entry.estCost = cumCost + entry.prun;
				entry.ccidx = ccidx;
				entries.add(entry);
				if (entries.size() > trySize) {
					entries.pollLast();
				}
			}
		}
		// nodeCnt = 0;
		out: for (int maxl = 0; maxl < 100; maxl++) {
			for (PhaseEntry entry : entries) {
				ccidx = entry.ccidx;
				if (maxl >= entry.estCost &&
				        idaSearch(entry.node, 0, maxl - entry.cumCost, VALID_MOVES.length, entry.prun) == 0) {
					break out;
				}
			}
		}
		// System.out.println(nodeCnt);
	}

	Node[] initFrom(CubieCube cc) {
		return null;
	}

	abstract static class Node {
		/**
		 *  other requirements besides getPrun() == 0
		 */
		boolean isSolved() {
			return true;
		}
		abstract int doMovePrun(Node node, int move, int maxl);
		abstract int getPrun();
	}

	Node[] searchNode = new Node[30];
	// static int nodeCnt = 0;

	private int idaSearch(Node node, int depth, int maxl, int lm, int prun) {
		if (prun == 0 && node.isSolved() && maxl < MIN_BACK_DEPTH) {
			return maxl != 0 ? 1 : callback.check(solution, depth, ccidx);
		}
		long skipMoves = SKIP_MOVES[lm];
		for (int move = 0; move < VALID_MOVES.length; move++) {
			if ((skipMoves >> move & 1) != 0) {
				continue;
			}
			// nodeCnt++;
			prun = searchNode[depth].doMovePrun(node, move, maxl);
			if (prun >= maxl) {
				move += NEXT_AXIS >> move & 3 & (maxl - prun);
				continue;
			}
			solution[depth] = VALID_MOVES[move];
			int ret = idaSearch(searchNode[depth], depth + 1, maxl - 1, move, prun);
			if (ret == 0) {
				return 0;
			}
		}
		return 1;
	}

	long[] SKIP_MOVES;
	int[] VALID_MOVES;
	long NEXT_AXIS;
	int MIN_BACK_DEPTH = 1;
}