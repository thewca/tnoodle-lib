package org.worldcubeassociation.tnoodle.scrambleanalysis;

import org.worldcubeassociation.tnoodle.puzzle.ThreeByThreeCubePuzzle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ScrambleProvider {

    public static List<String> getScrambles(String fileName) throws IOException {

        List<String> scrambles = new ArrayList<String>();

        // Read scrambles
        File file = new File(fileName);
        Scanner input = new Scanner(file);

        try {
            while (input.hasNextLine()) {
                String scramble = input.nextLine().trim();
                if (scramble.length() > 0) {
                    scrambles.add(scramble);
                }
            }
        } catch (Exception e) {
            throw new IOException("There was an error reading the file.");
        } finally {
            input.close();
        }

        return scrambles;
    }

    // This is the main test
    public static ArrayList<String> generateWcaScrambles(int N) {
        ThreeByThreeCubePuzzle cube = new ThreeByThreeCubePuzzle();
        ArrayList<String> scrambles = new ArrayList<String>();

        for (int i = 0; i < N; i++) {
            // Give some status to the user
            if (i % 1000 == 0) {
                System.out.println("Generating scramble " + (i + 1) + "/" + N);
            }

            String scramble = cube.generateScramble();
            scrambles.add(scramble);
        }

        return scrambles;
    }
}
