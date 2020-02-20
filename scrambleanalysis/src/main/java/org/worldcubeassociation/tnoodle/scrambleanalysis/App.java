package org.worldcubeassociation.tnoodle.scrambleanalysis;

import org.worldcubeassociation.tnoodle.scrambles.InvalidMoveException;
import org.worldcubeassociation.tnoodle.scrambles.InvalidScrambleException;

import java.util.ArrayList;

public class App {

	public static void main(String[] args)
			throws InvalidScrambleException, RepresentationException, InvalidMoveException {

		// to test your set of scrambles
		// ArrayList<String> scrambles = ScrambleProvider.getScrambles(fileName);
		// boolean passed = testScrambles(scrambles);

		// Main test
		int numberOfScrambles = 6500;
		ArrayList<String> scrambles = ScrambleProvider.generateWcaScrambles(numberOfScrambles);
		boolean passed = CubeTest.testScrambles(scrambles);
		System.out.println("\nMain test passed? " + passed);

	}
}
