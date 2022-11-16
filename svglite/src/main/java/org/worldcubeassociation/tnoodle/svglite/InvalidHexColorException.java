package org.worldcubeassociation.tnoodle.svglite;

public class InvalidHexColorException extends Exception {
    public InvalidHexColorException(String invalidHex) {
        super(invalidHex);
    }
}
