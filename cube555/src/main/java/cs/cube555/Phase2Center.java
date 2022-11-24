package cs.cube555;

import static cs.cube555.Util.*;
import static cs.cube555.Phase2Search.VALID_MOVES;

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

class Phase2Center {

	static int[] eParityDiff = new int[] {
	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	    0, 1, 0, 1, 0, 0, 1, 0, 1, 0
	};

	int[] tCenter = new int[16];
	int[] xCenter = new int[16];
	int eParity = 0;

	void setTCenter(int idx) {
		setComb(tCenter, idx, 8);
	}

	int getTCenter() {
		return getComb(tCenter, 8);
	}

	void setXCenter(int idx) {
		setComb(xCenter, idx, 8);
	}

	int getXCenter() {
		return getComb(xCenter, 8);
	}

	void setEParity(int idx) {
		eParity = idx;
	}

	int getEParity() {
		return eParity;
	}

	void doMove(int move) {
		eParity ^= eParityDiff[move];
		move = VALID_MOVES[move];
		int axis = move / 3;
		int pow = move % 3;
		switch (axis) {
		case 6: //Uw
			swap(xCenter, 8, 12);
			swap(xCenter, 9, 13);
			swap(tCenter, 8, 12);
		case 0: //U
			swap(xCenter, 0, 1, 2, 3, pow);
			swap(tCenter, 0, 1, 2, 3, pow);
			break;
		case 7: //Rw
			swap(xCenter, 1, 15, 5, 9, pow);
			swap(xCenter, 2, 12, 6, 10, pow);
			swap(tCenter, 1, 15, 5, 9, pow);
		case 1: //R
			break;
		case 8: //Fw
			swap(xCenter, 2, 4);
			swap(xCenter, 3, 5);
			swap(tCenter, 2, 4);
		case 2: //F
			swap(xCenter, 8, 9, 10, 11, pow);
			swap(tCenter, 8, 9, 10, 11, pow);
			break;
		case 9: //Dw
			swap(xCenter, 10, 14);
			swap(xCenter, 11, 15);
			swap(tCenter, 10, 14);
		case 3: //D
			swap(xCenter, 4, 5, 6, 7, pow);
			swap(tCenter, 4, 5, 6, 7, pow);
			break;
		case 10: //Lw
			swap(xCenter, 0, 8, 4, 14, pow);
			swap(xCenter, 3, 11, 7, 13, pow);
			swap(tCenter, 3, 11, 7, 13, pow);
		case 4: //L
			break;
		case 11: //Bw
			swap(xCenter, 1, 7);
			swap(xCenter, 0, 6);
			swap(tCenter, 0, 6);
		case 5: //B
			swap(xCenter, 12, 13, 14, 15, pow);
			swap(tCenter, 12, 13, 14, 15, pow);
			break;
		}
	}
}