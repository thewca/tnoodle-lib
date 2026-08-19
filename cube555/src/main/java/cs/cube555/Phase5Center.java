package cs.cube555;

import static cs.cube555.Util.*;
import static cs.cube555.Phase5Search.VALID_MOVES;

/*
				0	0	1
				3		1
				3	2	2

7		6		1		0		5		4		3		2

				4	4	5
				7		5
				7	6	6
*/

class Phase5Center {
	int[] tCenter = new int[8];
	int[] xCenter = new int[8];
	int[] rflbCenter = new int[8];

	Phase5Center() {
		setRFLBCenter(0);
		setXCenter(0);
		setTCenter(0);
	}

	void setRFLBCenter(int idx) {
		int[] fbCenter = new int[4];
		int[] rlCenter = new int[4];
		setComb(fbCenter, idx % 6, 2);
		setComb(rlCenter, idx / 6, 2);
		for (int i = 0; i < 4; i++) {
			rflbCenter[i] = fbCenter[i];
			rflbCenter[i + 4] = rlCenter[i];
		}
	}

	int getRFLBCenter() {
		int[] fbCenter = new int[4];
		int[] rlCenter = new int[4];
		for (int i = 0; i < 4; i++) {
			fbCenter[i] = rflbCenter[i];
			rlCenter[i] = rflbCenter[i + 4];
		}
		return getComb(rlCenter, 2) * 6 + getComb(fbCenter, 2);
	}

	void setXCenter(int idx) {
		setComb(xCenter, idx, 4);
	}

	int getXCenter() {
		return getComb(xCenter, 4);
	}

	void setTCenter(int idx) {
		setComb(tCenter, idx, 4);
	}

	int getTCenter() {
		return getComb(tCenter, 4);
	}

	void doMove(int move) {
		move = VALID_MOVES[move];
		int pow = move % 3;
		switch (move) {
		case Ux1:
		case Ux2:
		case Ux3:
			swap(tCenter, 0, 1, 2, 3, pow);
			swap(xCenter, 0, 1, 2, 3, pow);
			break;
		case rx2:
			swap(tCenter, 1, 5);
			swap(xCenter, 1, 5);
			swap(xCenter, 2, 6);
			swap(rflbCenter, 0, 3);
		case Rx2:
			swap(rflbCenter, 4, 5);
			break;
		case fx2:
			swap(tCenter, 2, 4);
			swap(xCenter, 2, 4);
			swap(xCenter, 3, 5);
			swap(rflbCenter, 5, 6);
		case Fx2:
			swap(rflbCenter, 0, 1);
			break;
		case Dx1:
		case Dx2:
		case Dx3:
			swap(tCenter, 4, 5, 6, 7, pow);
			swap(xCenter, 4, 5, 6, 7, pow);
			break;
		case lx2:
			swap(tCenter, 3, 7);
			swap(xCenter, 3, 7);
			swap(xCenter, 0, 4);
			swap(rflbCenter, 1, 2);
		case Lx2:
			swap(rflbCenter, 6, 7);
			break;
		case bx2:
			swap(tCenter, 0, 6);
			swap(xCenter, 0, 6);
			swap(xCenter, 1, 7);
			swap(rflbCenter, 4, 7);
		case Bx2:
			swap(rflbCenter, 2, 3);
			break;
		}
	}

	void doConj(int conj) {
		switch (conj) {
		case 0: //y
			swap(tCenter, 0, 1, 2, 3, 0);
			swap(xCenter, 0, 1, 2, 3, 0);
			swap(tCenter, 4, 5, 6, 7, 2);
			swap(xCenter, 4, 5, 6, 7, 2);
			for (int i = 0; i < 4; i++) {
				rflbCenter[i] = -1 - rflbCenter[i];
			}
			swap(rflbCenter, 1, 7, 3, 5, 0);
			swap(rflbCenter, 0, 6, 2, 4, 0);
			break;
		case 1: //x2
			swap(tCenter, 0, 4);
			swap(tCenter, 1, 5);
			swap(tCenter, 2, 6);
			swap(tCenter, 3, 7);
			swap(xCenter, 0, 4);
			swap(xCenter, 1, 5);
			swap(xCenter, 2, 6);
			swap(xCenter, 3, 7);
			swap(rflbCenter, 0, 3);
			swap(rflbCenter, 4, 5);
			swap(rflbCenter, 1, 2);
			swap(rflbCenter, 6, 7);
			for (int i = 0; i < 8; i++) {
				tCenter[i] = -1 - tCenter[i];
				xCenter[i] = -1 - xCenter[i];
			}
			for (int i = 0; i < 4; i++) {
				rflbCenter[i] = -1 - rflbCenter[i];
			}
			break;
		case 2: //lr mirror
			swap(tCenter, 1, 3);
			swap(tCenter, 5, 7);
			swap(xCenter, 0, 1);
			swap(xCenter, 2, 3);
			swap(xCenter, 4, 5);
			swap(xCenter, 6, 7);
			swap(rflbCenter, 0, 1);
			swap(rflbCenter, 2, 3);
			swap(rflbCenter, 4, 7);
			swap(rflbCenter, 5, 6);
			for (int i = 4; i < 8; i++) {
				rflbCenter[i] = -1 - rflbCenter[i];
			}
			break;
		}
	}
}