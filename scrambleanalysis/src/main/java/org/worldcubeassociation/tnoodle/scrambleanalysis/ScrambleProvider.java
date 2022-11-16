package org.worldcubeassociation.tnoodle.scrambleanalysis;

import org.worldcubeassociation.tnoodle.puzzle.CubePuzzle;
import org.worldcubeassociation.tnoodle.puzzle.ThreeByThreeCubePuzzle;
import org.worldcubeassociation.tnoodle.scrambles.InvalidScrambleException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ScrambleProvider {

    public static List<String> getScrambles(String fileName) throws IOException {
        List<String> scrambles = new ArrayList<>();

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
    public static List<String> generateWcaScrambles(CubePuzzle cube, int N) {
        List<String> scrambles = new ArrayList<>(N);

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

    static CubePuzzle defaultCube = new ThreeByThreeCubePuzzle();

    public static List<String> generateWcaScrambles(int N) {
        return generateWcaScrambles(defaultCube, N);
    }

    public static List<CubePuzzle.CubeState> convertToCubeStates(List<String> scrambles) throws InvalidScrambleException {
        List<CubePuzzle.CubeState> cubeStates = new ArrayList<>(scrambles.size());
        CubePuzzle puzzle = new CubePuzzle(3);

        for (String scramble : scrambles) {
            CubePuzzle.CubeState solved = puzzle.getSolvedState();
            CubePuzzle.CubeState cubeState = (CubePuzzle.CubeState) solved.applyAlgorithm(scramble);

            cubeStates.add(cubeState);
        }

        return cubeStates;
    }
}
