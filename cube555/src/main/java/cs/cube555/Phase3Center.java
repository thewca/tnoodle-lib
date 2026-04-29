package cs.cube555;

import static cs.cube555.Util.*;
import static cs.cube555.Phase3Search.VALID_MOVES;

/*

7	7	4						3	3	0
6		4						2		0
6	5	5						2	1	1
*/

class Phase3Center {

	static int[] SOLVED_XCENTER = new int[] {0, 9, 14, 23, 27, 28};
	static int[] SOLVED_TCENTER = new int[] {0, 2, 4, 5, 7, 9, 11, 12, 14, 16, 18, 23, 25, 27, 28, 30, 32, 34};
	static int[] SOLVED_CENTER = new int[108];
	static {
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 18; j++) {
				SOLVED_CENTER[i * 18 + j] = SOLVED_XCENTER[i] * 35 + SOLVED_TCENTER[j];
			}
		}
	}

	int[] tCenter = new int[8];
	int[] xCenter = new int[8];

	Phase3Center() {
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 8; i++) {
			sb.append(tCenter[i]).append(' ');
		}
		sb.append('|');
		for (int i = 0; i < 8; i++) {
			sb.append(xCenter[i]).append(' ');
		}
		return sb.toString();
	}

	void setCenter(int idx) {
		setComb(xCenter, idx / 35, 4);
		setComb(tCenter, idx % 35, 4);
	}

	int getCenter() {
		return getSComb(xCenter) * 35 + getSComb(tCenter);
	}

	void doMove(int move) {
		move = VALID_MOVES[move];
		int pow = move % 3;
		switch (move) {
		case ux2:
			swap(tCenter, 3, 7);
			swap(xCenter, 3, 7);
			swap(xCenter, 0, 4);
		case Ux1:
		case Ux2:
		case Ux3:
			break;
		case rx2:
		case Rx1:
		case Rx2:
		case Rx3:
			swap(tCenter, 0, 1, 2, 3, pow);
			swap(xCenter, 0, 1, 2, 3, pow);
			break;
		case fx2:
			swap(tCenter, 2, 4);
			swap(xCenter, 2, 4);
			swap(xCenter, 3, 5);
		case Fx1:
		case Fx2:
		case Fx3:
			break;
		case dx2:
			swap(tCenter, 1, 5);
			swap(xCenter, 1, 5);
			swap(xCenter, 2, 6);
		case Dx1:
		case Dx2:
		case Dx3:
			break;
		case lx2:
		case Lx1:
		case Lx2:
		case Lx3:
			swap(tCenter, 4, 5, 6, 7, pow);
			swap(xCenter, 4, 5, 6, 7, pow);
			break;
		case bx2:
			swap(tCenter, 0, 6);
			swap(xCenter, 0, 6);
			swap(xCenter, 1, 7);
		case Bx2:
			break;
		}
	}

	void doConj(int conj) {
		switch (conj) {
		case 0: //x
			doMove(Rx1);
			doMove(Lx3);
			break;
		case 1: //y2
			swap(tCenter, 0, 4);
			swap(tCenter, 1, 5);
			swap(tCenter, 2, 6);
			swap(tCenter, 3, 7);
			swap(xCenter, 0, 4);
			swap(xCenter, 1, 5);
			swap(xCenter, 2, 6);
			swap(xCenter, 3, 7);
			break;
		case 2: //lr2
			swap(tCenter, 0, 6);
			swap(tCenter, 1, 5);
			swap(tCenter, 2, 4);
			swap(tCenter, 3, 7);
			swap(xCenter, 0, 7);
			swap(xCenter, 1, 6);
			swap(xCenter, 2, 5);
			swap(xCenter, 3, 4);
			break;
		}
	}
}