package cs.threephase;
import java.util.*;

class Moves {
	static final byte U1 = 0;
	static final byte U2 = 1;
	static final byte U3 = 2;
	static final byte U4 = 3;
	static final byte U5 = 4;
	static final byte U6 = 5;
	static final byte U7 = 6;
	static final byte U8 = 7;
	static final byte U9 = 8;
	static final byte R1 = 9;
	static final byte R2 = 10;
	static final byte R3 = 11;
	static final byte R4 = 12;
	static final byte R5 = 13;
	static final byte R6 = 14;
	static final byte R7 = 15;
	static final byte R8 = 16;
	static final byte R9 = 17;
	static final byte F1 = 18;
	static final byte F2 = 19;
	static final byte F3 = 20;
	static final byte F4 = 21;
	static final byte F5 = 22;
	static final byte F6 = 23;
	static final byte F7 = 24;
	static final byte F8 = 25;
	static final byte F9 = 26;
	static final byte D1 = 27;
	static final byte D2 = 28;
	static final byte D3 = 29;
	static final byte D4 = 30;
	static final byte D5 = 31;
	static final byte D6 = 32;
	static final byte D7 = 33;
	static final byte D8 = 34;
	static final byte D9 = 35;
	static final byte L1 = 36;
	static final byte L2 = 37;
	static final byte L3 = 38;
	static final byte L4 = 39;
	static final byte L5 = 40;
	static final byte L6 = 41;
	static final byte L7 = 42;
	static final byte L8 = 43;
	static final byte L9 = 44;
	static final byte B1 = 45;
	static final byte B2 = 46;
	static final byte B3 = 47;
	static final byte B4 = 48;
	static final byte B5 = 49;
	static final byte B6 = 50;
	static final byte B7 = 51;
	static final byte B8 = 52;
	static final byte B9 = 53;

	static final byte u0 = 0x0;
	static final byte u1 = 0x1;
	static final byte u2 = 0x2;
	static final byte u3 = 0x3;
	static final byte u4 = 0x4;
	static final byte u5 = 0x5;
	static final byte u6 = 0x6;
	static final byte u7 = 0x7;
	static final byte u8 = 0x8;
	static final byte u9 = 0x9;
	static final byte ua = 0xa;
	static final byte ub = 0xb;
	static final byte uc = 0xc;
	static final byte ud = 0xd;
	static final byte ue = 0xe;
	static final byte uf = 0xf;
	static final byte r0 = 0x10;
	static final byte r1 = 0x11;
	static final byte r2 = 0x12;
	static final byte r3 = 0x13;
	static final byte r4 = 0x14;
	static final byte r5 = 0x15;
	static final byte r6 = 0x16;
	static final byte r7 = 0x17;
	static final byte r8 = 0x18;
	static final byte r9 = 0x19;
	static final byte ra = 0x1a;
	static final byte rb = 0x1b;
	static final byte rc = 0x1c;
	static final byte rd = 0x1d;
	static final byte re = 0x1e;
	static final byte rf = 0x1f;
	static final byte f0 = 0x20;
	static final byte f1 = 0x21;
	static final byte f2 = 0x22;
	static final byte f3 = 0x23;
	static final byte f4 = 0x24;
	static final byte f5 = 0x25;
	static final byte f6 = 0x26;
	static final byte f7 = 0x27;
	static final byte f8 = 0x28;
	static final byte f9 = 0x29;
	static final byte fa = 0x2a;
	static final byte fb = 0x2b;
	static final byte fc = 0x2c;
	static final byte fd = 0x2d;
	static final byte fe = 0x2e;
	static final byte ff = 0x2f;
	static final byte d0 = 0x30;
	static final byte d1 = 0x31;
	static final byte d2 = 0x32;
	static final byte d3 = 0x33;
	static final byte d4 = 0x34;
	static final byte d5 = 0x35;
	static final byte d6 = 0x36;
	static final byte d7 = 0x37;
	static final byte d8 = 0x38;
	static final byte d9 = 0x39;
	static final byte da = 0x3a;
	static final byte db = 0x3b;
	static final byte dc = 0x3c;
	static final byte dd = 0x3d;
	static final byte de = 0x3e;
	static final byte df = 0x3f;
	static final byte l0 = 0x40;
	static final byte l1 = 0x41;
	static final byte l2 = 0x42;
	static final byte l3 = 0x43;
	static final byte l4 = 0x44;
	static final byte l5 = 0x45;
	static final byte l6 = 0x46;
	static final byte l7 = 0x47;
	static final byte l8 = 0x48;
	static final byte l9 = 0x49;
	static final byte la = 0x4a;
	static final byte lb = 0x4b;
	static final byte lc = 0x4c;
	static final byte ld = 0x4d;
	static final byte le = 0x4e;
	static final byte lf = 0x4f;
	static final byte b0 = 0x50;
	static final byte b1 = 0x51;
	static final byte b2 = 0x52;
	static final byte b3 = 0x53;
	static final byte b4 = 0x54;
	static final byte b5 = 0x55;
	static final byte b6 = 0x56;
	static final byte b7 = 0x57;
	static final byte b8 = 0x58;
	static final byte b9 = 0x59;
	static final byte ba = 0x5a;
	static final byte bb = 0x5b;
	static final byte bc = 0x5c;
	static final byte bd = 0x5d;
	static final byte be = 0x5e;
	static final byte bf = 0x5f;

	static final int U = 0;
	static final int R = 1;
	static final int F = 2;
	static final int D = 3;
	static final int L = 4;
	static final int B = 5;

	public static final int Ux1 = 0;
	public static final int Ux2 = 1;
	public static final int Ux3 = 2;
	public static final int Rx1 = 3;
	public static final int Rx2 = 4;
	public static final int Rx3 = 5;
	public static final int Fx1 = 6;
	public static final int Fx2 = 7;
	public static final int Fx3 = 8;
	public static final int Dx1 = 9;
	public static final int Dx2 = 10;
	public static final int Dx3 = 11;
	public static final int Lx1 = 12;
	public static final int Lx2 = 13;
	public static final int Lx3 = 14;
	public static final int Bx1 = 15;
	public static final int Bx2 = 16;
	public static final int Bx3 = 17;
	public static final int ux1 = 18;
	public static final int ux2 = 19;
	public static final int ux3 = 20;
	public static final int rx1 = 21;
	public static final int rx2 = 22;
	public static final int rx3 = 23;
	public static final int fx1 = 24;
	public static final int fx2 = 25;
	public static final int fx3 = 26;
	public static final int dx1 = 27;
	public static final int dx2 = 28;
	public static final int dx3 = 29;
	public static final int lx1 = 30;
	public static final int lx2 = 31;
	public static final int lx3 = 32;
	public static final int bx1 = 33;
	public static final int bx2 = 34;
	public static final int bx3 = 35;
	public static final int eom = 36;//End Of Moves

	public static final String[] move2str = {"U  ", "U2 ", "U' ", "R  ", "R2 ", "R' ", "F  ", "F2 ", "F' ",
											 "D  ", "D2 ", "D' ", "L  ", "L2 ", "L' ", "B  ", "B2 ", "B' ",
											 "Uw ", "Uw2", "Uw'", "Rw ", "Rw2", "Rw'", "Fw ", "Fw2", "Fw'",
											 "Dw ", "Dw2", "Dw'", "Lw ", "Lw2", "Lw'", "Bw ", "Bw2", "Bw'"};

	public static final String[] moveIstr = {"U' ", "U2 ", "U  ", "R' ", "R2 ", "R  ", "F' ", "F2 ", "F  ",
											 "D' ", "D2 ", "D  ", "L' ", "L2 ", "L  ", "B' ", "B2 ", "B  ",
											 "Uw'", "Uw2", "Uw ", "Rw'", "Rw2", "Rw ", "Fw'", "Fw2", "Fw ",
											 "Dw'", "Dw2", "Dw ", "Lw'", "Lw2", "Lw ", "Bw'", "Bw2", "Bw "};

	static int[] move2std = {Ux1, Ux2, Ux3, Rx1, Rx2, Rx3, Fx1, Fx2, Fx3,
							 Dx1, Dx2, Dx3, Lx1, Lx2, Lx3, Bx1, Bx2, Bx3,
							 ux2, rx1, rx2, rx3, fx2, dx2, lx1, lx2, lx3, bx2, eom};

	static int[] move3std = {Ux1, Ux2, Ux3, Rx2, Fx1, Fx2, Fx3, Dx1, Dx2, Dx3, Lx2, Bx1, Bx2, Bx3,
							 ux2, rx2, fx2, dx2, lx2, bx2, eom};

	static int[] std2move = new int[37];
	static int[] std3move = new int[37];

	static boolean[][] ckmv = new boolean[37][36];
	static boolean[][] ckmv2 = new boolean[29][28];
	static boolean[][] ckmv3 = new boolean[21][20];

	static int[] skipAxis = new int[36];
	static int[] skipAxis2 = new int[28];
	static int[] skipAxis3 = new int[20];

	static {
		for (int i=0; i<29; i++) {
			std2move[move2std[i]] = i;
		}
		for (int i=0; i<21; i++) {
			std3move[move3std[i]] = i;
		}
		for (int i=0; i<36; i++) {
			for (int j=0; j<36; j++) {
				ckmv[i][j] = (i/3 == j/3) || ((i/3%3 == j/3%3) && (i>j));
			}
			ckmv[36][i] = false;
		}
		for (int i=0; i<29; i++) {
			for (int j=0; j<28; j++) {
				ckmv2[i][j] = ckmv[move2std[i]][move2std[j]];
			}
		}
		for (int i=0; i<21; i++) {
			for (int j=0; j<20; j++) {
				ckmv3[i][j] = ckmv[move3std[i]][move3std[j]];
			}
		}
		for (int i=0; i<36; i++) {
			skipAxis[i] = 36;
			for (int j=i; j<36; j++) {
				if (!ckmv[i][j]) {
					skipAxis[i] = j - 1;
					break;
				}
			}
		}
		for (int i=0; i<28; i++) {
			skipAxis2[i] = 28;
			for (int j=i; j<28; j++) {
				if (!ckmv2[i][j]) {
					skipAxis2[i] = j - 1;
					break;
				}
			}
		}
		for (int i=0; i<20; i++) {
			skipAxis3[i] = 20;
			for (int j=i; j<20; j++) {
				if (!ckmv3[i][j]) {
					skipAxis3[i] = j - 1;
					break;
				}
			}
		}
	}
}
