package org.worldcubeassociation.tnoodle.puzzle;

import java.util.Random;

import org.worldcubeassociation.tnoodle.scrambles.AlgorithmBuilder;
import org.worldcubeassociation.tnoodle.scrambles.AlgorithmBuilder.MergingMode;
import org.worldcubeassociation.tnoodle.scrambles.InvalidMoveException;
import org.worldcubeassociation.tnoodle.scrambles.InvalidScrambleException;
import org.worldcubeassociation.tnoodle.scrambles.PuzzleStateAndGenerator;
import org.timepedia.exporter.client.Export;

@Export
public class FourByFourCubePuzzle extends CubePuzzle {
    private ThreadLocal<cs.threephase.Search> threePhaseSearcher = null;

    public FourByFourCubePuzzle() {
        super(4);
        threePhaseSearcher = new ThreadLocal<cs.threephase.Search>() {
            protected cs.threephase.Search initialValue() {
                return new cs.threephase.Search();
            };
        };
    }

    public double getInitializationStatus() {
        return cs.threephase.Edge3.initStatus();
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
