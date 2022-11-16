package org.worldcubeassociation.tnoodle.scrambles;

public class InvalidMoveException extends Exception {
    public InvalidMoveException(String move) {
        super("Invalid move: " + move);
    }
}
