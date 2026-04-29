package cs.cube555;

import static cs.cube555.Util.*;
import static cs.cube555.Phase4Search.VALID_MOVES;

/*
				0	0	1
				3		1
				3	2	2

7	7	4						3	3	0
6		4						2		0
6	5	5						2	1	1

				4	4	5
				7		5
				7	6	6
*/

class Phase4Center {

	int[] udtCenter = new int[8];
	int[] udxCenter = new int[8];
	int[] rltCenter = new int[8];
	int[] rlxCenter = new int[8];

	Phase4Center() {
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 8; i++) {
			sb.append(udtCenter[i]).append(' ');
		}
		sb.append('|');
		for (int i = 0; i < 8; i++) {
			sb.append(udxCenter[i]).append(' ');
		}
		sb.append('|');
		for (int i = 0; i < 8; i++) {
			sb.append(rltCenter[i]).append(' ');
		}
		sb.append('|');
		for (int i = 0; i < 8; i++) {
			sb.append(rlxCenter[i]).append(' ');
		}
		return sb.toString();
	}

	void setUDCenter(int idx) {
		setComb(udxCenter, idx / 70, 4);
		setComb(udtCenter, idx % 70, 4);
	}

	int getUDCenter() {
		return getComb(udxCenter, 4) * 70 + getComb(udtCenter, 4);
	}

	void setRLCenter(int idx) {
		setComb(rlxCenter, idx / 70, 4);
		setComb(rltCenter, idx % 70, 4);
	}

	int getRLCenter() {
		if (rlxCenter[7] != -1) {
			for (int i = 0; i < 8; i++) {
				rlxCenter[i] = -1 - rlxCenter[i];
			}
			for (int i = 0; i < 4; i++) {
				rltCenter[i << 1 | 1] = -1 - rltCenter[i << 1 | 1];
			}
		}
		return getComb(rlxCenter, 4) * 70 + getComb(rltCenter, 4);
	}

	void doMove(int move) {
		move = VALID_MOVES[move];
		int pow = move % 3;
		switch (move) {
		case ux2:
			swap(rltCenter, 3, 7);
			swap(rlxCenter, 3, 7);
			swap(rlxCenter, 0, 4);
		case Ux1:
		case Ux2:
		case Ux3:
			swap(udtCenter, 0, 1, 2, 3, pow);
			swap(udxCenter, 0, 1, 2, 3, pow);
			break;
		case rx2:
			swap(udtCenter, 1, 5);
			swap(udxCenter, 1, 5);
			swap(udxCenter, 2, 6);
		case Rx2:
			swap(rltCenter, 0, 1, 2, 3, pow);
			swap(rlxCenter, 0, 1, 2, 3, pow);
			break;
		case fx2:
			swap(udtCenter, 2, 4);
			swap(udxCenter, 2, 4);
			swap(udxCenter, 3, 5);
			swap(rltCenter, 2, 4);
			swap(rlxCenter, 2, 4);
			swap(rlxCenter, 3, 5);
		case Fx2:
			break;
		case dx2:
			swap(rltCenter, 1, 5);
			swap(rlxCenter, 1, 5);
			swap(rlxCenter, 2, 6);
		case Dx1:
		case Dx2:
		case Dx3:
			swap(udtCenter, 4, 5, 6, 7, pow);
			swap(udxCenter, 4, 5, 6, 7, pow);
			break;
		case lx2:
			swap(udtCenter, 3, 7);
			swap(udxCenter, 3, 7);
			swap(udxCenter, 0, 4);
		case Lx2:
			swap(rltCenter, 4, 5, 6, 7, pow);
			swap(rlxCenter, 4, 5, 6, 7, pow);
			break;
		case bx2:
			swap(udtCenter, 0, 6);
			swap(udxCenter, 0, 6);
			swap(udxCenter, 1, 7);
			swap(rltCenter, 0, 6);
			swap(rlxCenter, 0, 6);
			swap(rlxCenter, 1, 7);
		case Bx2:
			break;
		}
	}

	void doConj(int conj) {
		switch (conj) {
		case 0: //x2
			swap(udtCenter, 0, 4);
			swap(udtCenter, 1, 5);
			swap(udtCenter, 2, 6);
			swap(udtCenter, 3, 7);
			swap(udxCenter, 0, 4);
			swap(udxCenter, 1, 5);
			swap(udxCenter, 2, 6);
			swap(udxCenter, 3, 7);
			swap(rltCenter, 0, 1, 2, 3, 1);
			swap(rltCenter, 4, 5, 6, 7, 1);
			swap(rlxCenter, 0, 1, 2, 3, 1);
			swap(rlxCenter, 4, 5, 6, 7, 1);
			for (int i = 0; i < 8; i++) {
				udtCenter[i] = -1 - udtCenter[i];
				udxCenter[i] = -1 - udxCenter[i];
			}
			break;
		case 1: //y2
			swap(rltCenter, 0, 4);
			swap(rltCenter, 1, 5);
			swap(rltCenter, 2, 6);
			swap(rltCenter, 3, 7);
			swap(rlxCenter, 0, 4);
			swap(rlxCenter, 1, 5);
			swap(rlxCenter, 2, 6);
			swap(rlxCenter, 3, 7);
			swap(udtCenter, 0, 1, 2, 3, 1);
			swap(udxCenter, 0, 1, 2, 3, 1);
			swap(udtCenter, 4, 5, 6, 7, 1);
			swap(udxCenter, 4, 5, 6, 7, 1);
			for (int i = 0; i < 8; i++) {
				rltCenter[i] = -1 - rltCenter[i];
				rlxCenter[i] = -1 - rlxCenter[i];
			}
			break;
		case 2: //lr mirror
			swap(udtCenter, 1, 3);
			swap(udtCenter, 5, 7);
			swap(udxCenter, 0, 1);
			swap(udxCenter, 2, 3);
			swap(udxCenter, 4, 5);
			swap(udxCenter, 6, 7);
			swap(rltCenter, 0, 6);
			swap(rltCenter, 1, 5);
			swap(rltCenter, 2, 4);
			swap(rltCenter, 3, 7);
			swap(rlxCenter, 0, 7);
			swap(rlxCenter, 1, 6);
			swap(rlxCenter, 2, 5);
			swap(rlxCenter, 3, 4);
			for (int i = 0; i < 8; i++) {
				rltCenter[i] = -1 - rltCenter[i];
				rlxCenter[i] = -1 - rlxCenter[i];
			}
			break;
		}
	}
}