package org.worldcubeassociation.tnoodle.puzzle;

import org.worldcubeassociation.tnoodle.scrambles.AlgorithmBuilder;
import org.worldcubeassociation.tnoodle.scrambles.InvalidMoveException;
import org.worldcubeassociation.tnoodle.scrambles.PuzzleStateAndGenerator;

import java.util.Random;
import java.util.logging.Level;

public class LayeredRandomizationCubePuzzle extends CubePuzzle {
    private final ThreadLocal<ThreeByThreeCubePuzzle> threeScrambler;
    private final ThreadLocal<TwoByTwoCubePuzzle> twoScrambler;

    // number of outer layers to exclude from the "thickness" scrambling process.
    // The current setting means that the outermost layer (the "reduction phase")
    // won't receive an additional 3x3 scramble and only the layers that are relevant
    // to centers will be scrambled.
    private static final int EXCLUDE_OUTER_LAYERS = 1;

    public LayeredRandomizationCubePuzzle(int size) {
        super(size);

        this.threeScrambler = ThreadLocal.withInitial(ThreeByThreeCubePuzzle::new);
        this.twoScrambler = ThreadLocal.withInitial(TwoByTwoCubePuzzle::new);
    }

    @Override
    public String getLongName() {
        return super.getLongName() + " (layered randomization)";
    }

    @Override
    public String getShortName() {
        return super.getShortName() + "lrand";
    }

    @Override
    public double getInitializationStatus() {
        double threeInit = this.threeScrambler.get().getInitializationStatus();
        double twoInit = this.twoScrambler.get().getInitializationStatus();

        return (threeInit + twoInit) / 2;
    }

    @Override
    public PuzzleStateAndGenerator generateRandomMoves(Random r) {
        AlgorithmBuilder ab = new AlgorithmBuilder(this, AlgorithmBuilder.MergingMode.CANONICALIZE_MOVES);

        int thickLayers = this.size / 2;

        for (int thickness = EXCLUDE_OUTER_LAYERS; thickness < thickLayers; thickness++) {
            // need to pass thickness here to determine if we need 3x3 scr
            // or 2x2 scr (on the innermost layer of even-numbered NxN)
            String rawScramble = generateOuterScramble(thickness, r);

            // transform the entire scramble to wide grips. This performs absolutely no sanity checks at the moment!
            String morphedScramble = transformScrambleToThickness(rawScramble, thickness);

            try {
                ab.appendAlgorithm(morphedScramble);
            } catch (InvalidMoveException e) {
                l.log(Level.SEVERE, "", e);
                throw new RuntimeException(e);
            }
        }

        // we take a "classic" big cube scramble sequence as reference
        // because there is currently no API to generate N random moves directly
        // (and I am too lazy to write one if we already have something that comes close)
        PuzzleStateAndGenerator randomMovesPsag = super.generateRandomMoves(r);
        String[] randomMoves = AlgorithmBuilder.splitAlgorithm(randomMovesPsag.generator);

        int remainingLength = this.getRandomMoveCount() - ab.getTotalCost();

        for (int i = 0; i < remainingLength; i++) {
            try {
                ab.appendMove(randomMoves[i]);
            } catch (InvalidMoveException e) {
                l.log(Level.SEVERE, "", e);
                throw new RuntimeException(e);
            }
        }

        return ab.getStateAndGenerator();
    }

    private String generateOuterScramble(int thickness, Random r) {
        if (this.size % 2 == 0 && thickness == (this.size / 2) - 1) {
            return this.twoScrambler.get().generateWcaScramble(r);
        }

        return this.threeScrambler.get().generateWcaScramble(r);
    }

    private String transformScrambleToThickness(String rawScramble, int thickness) {
        String[] rawMoves = AlgorithmBuilder.splitAlgorithm(rawScramble);
        StringBuilder transformedMoves = new StringBuilder();

        // This parsing method is very hacky in that it silently assumes it only ever handles
        // 2x2 and 3x3 scrambles. Concepts like "Fw" (w at the end) or even "3Fw" (number at the front)
        // are absolutely not accounted for because we don't need them for the current generators.
        for (String rawMove : rawMoves) {
            String rawFace = String.valueOf(rawMove.charAt(0));
            Face f = Face.valueOf(rawFace);

            String dirModifier = rawMove.substring(1).replace('\'', '3');
            int dir = dirModifier.length() == 0 ? 1 : Integer.parseInt(dirModifier);

            CubeMove thickMove = new CubeMove(f, dir, thickness);

            transformedMoves.append(thickMove);
            transformedMoves.append(" ");
        }

        return transformedMoves.toString().trim();
    }
}
