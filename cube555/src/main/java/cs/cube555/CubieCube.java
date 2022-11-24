package cs.cube555;

import static cs.cube555.Util.*;

/*

Facelet:
						U1	U2	U3	U4	U5
						U6	U7	U8	U9	U10
						U11	U12	U13	U14	U15
						U16	U17	U18	U19	U20
						U21	U22	U23	U24	U25

L1	L2	L3	L4	L5		F1	F2	F3	F4	F5		R1	R2	R3	R4	R5		B1	B2	B3	B4	B5
L6	L7	L8	L9	L10		F6	F7	F8	F9	F10		R6	R7	R8	R9	R10		B6	B7	B8	B9	B10
L11	L12	L13	L14	L15		F11	F12	F13	F14	F15		R11	R12	R13	R14	R15		B11	B12	B13	B14	B15
L16	L17	L18	L19	L20		F16	F17	F18	F19	F20		R16	R17	R18	R19	R20		B16	B17	B18	B19	B20
L21	L22	L23	L24	L25		F21	F22	F23	F24	F25		R21	R22	R23	R24	R25		B21	B22	B23	B24	B25

						D1	D2	D3	D4	D5
						D6	D7	D8	D9	D10
						D11	D12	D13	D14	D15
						D16	D17	D18	D19	D20
						D21	D22	D23	D24	D25

Center:
           0  0  1
           3     1
           3  2  2

20 20 21   8  8  9    16 16 17   12 12  13
23    21   11    9    19    17   15     13
23 22 22   11 10 10   19 18 18   15 14  14

           4  4  5
           7     5
           7  6  6

Edge:
 					13	1
				4			17
				16			5
					0	12
	4	16			0	12			5	17			1	13
9			20	20			11	11			22	22			9
21			8	8			23	23			10	10			21
	19	7			15	3			18	6			14	2
					15	3
				7			18
				19			6
					2	14
 */

class CubieCube {

	// For pretty print
	static int[] PRINT_FACELET = new int[] {
	    U1, U2, U3, U4, U5,
	    U6, U7, U8, U9, U10,
	    U11, U12, U13, U14, U15,
	    U16, U17, U18, U19, U20,
	    U21, U22, U23, U24, U25,
	    L1, L2, L3, L4, L5, F1, F2, F3, F4, F5, R1, R2, R3, R4, R5, B1, B2, B3, B4, B5,
	    L6, L7, L8, L9, L10, F6, F7, F8, F9, F10, R6, R7, R8, R9, R10, B6, B7, B8, B9, B10,
	    L11, L12, L13, L14, L15, F11, F12, F13, F14, F15, R11, R12, R13, R14, R15, B11, B12, B13, B14, B15,
	    L16, L17, L18, L19, L20, F16, F17, F18, F19, F20, R16, R17, R18, R19, R20, B16, B17, B18, B19, B20,
	    L21, L22, L23, L24, L25, F21, F22, F23, F24, F25, R21, R22, R23, R24, R25, B21, B22, B23, B24, B25,
	    D1, D2, D3, D4, D5,
	    D6, D7, D8, D9, D10,
	    D11, D12, D13, D14, D15,
	    D16, D17, D18, D19, D20,
	    D21, D22, D23, D24, D25
	};

	static int[] MAP333_FACELET = new int[] {
	    U1, U3, U5, U11, U13, U15, U21, U23, U25,
	    R1, R3, R5, R11, R13, R15, R21, R23, R25,
	    F1, F3, F5, F11, F13, F15, F21, F23, F25,
	    D1, D3, D5, D11, D13, D15, D21, D23, D25,
	    L1, L3, L5, L11, L13, L15, L21, L23, L25,
	    B1, B3, B5, B11, B13, B15, B21, B23, B25
	};

	static int[] TCENTER = new int[] {
	    U8, U14, U18, U12,
	    D8, D14, D18, D12,
	    F8, F14, F18, F12,
	    B8, B14, B18, B12,
	    R8, R14, R18, R12,
	    L8, L14, L18, L12
	};

	static int[] XCENTER = new int[] {
	    U7, U9, U19, U17,
	    D7, D9, D19, D17,
	    F7, F9, F19, F17,
	    B7, B9, B19, B17,
	    R7, R9, R19, R17,
	    L7, L9, L19, L17
	};

	static int[][] MEDGE = new int[][] {
		{U23, F3}, {U3, B3}, {D23, B23}, {D3, F23},
		{U11, L3}, {U15, R3}, {D15, R23}, {D11, L23},
		{L15, F11}, {L11, B15}, {R15, B11}, {R11, F15}
	};

	static int[][] WEDGE = new int[][] {
		{U22, F2}, {U4, B2}, {D22, B24}, {D4, F24},
		{U6, L2}, {U20, R2}, {D20, R24}, {D6, L24},
		{L20, F16}, {L6, B10}, {R20, B16}, {R6, F10},
		{F4, U24}, {B4, U2}, {B22, D24}, {F22, D2},
		{L4, U16}, {R4, U10}, {R22, D10}, {L22, D16},
		{F6, L10}, {B20, L16}, {B6, R10}, {F20, R16}
	};

	static int[][] CORNER = new int[][] {
		{U25, R1, F5}, {U21, F1, L5}, {U1, L1, B5}, {U5, B1, R5},
		{D5, F25, R21}, {D1, L25, F21}, {D21, B25, L21}, {D25, R25, B21}
	};

	static CubieCube SOLVED = new CubieCube();

	int[] tCenter = new int[24];
	int[] xCenter = new int[24];
	int[] mEdge = new int[12];
	int[] wEdge = new int[24];
	CornerCube corner = new CornerCube();

	CubieCube() {
		for (int i = 0; i < 24; i++) {
			tCenter[i] = TCENTER[i] / 25;
			xCenter[i] = XCENTER[i] / 25;
			wEdge[i] = i;
		}
		for (int i = 0; i < 12; i++) {
			mEdge[i] = i << 1;
		}
	}

	CubieCube(CubieCube cc) {
		copy(cc);
	}

	void copy(CubieCube cc) {
		for (int i = 0; i < 24; i++) {
			tCenter[i] = cc.tCenter[i];
			xCenter[i] = cc.xCenter[i];
			wEdge[i] = cc.wEdge[i];
		}
		for (int i = 0; i < 12; i++) {
			mEdge[i] = cc.mEdge[i];
		}
	}

	static String to333Facelet(String facelet) {
		StringBuffer sb = new StringBuffer();
		for (int i : MAP333_FACELET) {
			sb.append(facelet.charAt(i));
		}
		return sb.toString();
	}

	static String fill333Facelet(String facelet, String facelet333) {
		StringBuffer sb = new StringBuffer(facelet);
		for (int i = 0; i < MAP333_FACELET.length; i++) {
			sb.setCharAt(MAP333_FACELET[i], facelet333.charAt(i));
		}
		return sb.toString();
	}

	int fromFacelet(String facelet) {
		int[] face = new int[150];
		long colorCnt = 0;
		try {
			String colors = new String(
			    new char[] {
			        facelet.charAt(U13), facelet.charAt(R13), facelet.charAt(F13),
			        facelet.charAt(D13), facelet.charAt(L13), facelet.charAt(B13)
			    }
			);
			for (int i = 0; i < 150; i++) {
				face[i] = colors.indexOf(facelet.charAt(i));
				if (face[i] == -1) {
					return -1;
				}
				colorCnt += 1L << face[i] * 8;
			}
		} catch (Exception e) {
			return -1;
		}
		int tCenterCnt = 0;
		int xCenterCnt = 0;
		for (int i = 0; i < 24; i++) {
			tCenter[i] = face[TCENTER[i]];
			xCenter[i] = face[XCENTER[i]];
			tCenterCnt += 1 << (tCenter[i] << 2);
			xCenterCnt += 1 << (xCenter[i] << 2);
		}
		int mEdgeCnt = 0;
		int mEdgeChk = 0;
		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 12; j++) {
				if (face[MEDGE[i][0]] == MEDGE[j][0] / 25
				        && face[MEDGE[i][1]] == MEDGE[j][1] / 25
				        || face[MEDGE[i][0]] == MEDGE[j][1] / 25
				        && face[MEDGE[i][1]] == MEDGE[j][0] / 25
				   ) {
					int ori = face[MEDGE[i][0]] == MEDGE[j][0] / 25 ? 0 : 1;
					mEdge[i] = j << 1 | ori;
					mEdgeCnt |= 1 << j;
					mEdgeChk ^= ori;
					break;
				}
			}
		}
		int wEdgeCnt = 0;
		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 24; j++) {
				if (face[WEDGE[i][0]] == WEDGE[j][0] / 25
				        && face[WEDGE[i][1]] == WEDGE[j][1] / 25) {
					wEdge[i] = j;
					wEdgeCnt |= 1 << j;
					break;
				}
			}
		}
		int ori = 0;
		int cornerCnt = 0;
		int cornerChk = 0;
		for (int i = 0; i < 8; i++) {
			for (ori = 0; ori < 3; ori++) {
				if (face[CORNER[i][ori]] == 0 || face[CORNER[i][ori]] == 3) {
					break;
				}
			}
			int col1 = face[CORNER[i][(ori + 1) % 3]];
			int col2 = face[CORNER[i][(ori + 2) % 3]];
			for (int j = 0; j < 8; j++) {
				if (col1 == CORNER[j][1] / 25 && col2 == CORNER[j][2] / 25) {
					corner.cp[i] = j;
					corner.co[i] = ori;
					cornerChk += ori;
					cornerCnt |= 1 << j;
					break;
				}
			}
		}
		int[] ep = new int[12];
		for (int i = 0; i < 12; i++) {
			ep[i] = mEdge[i] >> 1;
		}
		if (colorCnt != 0x191919191919L) {
			return -1;
		} else if (tCenterCnt != 0x444444) {
			return -2;
		} else if (xCenterCnt != 0x444444) {
			return -3;
		} else if (mEdgeCnt != 0xfff) {
			return -4;
		} else if (wEdgeCnt != 0xffffff) {
			return -5;
		} else if (cornerCnt != 0xff) {
			return -6;
		} else if (mEdgeChk != 0) {
			return -7;
		} else if (cornerChk % 3 != 0) {
			return -8;
		} else if (getParity(ep) != getParity(corner.cp)) {
			return -9;
		}
		return 0;
	}

	String toFacelet() {
		char[] face = new char[150];
		String colors = "URFDLB";
		for (int i = 0; i < 150; i++) {
			face[i] = i % 25 == 12 ? colors.charAt(i / 25) : '-';
		}
		for (int i = 0; i < 24; i++) {
			face[TCENTER[i]] = colors.charAt(tCenter[i]);
			face[XCENTER[i]] = colors.charAt(xCenter[i]);
			for (int j = 0; j < 2; j++) {
				face[WEDGE[i][j]] = colors.charAt(WEDGE[wEdge[i]][j] / 25);
			}
		}
		for (int i = 0; i < 12; i++) {
			int perm = mEdge[i] >> 1;
			int ori = mEdge[i] & 1;// Orientation of this cubie
			for (int j = 0; j < 2; j++) {
				face[MEDGE[i][j ^ ori]] = colors.charAt(MEDGE[perm][j] / 25);
			}
		}
		for (int i = 0; i < 8; i++) {
			int j = corner.cp[i];
			int ori = corner.co[i];
			for (int n = 0; n < 3; n++) {
				face[CORNER[i][(n + ori) % 3]] = colors.charAt(CORNER[j][n] / 25);
			}
		}
		return new String(face);
	}

	public String toString() {
		String facelet = toFacelet();
		String colors = "URFDLB-";
		String[] controls = new String[] {
		    "\033[37m", "\033[31m", "\033[32m", "\033[33m", "\033[35m", "\033[34m", "\033[30m"
		};
		String[] arr = new String[150];
		for (int i = 0; i < 150; i++) {
			char val = facelet.charAt(PRINT_FACELET[i]);
			arr[i] = controls[colors.indexOf(val)] + " " + val + "\033[0m";
		}
		return String.format(
		           "           %s%s%s%s%s\n" +
		           "           %s%s%s%s%s\n" +
		           "           %s%s%s%s%s\n" +
		           "           %s%s%s%s%s\n" +
		           "           %s%s%s%s%s\n" +
		           "%s%s%s%s%s %s%s%s%s%s %s%s%s%s%s %s%s%s%s%s\n" +
		           "%s%s%s%s%s %s%s%s%s%s %s%s%s%s%s %s%s%s%s%s\n" +
		           "%s%s%s%s%s %s%s%s%s%s %s%s%s%s%s %s%s%s%s%s\n" +
		           "%s%s%s%s%s %s%s%s%s%s %s%s%s%s%s %s%s%s%s%s\n" +
		           "%s%s%s%s%s %s%s%s%s%s %s%s%s%s%s %s%s%s%s%s\n" +
		           "           %s%s%s%s%s\n" +
		           "           %s%s%s%s%s\n" +
		           "           %s%s%s%s%s\n" +
		           "           %s%s%s%s%s\n" +
		           "           %s%s%s%s%s\033[0m\n",
		           (Object []) arr);
	}

	void doCornerMove(int... moves) {
		for (int move : moves) {
			corner.doMove(move % 18);
		}
	}

	void doMove(int... moves) {
		for (int move : moves) {
			int pow = move % 3;
			switch (move) {
			case ux1:
			case ux2:
			case ux3:
				swap(xCenter, 8, 20, 12, 16, pow);
				swap(xCenter, 9, 21, 13, 17, pow);
				swap(tCenter, 8, 20, 12, 16, pow);
				swap(wEdge, 9, 22, 11, 20, pow);
			case Ux1:
			case Ux2:
			case Ux3:
				swap(xCenter, 0, 1, 2, 3, pow);
				swap(tCenter, 0, 1, 2, 3, pow);
				swap(mEdge, 0, 4, 1, 5, pow);
				swap(wEdge, 0, 4, 1, 5, pow);
				swap(wEdge, 12, 16, 13, 17, pow);
				break;
			case rx1:
			case rx2:
			case rx3:
				swap(xCenter, 1, 15, 5, 9, pow);
				swap(xCenter, 2, 12, 6, 10, pow);
				swap(tCenter, 1, 15, 5, 9, pow);
				swap(wEdge, 1, 14, 3, 12, pow);
			case Rx1:
			case Rx2:
			case Rx3:
				swap(xCenter, 16, 17, 18, 19, pow);
				swap(tCenter, 16, 17, 18, 19, pow);
				swap(mEdge, 5, 10, 6, 11, pow, true);
				swap(wEdge, 5, 22, 6, 23, pow);
				swap(wEdge, 17, 10, 18, 11, pow);
				break;
			case fx1:
			case fx2:
			case fx3:
				swap(xCenter, 2, 19, 4, 21, pow);
				swap(xCenter, 3, 16, 5, 22, pow);
				swap(tCenter, 2, 19, 4, 21, pow);
				swap(wEdge, 5, 18, 7, 16, pow);
			case Fx1:
			case Fx2:
			case Fx3:
				swap(xCenter, 8, 9, 10, 11, pow);
				swap(tCenter, 8, 9, 10, 11, pow);
				swap(mEdge, 0, 11, 3, 8, pow);
				swap(wEdge, 0, 11, 3, 8, pow);
				swap(wEdge, 12, 23, 15, 20, pow);
				break;
			case dx1:
			case dx2:
			case dx3:
				swap(xCenter, 10, 18, 14, 22, pow);
				swap(xCenter, 11, 19, 15, 23, pow);
				swap(tCenter, 10, 18, 14, 22, pow);
				swap(wEdge, 8, 23, 10, 21, pow);
			case Dx1:
			case Dx2:
			case Dx3:
				swap(xCenter, 4, 5, 6, 7, pow);
				swap(tCenter, 4, 5, 6, 7, pow);
				swap(mEdge, 2, 7, 3, 6, pow);
				swap(wEdge, 2, 7, 3, 6, pow);
				swap(wEdge, 14, 19, 15, 18, pow);
				break;
			case lx1:
			case lx2:
			case lx3:
				swap(xCenter, 0, 8, 4, 14, pow);
				swap(xCenter, 3, 11, 7, 13, pow);
				swap(tCenter, 3, 11, 7, 13, pow);
				swap(wEdge, 0, 15, 2, 13, pow);
			case Lx1:
			case Lx2:
			case Lx3:
				swap(xCenter, 20, 21, 22, 23, pow);
				swap(tCenter, 20, 21, 22, 23, pow);
				swap(mEdge, 4, 8, 7, 9, pow, true);
				swap(wEdge, 4, 20, 7, 21, pow);
				swap(wEdge, 16, 8, 19, 9, pow);
				break;
			case bx1:
			case bx2:
			case bx3:
				swap(xCenter, 1, 20, 7, 18, pow);
				swap(xCenter, 0, 23, 6, 17, pow);
				swap(tCenter, 0, 23, 6, 17, pow);
				swap(wEdge, 4, 19, 6, 17, pow);
			case Bx1:
			case Bx2:
			case Bx3:
				swap(xCenter, 12, 13, 14, 15, pow);
				swap(tCenter, 12, 13, 14, 15, pow);
				swap(mEdge, 1, 9, 2, 10, pow);
				swap(wEdge, 1, 9, 2, 10, pow);
				swap(wEdge, 13, 21, 14, 22, pow);
				break;
			}
		}
	}

	void doConj(int idx) {
		CubieCube a = new CubieCube(this);
		CubieCube sinv = CubeSym[SymMultInv[0][idx]];
		CubieCube s = CubeSym[idx];
		for (int i = 0; i < 12; i++) {
			this.mEdge[i] = sinv.mEdge[a.mEdge[s.mEdge[i] >> 1] >> 1]
			                ^ (a.mEdge[s.mEdge[i] >> 1] & 1)
			                ^ (s.mEdge[i] & 1);
		}
		for (int i = 0; i < 24; i++) {
			this.tCenter[i] = SOLVED.tCenter[sinv.tCenter[COLOR_TO_CENTER[a.tCenter[s.tCenter[i]]]]];
			this.xCenter[i] = SOLVED.xCenter[sinv.xCenter[COLOR_TO_CENTER[a.xCenter[s.xCenter[i]]]]];
			this.wEdge[i] = sinv.wEdge[a.wEdge[s.wEdge[i]]];
		}
	}

	static CubieCube[] CubeSym = new CubieCube[48];
	static int[][] SymMult = new int[48][48];
	static int[][] SymMultInv = new int[48][48];
	static int[][] SymMove = new int[48][36];

	static void CubeMult(CubieCube a, CubieCube b, CubieCube prod) {
		for (int i = 0; i < 12; i++) {
			prod.mEdge[i] = a.mEdge[b.mEdge[i] >> 1] ^ (b.mEdge[i] & 1);
		}
		for (int i = 0; i < 24; i++) {
			prod.tCenter[i] = a.tCenter[b.tCenter[i]];
			prod.xCenter[i] = a.xCenter[b.xCenter[i]];
			prod.wEdge[i] = a.wEdge[b.wEdge[i]];
		}
	}

	static int[] COLOR_TO_CENTER = new int[] {0, 16, 8, 4, 20, 12};

	static void init() {
		CornerCube.initMove();
		CubieCube c = new CubieCube();
		for (int i = 0; i < 24; i++) {
			c.tCenter[i] = i;
			c.xCenter[i] = i;
		}
		for (int i = 0; i < 48; i++) {
			CubeSym[i] = new CubieCube(c);

			// x
			c.doMove(rx1, lx3);
			swap(c.tCenter, 0, 14, 4, 8, 0);
			swap(c.tCenter, 2, 12, 6, 10, 0);
			swap(c.mEdge, 0, 1, 2, 3, 0, true);

			if ((i & 0x3) == 0x3) {
				// y2
				c.doMove(ux2, dx2);
				swap(c.tCenter, 9, 21, 13, 17, 1);
				swap(c.tCenter, 11, 23, 15, 19, 1);
				swap(c.mEdge, 8, 9, 10, 11, 1, true);
			}
			if ((i & 0x7) == 0x7) {
				// lr mirror
				swap(c.tCenter, 1, 3);
				swap(c.tCenter, 5, 7);
				swap(c.tCenter, 9, 11);
				swap(c.tCenter, 13, 15);
				swap(c.tCenter, 16, 20);
				swap(c.tCenter, 17, 23);
				swap(c.tCenter, 18, 22);
				swap(c.tCenter, 19, 21);
				swap(c.xCenter, 0, 1);
				swap(c.xCenter, 2, 3);
				swap(c.xCenter, 4, 5);
				swap(c.xCenter, 6, 7);
				swap(c.xCenter, 8, 9);
				swap(c.xCenter, 10, 11);
				swap(c.xCenter, 12, 13);
				swap(c.xCenter, 14, 15);
				swap(c.xCenter, 16, 21);
				swap(c.xCenter, 17, 20);
				swap(c.xCenter, 18, 23);
				swap(c.xCenter, 19, 22);
				swap(c.wEdge, 0, 12);
				swap(c.wEdge, 1, 13);
				swap(c.wEdge, 2, 14);
				swap(c.wEdge, 3, 15);
				swap(c.wEdge, 4, 17);
				swap(c.wEdge, 5, 16);
				swap(c.wEdge, 6, 19);
				swap(c.wEdge, 7, 18);
				swap(c.wEdge, 8, 23);
				swap(c.wEdge, 9, 22);
				swap(c.wEdge, 10, 21);
				swap(c.wEdge, 11, 20);
				swap(c.mEdge, 4, 5);
				swap(c.mEdge, 6, 7);
				swap(c.mEdge, 8, 11);
				swap(c.mEdge, 9, 10);
			}
			if ((i & 0xf) == 0xf) {
				// URF -> RFU <=> x y
				c.doMove(rx1, lx3);
				swap(c.tCenter, 0, 14, 4, 8, 0);
				swap(c.tCenter, 2, 12, 6, 10, 0);
				swap(c.mEdge, 0, 1, 2, 3, 0, true);
				c.doMove(ux1, dx3);
				swap(c.tCenter, 9, 21, 13, 17, 0);
				swap(c.tCenter, 11, 23, 15, 19, 0);
				swap(c.mEdge, 8, 9, 10, 11, 0, true);
			}
		}
		for (int i = 0; i < 48; i++) {
			for (int j = 0; j < 48; j++) {
				CubeMult(CubeSym[i], CubeSym[j], c);
				for (int k = 0; k < 48; k++) {
					if (java.util.Arrays.equals(CubeSym[k].wEdge, c.wEdge)) {
						SymMult[i][j] = k;
						SymMultInv[k][j] = i;
						break;
					}
				}
			}
		}

		for (int move = 0; move < 36; move++) {
			for (int s = 0; s < 48; s++) {
				c = new CubieCube();
				c.doMove(move);
				c.doConj(SymMultInv[0][s]);
				for (int move2 = 0; move2 < 36; move2++) {
					CubieCube d = new CubieCube();
					d.doMove(move2);
					if (java.util.Arrays.equals(c.wEdge, d.wEdge)) {
						SymMove[s][move] = move2;
						break;
					}
				}
			}
		}
	}

	static int[][] getSymMove(int[] moves, int nsym) {
		int[] symList = new int[nsym];
		for (int i = 0; i < nsym; i++) {
			symList[i] = i;
		}
		return getSymMove(moves, symList);
	}

	static int[][] getSymMove(int[] moves, int[] symList) {
		int[][] ret = new int[symList.length][moves.length];
		for (int s = 0; s < symList.length; s++) {
			for (int m = 0; m < moves.length; m++) {
				ret[s][m] = indexOf(moves, SymMove[symList[s]][moves[m]]);
			}
		}
		return ret;
	}

	static class CornerCube {

		private static CornerCube[] moveCube = new CornerCube[18];

		int[] cp = {0, 1, 2, 3, 4, 5, 6, 7};
		int[] co = {0, 0, 0, 0, 0, 0, 0, 0};

		CornerCube() {}

		CornerCube(int cperm, int twist) {
			setPerm(cp, cperm);
			int twst = 0;
			for (int i = 6; i >= 0; i--) {
				twst += co[i] = twist % 3;
				twist /= 3;
			}
			co[7] = (15 - twst) % 3;
		}

		CornerCube(CornerCube c) {
			copy(c);
		}

		void copy(CornerCube c) {
			for (int i = 0; i < 8; i++) {
				this.cp[i] = c.cp[i];
				this.co[i] = c.co[i];
			}
		}

		static void CornMult(CornerCube a, CornerCube b, CornerCube prod) {
			for (int corn = 0; corn < 8; corn++) {
				prod.cp[corn] = a.cp[b.cp[corn]];
				prod.co[corn] = (a.co[b.cp[corn]] + b.co[corn]) % 3;
			}
		}

		void doMove(int move) {
			CornerCube cc = new CornerCube();
			CornMult(this, moveCube[move], cc);
			copy(cc);
		}

		static void initMove() {
			moveCube[0] = new CornerCube(15120, 0);
			moveCube[3] = new CornerCube(21021, 1494);
			moveCube[6] = new CornerCube(8064, 1236);
			moveCube[9] = new CornerCube(9, 0);
			moveCube[12] = new CornerCube(1230, 412);
			moveCube[15] = new CornerCube(224, 137);
			for (int a = 0; a < 18; a += 3) {
				for (int p = 0; p < 2; p++) {
					moveCube[a + p + 1] = new CornerCube();
					CornMult(moveCube[a + p], moveCube[a], moveCube[a + p + 1]);
				}
			}
		}
	}
}