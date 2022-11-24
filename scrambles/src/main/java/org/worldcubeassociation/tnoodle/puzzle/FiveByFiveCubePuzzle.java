package org.worldcubeassociation.tnoodle.puzzle;

import cs.cube555.Search;
import cs.cube555.Tools;
import cs.min2phase.SearchWCA;
import org.timepedia.exporter.client.Export;
import org.worldcubeassociation.tnoodle.scrambles.*;
import org.worldcubeassociation.tnoodle.scrambles.AlgorithmBuilder.MergingMode;

import java.util.Random;
import java.util.logging.Logger;

@Export
public class FiveByFiveCubePuzzle extends CubePuzzle {
    private static final Logger l = Logger.getLogger(FiveByFiveCubePuzzle.class.getName());

    private final ThreadLocal<Search> threePhaseSearcher;
    private final ThreadLocal<SearchWCA> twoPhaseSearcher;

    public FiveByFiveCubePuzzle() {
        super(5);

        threePhaseSearcher = ThreadLocal.withInitial(Search::new);
        twoPhaseSearcher = ThreadLocal.withInitial(SearchWCA::new);
    }

    @Override
    public String getShortName() {
        return "555rs";
    }

    @Override
    public String getLongName() {
        return "5x5x5 (random state, unofficial)";
    }

    @Override
    public PuzzleStateAndGenerator generateRandomMoves(Random r) {
        String randomState = Tools.randomCube(r);
        String[] scrambleState = threePhaseSearcher.get().solveReduction(randomState, Search.INVERT_SOLUTION);

        String reductionSolution = scrambleState[0];
        String reducedState = scrambleState[1];

        if (reducedState == null) {
            // TODO - Not really sure what to do here.
            l.severe(reductionSolution + " while searching for solution to " + randomState);
            assert false;
            return null;
        }

        String reducedSolution = twoPhaseSearcher.get().solution(reducedState, ThreeByThreeCubePuzzle.THREE_BY_THREE_MAX_SCRAMBLE_LENGTH, ThreeByThreeCubePuzzle.THREE_BY_THREE_TIMEOUT, ThreeByThreeCubePuzzle.THREE_BY_THREE_TIMEMIN, SearchWCA.INVERSE_SOLUTION).trim();

        if(reducedSolution.startsWith("Error")) {
            // TODO - Not really sure what to do here.
            l.severe(reducedSolution + " while searching for solution to " + reducedState);
            assert false;
            return null;
        }

        AlgorithmBuilder ab = new AlgorithmBuilder(this, MergingMode.CANONICALIZE_MOVES);

        try {
            ab.appendAlgorithm(reducedSolution);
        } catch (InvalidMoveException e) {
            throw new RuntimeException(new InvalidScrambleException(reducedSolution, e));
        }

        try {
            ab.appendAlgorithm(reductionSolution);
        } catch (InvalidMoveException e) {
            throw new RuntimeException(new InvalidScrambleException(reductionSolution, e));
        }

        return ab.getStateAndGenerator();
    }
}
