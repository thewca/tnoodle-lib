package org.worldcubeassociation.tnoodle.scrambles;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.logging.Level;

public class PuzzleIcon {
    private static final Logger l = Logger.getLogger(PuzzleIcon.class.getName());

    private PuzzleIcon() {}

    public static final ByteArrayOutputStream loadPuzzleIconPng(String shortName) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        InputStream in = PuzzleIcon.class.getResourceAsStream("/icon/" + shortName + ".png");

        if (in == null) {
            return null;
        }

        try {
            final byte[] buffer = new byte[0x10000];

            for(;;) {
                int read = in.read(buffer);
                if(read < 0) {
                    break;
                }
                bytes.write(buffer, 0, read);
            }

            in.close();
            return bytes;
        } catch (IOException e) {
            l.log(Level.INFO, "", e);
        }
        return null;
    }

}
