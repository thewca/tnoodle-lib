package cs.cube555;

import static cs.cube555.Util.*;

/*
           0  0  1
           3     1
           3  2  2

20 20 21   8  8  9    16 16 17   12 12  13
23    21   11    9    19    17   15     13
23 22 22   11 10 10   19 18 18   15 14  14

           4  4  5
           7     5
           7  6  6
*/

class Phase1Center {

	int[] tCenter = new int[24];
	int[] xCenter = new int[24];

	Phase1Center() {
		setTCenter(0);
		setXCenter(0);
	}

	void setTCenter(int idx) {
		setComb(tCenter, 735470 - idx, 8);
	}

	int getTCenter() {
		return 735470 - getComb(tCenter, 8);
	}

	void setXCenter(int idx) {
		setComb(xCenter, 735470 - idx, 8);
	}

	int getXCenter() {
		return 735470 - getComb(xCenter, 8);
	}

	void doMove(int move) {
		int pow = move % 3;
		switch (move) {
		case ux1:
		case ux2:
		case ux3:
			swap(xCenter, 8, 20, 12, 16, pow);
			swap(xCenter, 9, 21, 13, 17, pow);
			swap(tCenter, 8, 20, 12, 16, pow);
		case Ux1:
		case Ux2:
		case Ux3:
			swap(xCenter, 0, 1, 2, 3, pow);
			swap(tCenter, 0, 1, 2, 3, pow);
			break;
		case rx1:
		case rx2:
		case rx3:
			swap(xCenter, 1, 15, 5, 9, pow);
			swap(xCenter, 2, 12, 6, 10, pow);
			swap(tCenter, 1, 15, 5, 9, pow);
		case Rx1:
		case Rx2:
		case Rx3:
			swap(xCenter, 16, 17, 18, 19, pow);
			swap(tCenter, 16, 17, 18, 19, pow);
			break;
		case fx1:
		case fx2:
		case fx3:
			swap(xCenter, 2, 19, 4, 21, pow);
			swap(xCenter, 3, 16, 5, 22, pow);
			swap(tCenter, 2, 19, 4, 21, pow);
		case Fx1:
		case Fx2:
		case Fx3:
			swap(xCenter, 8, 9, 10, 11, pow);
			swap(tCenter, 8, 9, 10, 11, pow);
			break;
		case dx1:
		case dx2:
		case dx3:
			swap(xCenter, 10, 18, 14, 22, pow);
			swap(xCenter, 11, 19, 15, 23, pow);
			swap(tCenter, 10, 18, 14, 22, pow);
		case Dx1:
		case Dx2:
		case Dx3:
			swap(xCenter, 4, 5, 6, 7, pow);
			swap(tCenter, 4, 5, 6, 7, pow);
			break;
		case lx1:
		case lx2:
		case lx3:
			swap(xCenter, 0, 8, 4, 14, pow);
			swap(xCenter, 3, 11, 7, 13, pow);
			swap(tCenter, 3, 11, 7, 13, pow);
		case Lx1:
		case Lx2:
		case Lx3:
			swap(xCenter, 20, 21, 22, 23, pow);
			swap(tCenter, 20, 21, 22, 23, pow);
			break;
		case bx1:
		case bx2:
		case bx3:
			swap(xCenter, 1, 20, 7, 18, pow);
			swap(xCenter, 0, 23, 6, 17, pow);
			swap(tCenter, 0, 23, 6, 17, pow);
		case Bx1:
		case Bx2:
		case Bx3:
			swap(xCenter, 12, 13, 14, 15, pow);
			swap(tCenter, 12, 13, 14, 15, pow);
			break;
		}
	}

	void doConj(int conj) {
		switch (conj) {
		case 0: //x
			doMove(rx1);
			doMove(lx3);
			swap(tCenter, 0, 14, 4, 8, 0);
			swap(tCenter, 2, 12, 6, 10, 0);

			break;
		case 1: //y2
			doMove(ux2);
			doMove(dx2);
			swap(tCenter, 9, 21, 13, 17, 1);
			swap(tCenter, 11, 23, 15, 19, 1);
			break;
		case 2: //lr mirror
			swap(tCenter, 1, 3);
			swap(tCenter, 5, 7);
			swap(tCenter, 9, 11);
			swap(tCenter, 13, 15);
			swap(tCenter, 16, 20);
			swap(tCenter, 17, 23);
			swap(tCenter, 18, 22);
			swap(tCenter, 19, 21);
			swap(xCenter, 0, 1);
			swap(xCenter, 2, 3);
			swap(xCenter, 4, 5);
			swap(xCenter, 6, 7);
			swap(xCenter, 8, 9);
			swap(xCenter, 10, 11);
			swap(xCenter, 12, 13);
			swap(xCenter, 14, 15);
			swap(xCenter, 16, 21);
			swap(xCenter, 17, 20);
			swap(xCenter, 18, 23);
			swap(xCenter, 19, 22);
		}
	}
}