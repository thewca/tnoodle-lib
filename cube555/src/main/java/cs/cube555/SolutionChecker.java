package cs.cube555;

class SolutionChecker {

	SolvingCube[] ccList;

	SolutionChecker(SolvingCube[] ccList) {
		this.ccList = ccList;
	}

	int check(int[] solution, int length, int ccidx) {
		SolvingCube sc = new SolvingCube(ccList[ccidx]);
		sc.doMove(copySolution(solution, length));
		sc.addCheckPoint();
		return check(sc);
	}

	int check(SolvingCube sc) {
		return 0;
	}

	static int[] copySolution(int[] solution, int length) {
		int[] solutionCopy = new int[length];
		for (int i = 0; i < length; i++) {
			solutionCopy[i] = solution[i];
		}
		return solutionCopy;
	}
}