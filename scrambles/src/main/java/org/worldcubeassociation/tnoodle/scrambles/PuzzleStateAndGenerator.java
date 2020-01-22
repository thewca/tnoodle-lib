package org.worldcubeassociation.tnoodle.scrambles;

public class PuzzleStateAndGenerator {
    public Puzzle.PuzzleState state;
    public String generator;
    public PuzzleStateAndGenerator(Puzzle.PuzzleState state, String generator) {
        this.state = state;
        this.generator = generator;
    }
}
