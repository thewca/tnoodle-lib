package cs.cube555;

import java.util.ArrayList;
import static cs.cube555.Util.*;

class SolvingCube extends CubieCube {

	private ArrayList<Integer> solution = new ArrayList<Integer>(60);
	private int conjIdx = 0;
	private int moveCost = 0;

	SolvingCube() {}

	SolvingCube(CubieCube cc) {
		copy(cc);
	}

	@Override
	void copy(CubieCube cc) {
		super.copy(cc);
		if (cc instanceof SolvingCube) {
			SolvingCube sc = (SolvingCube) cc;
			this.conjIdx = sc.conjIdx;
			this.moveCost = sc.moveCost;
			this.solution.clear();
			this.solution.addAll(sc.solution);
		} else {
			this.conjIdx = 0;
			this.moveCost = 0;
			this.solution.clear();
		}
	}

	@Override
	void doMove(int ... moves) {
		super.doMove(moves);
		for (int i = 0; i < moves.length; i++) {
			solution.add(CubieCube.SymMove[conjIdx][moves[i]]);
		}
		moveCost += moves.length;
	}

	@Override
	void doConj(int idx) {
		super.doConj(idx);
		conjIdx = CubieCube.SymMult[conjIdx][idx];
	}

	int[] getSolution() {
		int[] ret = new int[moveCost];
		int i = 0;
		for (int m : solution) {
			if (m != -1) {
				ret[i++] = m;
			}
		}
		return ret;
	}

	void addCheckPoint() {
		solution.add(-1);
	}

	int length() {
		return moveCost;
	}

	String toSolutionString(int verbose) {
		StringBuffer sb = new StringBuffer();
		for (int move : solution) {
			if (move == -1) {
				if ((verbose & Search.USE_SEPARATOR) != 0) {
					sb.append(".  ");
				}
				continue;
			}
			sb.append(move2str[move]).append(' ');
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		int cnt = 0;
		int cumcnt = 0;
		for (int move : solution) {
			if (move == -1) {
				cumcnt += cnt;
				sb.append(String.format("//(%df,cum=%df)\n", cnt, cumcnt));
				cnt = 0;
				continue;
			}
			cnt++;
			sb.append(move2str[move]).append(' ');
		}
		sb.append(String.format("//conjIdx=%d\n", conjIdx));
		sb.append(super.toString());
		return sb.toString();
	}
}