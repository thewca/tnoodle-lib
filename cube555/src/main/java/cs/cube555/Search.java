package cs.cube555;

import java.util.ArrayList;
import static cs.cube555.Util.*;

public class Search {

	public static final int USE_SEPARATOR = 0x1;

	static int phase1SolsSize = 200;
	static int phase2SolsSize = 500;
	static int phase3SolsSize = 500;
	static int phase4SolsSize = 500;
	static int phase5SolsSize = 1;

	public static class Logger {
		final static boolean DEBUG = true;
		static long startTime;
		static long cumSolLen = 0;
		static long[] cumPhaseT = new long[5];
		static void start() {
			startTime = System.nanoTime();
		}
		static void logTime(int phase) {
			cumPhaseT[phase] += System.nanoTime() - startTime;
			System.out.println(String.format("Phase%d Finished in %d ms", phase + 1, (System.nanoTime() - startTime) / 1000000));
			startTime = System.nanoTime();
		}
		public static void print(int div) {
			System.out.println(
			    String.format(
			        "AvgLen=%.2f P1T=%4dms P2T=%4dms P3T=%4dms P4T=%4dms P5T=%4dms TOT=%4dms",
			        cumSolLen * 1.0 / div,
			        cumPhaseT[0] / div / 1000000,
			        cumPhaseT[1] / div / 1000000,
			        cumPhaseT[2] / div / 1000000,
			        cumPhaseT[3] / div / 1000000,
			        cumPhaseT[4] / div / 1000000,
			        (cumPhaseT[0] + cumPhaseT[1] + cumPhaseT[2] + cumPhaseT[3] + cumPhaseT[4]) / div / 1000000
			    ));
		}
	}

	static boolean isInited = false;

	public static synchronized void init() {
		if (isInited) {
			return;
		}
		CubieCube.init();
		Phase1Search.init();
		Phase2Search.init();
		Phase3Search.init();
		Phase4Search.init();
		Phase5Search.init();
		isInited = true;
	}

	Phase1Search p1search = new Phase1Search();
	Phase2Search p2search = new Phase2Search();
	Phase3Search p3search = new Phase3Search();
	Phase4Search p4search = new Phase4Search();
	Phase5Search p5search = new Phase5Search();
	ArrayList<SolvingCube> p1sols = new ArrayList<SolvingCube>();
	ArrayList<SolvingCube> p2sols = new ArrayList<SolvingCube>();
	ArrayList<SolvingCube> p3sols = new ArrayList<SolvingCube>();
	ArrayList<SolvingCube> p4sols = new ArrayList<SolvingCube>();
	ArrayList<SolvingCube> p5sols = new ArrayList<SolvingCube>();
	SolvingCube[] p1cc;
	SolvingCube[] p2cc;
	SolvingCube[] p3cc;
	SolvingCube[] p4cc;
	SolvingCube[] p5cc;

	public synchronized String[] solveReduction(String facelet, int verbose) {
		CubieCube cc = new CubieCube();
		int verifyReduction = cc.fromFacelet(facelet);
		if (verifyReduction != 0) {
			System.out.println(verifyReduction);
			return new String[] {"Error " + verifyReduction, null};
		}
		p1sols.clear();
		p2sols.clear();
		p3sols.clear();
		p4sols.clear();
		p5sols.clear();

		Logger.start();
		System.out.println(cc);

		SolvingCube sc = new SolvingCube(cc);

		SolvingCube[] p1cc = new SolvingCube[3];
		for (int i = 0; i < 3; i++) {
			p1cc[i] = new SolvingCube(sc);
			sc.doConj(16);
		}
		p1search.solve(p1cc, new SolutionChecker(p1cc) {
			@Override
			int check(SolvingCube sc) {
				p1sols.add(sc);
				return p1sols.size() >= phase1SolsSize ? 0 : 1;
			}
		});
		Logger.logTime(0);

		p2cc = p1sols.toArray(new SolvingCube[0]);
		p2search.solve(p2cc, new SolutionChecker(p2cc) {
			@Override
			int check(SolvingCube sc) {
				for (int i = 0; i < 3; i++) {
					p2sols.add(new SolvingCube(sc));
					sc.doConj(16);
				}
				return p2sols.size() >= phase2SolsSize ? 0 : 1;
			}
		});
		Logger.logTime(1);

		p3cc = p2sols.toArray(new SolvingCube[0]);
		p3search.solve(p3cc, new SolutionChecker(p3cc) {
			@Override
			int check(SolvingCube sc) {
				int maskY = 0;
				int maskZ = 0;
				for (int i = 0; i < 4; i++) {
					maskY |= 1 << (sc.wEdge[8 + i] % 12);
					maskY |= 1 << (sc.wEdge[8 + i + 12] % 12);
					maskY |= 1 << (sc.mEdge[8 + i] >> 1);
					maskZ |= 1 << (sc.wEdge[4 + i] % 12);
					maskZ |= 1 << (sc.wEdge[4 + i + 12] % 12);
					maskZ |= 1 << (sc.mEdge[4 + i] >> 1);
				}
				if (Integer.bitCount(maskY) <= 8) {
					p3sols.add(sc);
				}
				if (Integer.bitCount(maskZ) <= 8) {
					sc.doConj(1);
					p3sols.add(new SolvingCube(sc));
				}
				return p3sols.size() >= phase3SolsSize ? 0 : 1;
			}
		});
		Logger.logTime(2);

		p4cc = p3sols.toArray(new SolvingCube[0]);
		p4search.solve(p4cc, new SolutionChecker(p4cc) {
			@Override
			int check(SolvingCube sc) {
				sc.doConj(1);
				p4sols.add(sc);
				return p4sols.size() >= phase4SolsSize ? 0 : 1;
			}
		});
		Logger.logTime(3);

		p5cc = p4sols.toArray(new SolvingCube[0]);
		p5search.solve(p5cc, new SolutionChecker(p5cc) {
			@Override
			int check(SolvingCube sc) {
				p5sols.add(sc);
				return p5sols.size() >= phase5SolsSize ? 0 : 1;
			}
		});
		Logger.logTime(4);

		sc = p5sols.get(0);
		System.out.println(sc);
		System.out.println("Reduction: " + sc.length());
		Logger.cumSolLen += sc.length();

		cc.doMove(sc.getSolution());
		cc.doCornerMove(sc.getSolution());
		String[] ret = new String[2];
		ret[0] = sc.toSolutionString(verbose);
		ret[1] = CubieCube.to333Facelet(cc.toFacelet());
		return ret;
	}
}