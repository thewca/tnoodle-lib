package org.worldcubeassociation.tnoodle.puzzle;

import java.util.Random;

import cs.threephase.Edge3;
import cs.threephase.Search;
import org.worldcubeassociation.tnoodle.scrambles.AlgorithmBuilder;
import org.worldcubeassociation.tnoodle.scrambles.AlgorithmBuilder.MergingMode;
import org.worldcubeassociation.tnoodle.scrambles.InvalidMoveException;
import org.worldcubeassociation.tnoodle.scrambles.InvalidScrambleException;
import org.worldcubeassociation.tnoodle.scrambles.PuzzleStateAndGenerator;
import org.timepedia.exporter.client.Export;

@Export
public class FourByFourCubePuzzle extends CubePuzzle {
    private final ThreadLocal<Search> threePhaseSearcher;

    public FourByFourCubePuzzle() {
        super(4);
        threePhaseSearcher = ThreadLocal.withInitial(Search::new);
    }

    public double getInitializationStatus() {
        return Edge3.initStatus();
    }

    @Override
    public PuzzleStateAndGenerator generateRandomMoves(Random r) {
        String scramble = threePhaseSearcher.get().randomState(r);
        AlgorithmBuilder ab = new AlgorithmBuilder(this, MergingMode.CANONICALIZE_MOVES);
        try {
            ab.appendAlgorithm(scramble);
        } catch (InvalidMoveException e) {
            throw new RuntimeException(new InvalidScrambleException(scramble, e));
        }
        return ab.getStateAndGenerator();
    }
}
