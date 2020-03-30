package org.worldcubeassociation.tnoodle.scrambles;

import org.worldcubeassociation.tnoodle.puzzle.*;

public enum PuzzleRegistry {
    TWO(TwoByTwoCubePuzzle.class),
    THREE(ThreeByThreeCubePuzzle.class),
    FOUR(FourByFourCubePuzzle.class),
    FOUR_FAST(FourByFourRandomTurnsCubePuzzle.class),
    FIVE(CubePuzzle.class, 5),
    SIX(CubePuzzle.class, 6),
    SEVEN(CubePuzzle.class, 7),
    THREE_NI(NoInspectionThreeByThreeCubePuzzle.class),
    FOUR_NI(NoInspectionFourByFourCubePuzzle.class),
    FIVE_NI(NoInspectionFiveByFiveCubePuzzle.class),
    THREE_FM(ThreeByThreeCubeFewestMovesPuzzle.class),
    PYRA(PyraminxPuzzle.class),
    SQ1(SquareOnePuzzle.class),
    MEGA(MegaminxPuzzle.class),
    CLOCK(ClockPuzzle.class),
    SKEWB(SkewbPuzzle.class);

    private LazySupplier<? extends Puzzle> puzzleSupplier;

    <T extends Puzzle> PuzzleRegistry(Class<T> suppliyingClass, Object... ctorArgs) {
        this.puzzleSupplier = new LazySupplier<T>(suppliyingClass, ctorArgs);
    }

    public Puzzle getScrambler() {
        return this.puzzleSupplier.getInstance();
    }

    // WORD OF ADVICE: The puzzles that use local scrambling mechanisms
    // should not take long to boot anyways because their computation-heavy
    // code is wrapped in ThreadLocal objects that are only executed on-demand

    public String getKey() {
        return this.getScrambler().getShortName();
    }

    public String getDescription() {
        return this.getScrambler().getLongName();
    }
}
