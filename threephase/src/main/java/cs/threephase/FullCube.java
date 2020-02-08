/*
Edge Cubies:
					14	2
				1			15
				13			3
					0	12
	1	13			0	12			3	15			2	14
9			20	20			11	11			22	22			9
21			8	8			23	23			10	10			21
	17	5			18	6			19	7			16	4
					18	6
				5			19
				17			7
					4	16

Center Cubies:
			0	1
			3	2

20	21		8	9		16	17		12	13
23	22		11	10		19	18		15	14

			4	5
			7	6
			|************|
			|*U1**U2**U3*|
			|************|
			|*U4**U5**U6*|
			|************|
			|*U7**U8**U9*|
			|************|
************|************|************|************|
*L1**L2**L3*|*F1**F2**F3*|*R1**R2**F3*|*B1**B2**B3*|
************|************|************|************|
*L4**L5**L6*|*F4**F5**F6*|*R4**R5**R6*|*B4**B5**B6*|
************|************|************|************|
*L7**L8**L9*|*F7**F8**F9*|*R7**R8**R9*|*B7**B8**B9*|
************|************|************|************|
			|************|
			|*D1**D2**D3*|
			|************|
			|*D4**D5**D6*|
			|************|
			|*D7**D8**D9*|
			|************|
				|****************|
				|*u0**u1**u2**u3*|
				|****************|
				|*u4**u5**u6**u7*|
				|****************|
				|*u8**u9**ua**ub*|
				|****************|
				|*uc**ud**ue**uf*|
				|****************|
****************|****************|****************|****************|
*l0**l1**l2**l3*|*f0**f1**f2**f3*|*r0**r1**r2**r3*|*b0**b1**b2**b3*|
****************|****************|****************|****************|
*l4**l5**l6**l7*|*f4**f5**f6**f7*|*r4**r5**r6**r7*|*b4**b5**b6**b7*|
****************|****************|****************|****************|
*l8**l9**la**lb*|*f8**f9**fa**fb*|*r8**r9**ra**rb*|*b8**b9**ba**bb*|
****************|****************|****************|****************|
*lc**ld**le**lf*|*fc**fd**fe**ff*|*rc**rd**re**rf*|*bc**bd**be**bf*|
****************|****************|****************|****************|
				|****************|
				|*d0**d1**d2**d3*|
				|****************|
				|*d4**d5**d6**d7*|
				|****************|
				|*d8**d9**da**db*|
				|****************|
				|*dc**dd**de**df*|
				|****************|
	 */


package cs.threephase;

import java.util.*;
import static cs.threephase.Moves.*;
import static cs.threephase.Util.*;
import static cs.threephase.Center1.symmove;
import static cs.threephase.Center1.symmult;
import static cs.threephase.Center1.syminv;

public class FullCube implements Comparable<FullCube> {

	static final byte[] centerFacelet = {u5, u6, ua, u9, d5, d6, da, d9, f5, f6, fa, f9, b5, b6, ba, b9, r5, r6, ra, r9, l5, l6, la, l9};
	static final byte[][] edgeFacelet = {
		{ud, f1}, {u4, l1}, {u2, b1}, {ub, r1}, {dd, be}, {d4, le}, {d2, fe}, {db, re}, {lb, f8}, {l4, b7}, {rb, b8}, {r4, f7},
		{f2, ue}, {l2, u8}, {b2, u1}, {r2, u7}, {bd, de}, {ld, d8}, {fd, d1}, {rd, d7}, {f4, l7}, {bb, l8}, {b4, r7}, {fb, r8}};
	static final byte[][] cornerFacelet = { { uf, r0, f3 }, { uc, f0, l3 }, { u0, l0, b3 }, { u3, b0, r3 },
		{ d3, ff, rc }, { d0, lf, fc }, { dc, bf, lc }, { df, rf, bc } };


	public FullCube(byte[] f) {
		edge = new EdgeCube();
		center = new CenterCube();
		corner = new CornerCube();
		for (int i=0; i<24; i++) {
			center.ct[i] = f[centerFacelet[i]];
		}
		for (int i=0; i<24; i++) {
			for (byte j=0; j<24; j++) {
				if (f[edgeFacelet[i][0]] == edgeFacelet[j][0]/16 && f[edgeFacelet[i][1]] == edgeFacelet[j][1]/16) {
					edge.ep[i] = j;
				}
			}
		}
		byte col1, col2, ori;
		for (byte i=0; i<8; i++) {
			// get the colors of the cubie at corner i, starting with U/D
			for (ori = 0; ori < 3; ori++)
				if (f[cornerFacelet[i][ori]] == u0/16 || f[cornerFacelet[i][ori]] == d0/16)
					break;
			col1 = f[cornerFacelet[i][(ori + 1) % 3]];
			col2 = f[cornerFacelet[i][(ori + 2) % 3]];

			for (byte j=0; j<8; j++) {
				if (col1 == cornerFacelet[j][1]/16 && col2 == cornerFacelet[j][2]/16) {
					// in cornerposition i we have cornercubie j
					corner.cp[i] = j;
					corner.co[i] = (byte) (ori % 3);
					break;
				}
			}
		}
	}

	void toFacelet(byte[] f) {
		for (int i=0; i<24; i++) {
			f[centerFacelet[i]] = center.ct[i];
		}
		for (int i=0; i<24; i++) {
			f[edgeFacelet[i][0]] = (byte) (edgeFacelet[edge.ep[i]][0]/16);
			f[edgeFacelet[i][1]] = (byte) (edgeFacelet[edge.ep[i]][1]/16);
		}
		for (byte c=0; c<8; c++) {
			byte j = corner.cp[c];
			byte ori = corner.co[c];
			for (byte n=0; n<3; n++)
				f[cornerFacelet[c][(n + ori) % 3]] = (byte) (cornerFacelet[j][n]/16);
		}
	}

	@Override
	public String toString() {
		getEdge();
		getCenter();
		getCorner();

		byte[] f = new byte[96];
		StringBuffer sb = new StringBuffer();
		toFacelet(f);
		for (int i=0; i<96; i++) {
			sb.append("URFDLB".charAt(f[i]));
			if (i % 4 == 3) {
				sb.append('\n');
			}
			if (i % 16 == 15) {
				sb.append('\n');
			}
		}
		return sb.toString();
	}

	public static class ValueComparator implements Comparator<FullCube> {
		public int compare(FullCube c1, FullCube c2) {
			return c2.value - c1.value;
		}
	}

	private EdgeCube edge;
	private CenterCube center;
	private CornerCube corner;

	int value = 0;
	boolean add1 = false;
	int length1 = 0;
	int length2 = 0;
	int length3 = 0;

	@Override
	public int compareTo(FullCube c) {
		return value - c.value;
	}

	public FullCube() {
		edge = new EdgeCube();
		center = new CenterCube();
		corner = new CornerCube();
	}

	public FullCube(FullCube c) {
		this();
		copy(c);
	}

	public FullCube(Random r) {
		edge = new EdgeCube(r);
		center = new CenterCube(r);
		corner = new CornerCube(r);
	}

	public FullCube(int[] moveseq) {
		this();
		for (int m : moveseq) {
			doMove(m);
		}
	}

	public void copy(FullCube c) {
		edge.copy(c.edge);
		center.copy(c.center);
		corner.copy(c.corner);

		this.value = c.value;
		this.add1 = c.add1;
		this.length1 = c.length1;
		this.length2 = c.length2;
		this.length3 = c.length3;

		this.sym = c.sym;

		for (int i=0; i<60; i++) {
			this.moveBuffer[i] = c.moveBuffer[i];
		}
		this.moveLength = c.moveLength;
		this.edgeAvail = c.edgeAvail;
		this.centerAvail = c.centerAvail;
		this.cornerAvail = c.cornerAvail;
	}

	public boolean checkEdge() {
		return getEdge().checkEdge();
	}

	public String getMoveString(boolean inverse, boolean rotation) {
		int[] fixedMoves = new int[moveLength - (add1 ? 2 : 0)];
		int idx = 0;
		for (int i=0; i<length1; i++) {
			fixedMoves[idx++] = moveBuffer[i];
		}
		int sym = this.sym;
		for (int i=length1 + (add1 ? 2 : 0); i<moveLength; i++) {
			if (symmove[sym][moveBuffer[i]] >= dx1) {
				fixedMoves[idx++] = symmove[sym][moveBuffer[i]] - 9;
				int rot = move2rot[symmove[sym][moveBuffer[i]] - dx1];
				sym = symmult[sym][rot];
			} else {
				fixedMoves[idx++] = symmove[sym][moveBuffer[i]];
			}
		}
		int finishSym = symmult[syminv[sym]][Center1.getSolvedSym(getCenter())];

		StringBuffer sb = new StringBuffer();
		sym = finishSym;
		if (inverse) {
			for (int i=idx-1; i>=0; i--) {
				int move = fixedMoves[i];
				move = move / 3 * 3 + (2 - move % 3);
				if (symmove[sym][move] >= dx1) {
					sb.append(move2str[symmove[sym][move] - 9]).append(' ');
					int rot = move2rot[symmove[sym][move] - dx1];
					sym = symmult[sym][rot];
				} else {
					sb.append(move2str[symmove[sym][move]]).append(' ');
				}
			}
			if (rotation) {
				sb.append(Center1.rot2str[syminv[sym]] + " ");//cube rotation after solution. for wca scramble, it should be omitted.
			}
		} else {
			for (int i=0; i<idx; i++) {
				sb.append(move2str[fixedMoves[i]]).append(' ');
			}
			if (rotation) {
				sb.append(Center1.rot2str[finishSym]);//cube rotation after solution.
			}
		}
		return sb.toString();
	}

	private static int[] move2rot = {35, 1, 34, 2, 4, 6, 22, 5, 19};

	String to333Facelet() {
		char[] ret = new char[54];
		getEdge().fill333Facelet(ret);
		getCenter().fill333Facelet(ret);
		getCorner().fill333Facelet(ret);
		return new String(ret);
	}

	byte[] moveBuffer = new byte[60];
	private int moveLength = 0;
	private int edgeAvail = 0;
	private int centerAvail = 0;
	private int cornerAvail = 0;

	int sym = 0;

	void move(int m) {
		moveBuffer[moveLength++] = (byte)m;
		return;
	}

	void doMove(int m) {
		getEdge().move(m);
		getCenter().move(m);
		getCorner().move(m % 18);
	}

	EdgeCube getEdge() {
		while (edgeAvail < moveLength) {
			edge.move(moveBuffer[edgeAvail++]);
		}
		return edge;
	}

	CenterCube getCenter() {
		while (centerAvail < moveLength) {
			center.move(moveBuffer[centerAvail++]);
		}
		return center;
	}

	CornerCube getCorner() {
		while (cornerAvail < moveLength) {
			corner.move(moveBuffer[cornerAvail++] % 18);
		}
		return corner;
	}
}
