package org.worldcubeassociation.tnoodle.scrambleanalysis;

import static org.junit.jupiter.api.Assertions.*;

import static org.worldcubeassociation.tnoodle.scrambleanalysis.CubeHelper.countMisorientedEdges;

import java.util.logging.Logger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.gnehzr.tnoodle.puzzle.CubePuzzle.CubeState;
import net.gnehzr.tnoodle.puzzle.ThreeByThreeCubePuzzle;
import net.gnehzr.tnoodle.scrambles.InvalidMoveException;
import net.gnehzr.tnoodle.scrambles.InvalidScrambleException;

public class CubeHelperTest {

	ThreeByThreeCubePuzzle cube = new ThreeByThreeCubePuzzle();
	Logger logger = Logger.getLogger(CubeHelperTest.class.getName());

	@Test
	public void orientationTest() throws InvalidScrambleException, RepresentationException, InvalidMoveException {
		int n = 1;

		// The number of misoriented edge must be even, corner orientation sum must be a
		// multiple of 3.
		for (int i = 0; i < n; i++) {
			String scramble = cube.generateScramble();
			CubeState state = (CubeState) cube.getSolvedState().applyAlgorithm(scramble);
			String representation = state.toFaceCube();

			int misorientedEdges = CubeHelper.countMisorientedEdges(representation);
			int cornerSum = CubeHelper.cornerOrientationSum(representation);

			logger.info("Scramble: " + scramble);
			logger.info("Misoriented edges: " + misorientedEdges);
			logger.info("Corner sum: " + cornerSum);
			logger.info("Parity: " + CubeHelper.hasParity(scramble));

			assertEquals(misorientedEdges % 2, 0);
			assertEquals(cornerSum % 3, 0);
		}
	}

	@Test
	public void hasParityTest() throws InvalidMoveException {
		String scramble = "U";
		Assertions.assertTrue(CubeHelper.hasParity(scramble));

		scramble = "U'";
		Assertions.assertTrue(CubeHelper.hasParity(scramble));

		scramble = "U2";
		Assertions.assertFalse(CubeHelper.hasParity(scramble));

		String yPerm = "F R U' R' U' R U R' F' R U R' U' R' F R F'";
		Assertions.assertTrue(CubeHelper.hasParity(yPerm));

		String uPerm = "R2 U' R' U' R U R U R U' R";
		Assertions.assertFalse(CubeHelper.hasParity(uPerm));
	}

	@Test
	public void countMisorientedEdgesTest() throws InvalidScrambleException, RepresentationException {
		String scramble1 = "F";
		String scramble2 = "F' B";
		String scramble3 = "F U F";

		CubeState state1 = (CubeState) cube.getSolvedState().applyAlgorithm(scramble1);
		String representation1 = state1.toFaceCube();
		int result1 = CubeHelper.countMisorientedEdges(representation1);

		CubeState state2 = (CubeState) cube.getSolvedState().applyAlgorithm(scramble2);
		String representation2 = state2.toFaceCube();
		int result2 = CubeHelper.countMisorientedEdges(representation2);

		CubeState state3 = (CubeState) cube.getSolvedState().applyAlgorithm(scramble3);
		String representation3 = state3.toFaceCube();
		int result3 = CubeHelper.countMisorientedEdges(representation3);

		assertEquals(result1, 4);
		assertEquals(result2, 8);
		assertEquals(result3, 2);

		Assertions.assertEquals(CubeHelper.countMisorientedEdges(representation1), CubeHelper.countMisorientedEdges(state1));
		Assertions.assertEquals(CubeHelper.countMisorientedEdges(representation2), CubeHelper.countMisorientedEdges(state2));
		Assertions.assertEquals(CubeHelper.countMisorientedEdges(representation3), CubeHelper.countMisorientedEdges(state3));
	}

	@Test
	public void isOrientedEdgeTest() throws InvalidScrambleException, RepresentationException {
		String scramble = "F B'";
		String representation = getRepresentation(scramble);

		Assertions.assertTrue(CubeHelper.isOrientedEdge(representation, 1));
		Assertions.assertTrue(CubeHelper.isOrientedEdge(representation, 2));
		Assertions.assertTrue(CubeHelper.isOrientedEdge(representation, 5));
		Assertions.assertTrue(CubeHelper.isOrientedEdge(representation, 6));

		Assertions.assertFalse(CubeHelper.isOrientedEdge(representation, 0));
		Assertions.assertFalse(CubeHelper.isOrientedEdge(representation, 3));
		Assertions.assertFalse(CubeHelper.isOrientedEdge(representation, 4));
		Assertions.assertFalse(CubeHelper.isOrientedEdge(representation, 7));
		Assertions.assertFalse(CubeHelper.isOrientedEdge(representation, 8));
		Assertions.assertFalse(CubeHelper.isOrientedEdge(representation, 9));
		Assertions.assertFalse(CubeHelper.isOrientedEdge(representation, 10));
		Assertions.assertFalse(CubeHelper.isOrientedEdge(representation, 11));
	}

	@Test
	public void getFinalPositionTest() throws InvalidScrambleException, RepresentationException {
		String scramble1 = "U2";
		String representation1 = getRepresentation(scramble1);

		String scramble2 = "R U R' U R U2 R'";
		String representation2 = getRepresentation(scramble2);

		Assertions.assertEquals(CubeHelper.getFinalPositionOfEdge(representation1, 0), 3);
		Assertions.assertEquals(CubeHelper.getFinalPositionOfEdge(representation2, 0), 1);

		Assertions.assertEquals(CubeHelper.getFinalPositionOfCorner(representation1, 0), 3);
		Assertions.assertEquals(CubeHelper.getFinalPositionOfCorner(representation2, 0), 3);

		Assertions.assertEquals(CubeHelper.getFinalPositionOfCorner(getRepresentation("R"), 1), 7);
		Assertions.assertEquals(CubeHelper.getFinalPositionOfCorner(getRepresentation("R'"), 1), 3);
	}

	private String getRepresentation(String scramble) throws InvalidScrambleException {
		CubeState state = (CubeState) cube.getSolvedState().applyAlgorithm(scramble);
		return state.toFaceCube();
	}
}
