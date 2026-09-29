package org.worldcubeassociation.tnoodle.puzzle;

import org.worldcubeassociation.tnoodle.svglite.Color;
import org.worldcubeassociation.tnoodle.svglite.Svg;
import org.worldcubeassociation.tnoodle.svglite.Dimension;
import org.worldcubeassociation.tnoodle.svglite.Path;
import org.worldcubeassociation.tnoodle.svglite.Transform;

import java.util.*;
import java.util.logging.Logger;

import org.worldcubeassociation.tnoodle.puzzle.SkewbSolver.SkewbSolverState;

import org.worldcubeassociation.tnoodle.scrambles.InvalidScrambleException;
import org.worldcubeassociation.tnoodle.scrambles.Puzzle;
import org.worldcubeassociation.tnoodle.scrambles.PuzzleStateAndGenerator;

import org.timepedia.exporter.client.Export;

@Export
public class SkewbPuzzle extends Puzzle {
    private static final int MIN_SCRAMBLE_LENGTH = 11;
    private static final Logger l = Logger.getLogger(SkewbPuzzle.class.getName());
    private final SkewbSolver skewbSolver;

    private static final int pieceSize = 30;
    private static final int gap = 3;

    private static final double sq3d2 = Math.sqrt(3) / 2;

    public SkewbPuzzle() {
        skewbSolver = new SkewbSolver();
        wcaMinScrambleDistance = 7;
    }

    @Override
    public PuzzleStateAndGenerator generateRandomMoves(Random r) {
        SkewbSolverState state = skewbSolver.randomState(r);
        String scramble = skewbSolver.generateExactly(state, MIN_SCRAMBLE_LENGTH);
        assert scramble.split(" ").length == MIN_SCRAMBLE_LENGTH;

        PuzzleState pState;
        try {
            pState = getSolvedState().applyAlgorithm(scramble);
        } catch (InvalidScrambleException e) {
            throw new RuntimeException(e);
        }
        return new PuzzleStateAndGenerator(pState, scramble);
    }

    /*************************************************************
     * Functions to display the puzzle
     */


    private static final Map<String, Color> defaultColorScheme = new HashMap<>();
    static {
        defaultColorScheme.put("U", Color.WHITE);
        defaultColorScheme.put("R", Color.BLUE);
        defaultColorScheme.put("F", Color.RED);
        defaultColorScheme.put("D", Color.YELLOW);
        defaultColorScheme.put("L", Color.GREEN);
        defaultColorScheme.put("B", new Color(0xFF8000));
    }

    @Override
    public Map<String, Color> getDefaultColorScheme() {
        return new HashMap<>(defaultColorScheme);
    }

    private Transform[] getFaceTrans() {
        Transform[] position = {
            new Transform(pieceSize*sq3d2, -pieceSize/2, pieceSize*sq3d2, pieceSize/2, (pieceSize*4+gap*1.5)*sq3d2, pieceSize),
            new Transform(pieceSize*sq3d2, -pieceSize/2, 0, pieceSize, (pieceSize*7+gap*3)*sq3d2, pieceSize * 1.5),
            new Transform(pieceSize*sq3d2, -pieceSize/2, 0, pieceSize, (pieceSize*5+gap*2)*sq3d2, pieceSize * 2.5 + 0.5 * gap),
            new Transform(0, pieceSize, -pieceSize*sq3d2, -pieceSize/2, (pieceSize*3+gap*1)*sq3d2, pieceSize * 4.5 + 1.5 * gap),
            new Transform(pieceSize*sq3d2, pieceSize/2, 0, pieceSize, (pieceSize*3+gap*1)*sq3d2, pieceSize * 2.5 + 0.5 * gap),
            new Transform(pieceSize*sq3d2, pieceSize/2, 0, pieceSize, pieceSize*sq3d2, pieceSize * 1.5),
        };
        return position;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(
                (int) Math.ceil((3 * gap + 8 * pieceSize + 1) * sq3d2),
                (int) Math.ceil(2 * gap + 6 * pieceSize + 1));
    }

    @Override
    public String getLongName() {
        return "Skewb";
    }

    @Override
    public String getShortName() {
        return "skewb";
    }

    @Override
    public PuzzleState getSolvedState() {
        return new SkewbState();
    }

    @Override
    protected int getRandomMoveCount() {
        return 15;
    }

    public class SkewbState extends PuzzleState {
        private final int ORIENTATION_U = 0;
        private final int ORIENTATION_D = 3;

        // This maps our solver's faces (index) to the face/sticker color
        //   used in the Jaap solver (value)
        private final int[] COLOR_TO_SOLVER = new int[] { 0, 3, 1, 5, 2, 4 };

        // This describes the changes to the internal `image` stickering schema
        //   when performing a z2 rotation (the spatial axis right between the two corners facing you)
        private final int[] Z2 = new int[] { 3, 5, 4, 0, 2, 1 };

        // Reusable coordinate maps for {Face, Sticker}
        private final int[][][] FIXED_CORNER_COORDS = {
            {{0, 4}, {1, 1}, {2, 2}}, // U-FR-BR
            {{0, 1}, {4, 1}, {5, 2}}, // U-FL-BL
            {{3, 1}, {4, 4}, {2, 3}}, // D-FL-FR
            {{3, 4}, {1, 4}, {5, 3}}  // D-BL-BR
        };

        private final int[][][] FREE_CORNER_COORDS = {
            {{0, 3}, {2, 1}, {4, 2}}, // U-FR-FL (Front)
            {{0, 2}, {5, 1}, {1, 2}}, // U-BL-BR (Back)
            {{3, 2}, {2, 4}, {1, 3}}, // D-FR-BR (Right-Down)
            {{3, 3}, {5, 4}, {4, 3}}  // D-FL-BL (Left-Down)
        };

        private final int[][] CENTER_COORDS = {
            {0, 0}, {2, 0}, {4, 0}, {1, 0}, {5, 0}, {3, 0}
        };

        // The four corners in the FCN orbit (the Jaap "free" orbit) can be uniquely identified
        //   by the sum of their stickers in the stickering schema of the internal solver.
        //   So this array maps the sums (index) to the solver's permutation index.
        private final int[] FREE_PERM_MAP = { -1, -1, -1, 0, -1, -1, -1, 1, -1, 2, -1, 3 };

        /**
         *           +---------+
         *           | 1     2 |
         *       U > |   0-0   |
         *           | 3     4 |
         * +---------+---------+---------+---------+
         * | 1     2 | 1     2 | 1     2 | 1     2 |
         * |   4-0   |   2-0   |   1-0   |   5-0   |
         * | 3     4 | 3     4 | 3     4 | 3     4 |
         * +---------+---------+---------+---------+
         *      ^    | 1     2 |
         *      FL   |   3-0   |
         *           | 3     4 |
         *           +---------+
         */
        private final int[][] image = new int[6][5];

        SkewbState() {
            for (int i=0; i<6; i++) {
                for (int j=0; j<5; j++) {
                    image[i][j] = i;
                }
            }
        }

        SkewbState(int[][] _image) {
            deepCopy(_image, image);
        }

        private void turn(int axis, int pow, int[][] image) {
            //axis:0-R 1-U 2-L 3-B
            for (int p=0; p<pow; p++) {
                switch (axis) {
                    case 0:
                        swap(2, 0, 3, 0, 1, 0, image);
                        swap(2, 4, 3, 2, 1, 3, image);
                        swap(2, 2, 3, 1, 1, 4, image);
                        swap(2, 3, 3, 4, 1, 1, image);
                        swap(4, 4, 5, 3, 0, 4, image);
                        break;
                    case 1:
                        swap(0, 0, 1, 0, 5, 0, image);
                        swap(0, 2, 1, 2, 5, 1, image);
                        swap(0, 4, 1, 4, 5, 2, image);
                        swap(0, 1, 1, 1, 5, 3, image);
                        swap(4, 1, 2, 2, 3, 4, image);
                        break;
                    case 2:
                        swap(4, 0, 5, 0, 3, 0, image);
                        swap(4, 3, 5, 4, 3, 3, image);
                        swap(4, 1, 5, 3, 3, 1, image);
                        swap(4, 4, 5, 2, 3, 4, image);
                        swap(2, 3, 0, 1, 1, 4, image);
                        break;
                    case 3:
                        swap(1, 0, 3, 0, 5, 0, image);
                        swap(1, 4, 3, 4, 5, 3, image);
                        swap(1, 3, 3, 3, 5, 1, image);
                        swap(1, 2, 3, 2, 5, 4, image);
                        swap(0, 2, 2, 4, 4, 3, image);
                        break;
                    default:
                        assert false;
                }
            }
        }

        private void swap(int f1, int s1, int f2, int s2, int f3, int s3, int[][] image) {
            int temp = image[f1][s1];
            image[f1][s1] = image[f2][s2];
            image[f2][s2] = image[f3][s3];
            image[f3][s3] = temp;
        }

        private void cycle(int f1, int f2, int f3, int[] schema) {
            int temp = schema[f1];
            schema[f1] = schema[f2];
            schema[f2] = schema[f3];
            schema[f3] = temp;
        }

        /**
         * return a square skewb face. whose 4 corners are (-1, -1), (1, -1), (1, 1), (-1, 1). It will be transformed later.
         */
        private Path[] getFacePaths() {
            Path[] p = new Path[5];
            for (int i=0; i<5; i++) {
                p[i] = new Path();
                // In svg, by default, borders are scaled along with shapes.
                // Setting vector-effect to non-scaling-stroke disables that.
                // Unfortunately, batik doesn't support it, so we have
                // to do something hacky by explicitly setting the
                // stroke-width to something teeny.
                // If Batik ever changes to support vector-effect, we
                // can clean this up.
                //p[i].setAttribute("vector-effect", "non-scaling-stroke");
                p[i].setAttribute("stroke-width", 1.0/pieceSize + "px");
            }
            p[0].moveTo(-1, 0); p[0].lineTo( 0, 1); p[0].lineTo( 1, 0); p[0].lineTo(0,-1); p[0].closePath();
            p[1].moveTo(-1, 0); p[1].lineTo(-1,-1); p[1].lineTo( 0,-1); p[1].closePath();
            p[2].moveTo( 0,-1); p[2].lineTo( 1,-1); p[2].lineTo( 1, 0); p[2].closePath();
            p[3].moveTo(-1, 0); p[3].lineTo(-1, 1); p[3].lineTo( 0, 1); p[3].closePath();
            p[4].moveTo( 0, 1); p[4].lineTo( 1, 1); p[4].lineTo( 1, 0); p[4].closePath();
            return p;
        }

        @Override
        protected Svg drawScramble(Map<String, Color> colorScheme) {
            Svg g = new Svg(getPreferredSize());
            Color[] scheme = new Color[6];
            for(int i = 0; i < scheme.length; i++) {
                scheme[i] = colorScheme.get("URFDLB".charAt(i)+"");
            }
            Transform[] position = getFaceTrans();
            for (int face=0; face<6; face++) {
                Path[] p = getFacePaths();
                for (int i=0; i<5; i++) {
                    p[i].transform(position[face]);
                    p[i].setFill(scheme[image[face][i]]);
                    p[i].setStroke(Color.BLACK);
                    g.appendChild(p[i]);
                }
            }
            return g;
        }

        public SkewbSolverState toSkewbSolverState() {
            // The internal solver is written in Jaap notation,
            //   but WCA FCN and Jaap notation have misaligned reference frames:
            // - In FCN, the "Holy Corner" (UFR) is fixed.
            // - Jaap's solver relies on its four notation corners L, R, D and B to be fixed.
            // The WCA FCN corner (UFR) is in the opposite orbit of the four Jaap corners,
            //   and so are the FCN R, L and U corners as well.
            // But the FCN B corner is in the "Jaap orbit", so because of that
            //   the orientation of the four Jaap corners might be off if our scramble contained B moves.
            //
            // Because the FCN B corner is in this "Jaap orbit" already, even when holding the Skewb
            //   in standard WCA scramble orientation (FCN), we can immediately use the Jaap orbit as a reference.
            // Note that at this point, we do not know whether the B corner is actually in the B slot
            //   (because any amount of FCN U, R and L moves may have permuted it). But no matter the current permutation,
            //   it will never leave its orbit (because of Skewb mathematical properties) and that's what we care about:
            // This next for-loop is only interested in determining the orientation of each corner in the B (aka Jaap) orbit.
            //   It does not matter at this point in time how these four orbit corners are permuted. It only matters that
            //   they are all in the same orbit, and we don't (yet) care which exact corner we're looking at
            //   within the orbit. We only care about that it's in this orbit and that it has an orientation w.r.t. white/yellow faces.
            // For every B move that was applied, the sum of orientations on that orbit will have shifted by 1 (mod 3).
            //   So we need to rotate our entire Skewb (later) according to this shifted reference frame.
            int fcnBOrbitOrientation = 0;
            for (int i = 0; i < 4; i++) {
                int[][] coords = FIXED_CORNER_COORDS[i];
                int orient = 0;

                while (this.image[coords[orient][0]][coords[orient][1]] != ORIENTATION_U &&
                    this.image[coords[orient][0]][coords[orient][1]] != ORIENTATION_D) {
                    orient++;
                    assert orient < 3;
                }

                fcnBOrbitOrientation += orient;
            }

            // The most trivial way to "swap" the two orbits of corners on a Skewb is by performing z2.
            // This is also what the internal solver does when converting between Jaap and FCN notation,
            //   see SkewbSolver#getSolution for details.
            int[] jaapCorrection = cloneArr(Z2);

            // The integer values passed to the `cycle` method calls in this next block
            //   are describing the six faces of the Skewb int[][] representation.
            //   See the Skewb ASCII-art further above for a visual mapping.
            for (int i = 0; i < fcnBOrbitOrientation % 3; i++) {
                // Rotate the three centers/colors around the FCN "holy corner" clockwise
                cycle(0, 4, 2, jaapCorrection);
                // Rotate the three centers/colors around FCN B counter-clockwise
                cycle(1, 5, 3, jaapCorrection);
            }

            // Our B-move restickering contains an implicit z2 rotation, so instead of just applying it
            //   to every element of `image`, we must also rotate our whole Skewb physically back by a z2.
            int[][] jaapImage = new int[][] {
                { jaapCorrection[image[3][0]], jaapCorrection[image[3][2]], jaapCorrection[image[3][4]], jaapCorrection[image[3][1]], jaapCorrection[image[3][3]] },
                { jaapCorrection[image[5][0]], jaapCorrection[image[5][4]], jaapCorrection[image[5][3]], jaapCorrection[image[5][2]], jaapCorrection[image[5][1]] },
                { jaapCorrection[image[4][0]], jaapCorrection[image[4][4]], jaapCorrection[image[4][3]], jaapCorrection[image[4][2]], jaapCorrection[image[4][1]] },
                { jaapCorrection[image[0][0]], jaapCorrection[image[0][3]], jaapCorrection[image[0][1]], jaapCorrection[image[0][4]], jaapCorrection[image[0][2]] },
                { jaapCorrection[image[2][0]], jaapCorrection[image[2][4]], jaapCorrection[image[2][3]], jaapCorrection[image[2][2]], jaapCorrection[image[2][1]] },
                { jaapCorrection[image[1][0]], jaapCorrection[image[1][4]], jaapCorrection[image[1][3]], jaapCorrection[image[1][2]], jaapCorrection[image[1][1]] }
            };

            // Now we know the Skewb state in the Jaap reference frame.
            // Everything below, all the way until the final `return` statement,
            //   is simply about re-formatting the int[][] state into something
            //   that the Jaap solver actually understands (aka packed Lehmer codes).
            int[] centerPerm = new int[6];
            for (int i = 0; i < 6; i++) {
                int[] coords = CENTER_COORDS[i];
                centerPerm[i] = COLOR_TO_SOLVER[jaapImage[coords[0]][coords[1]]];
            }

            int[] fixedTwist = new int[4];
            for (int i = 0; i < 4; i++) {
                int[][] coords = FIXED_CORNER_COORDS[i];
                while (jaapImage[coords[fixedTwist[i]][0]][coords[fixedTwist[i]][1]] != ORIENTATION_U &&
                    jaapImage[coords[fixedTwist[i]][0]][coords[fixedTwist[i]][1]] != ORIENTATION_D) {
                    fixedTwist[i]++;
                    assert fixedTwist[i] < 3;
                }
            }

            int[] currentFreePerm = new int[4];
            int[] freeTwist = new int[4];
            for (int i = 0; i < 4; i++) {
                int[][] coords = FREE_CORNER_COORDS[i];

                int s0 = COLOR_TO_SOLVER[jaapImage[coords[0][0]][coords[0][1]]];
                int s1 = COLOR_TO_SOLVER[jaapImage[coords[1][0]][coords[1][1]]];
                int s2 = COLOR_TO_SOLVER[jaapImage[coords[2][0]][coords[2][1]]];

                currentFreePerm[i] = FREE_PERM_MAP[s0 + s1 + s2];
                assert currentFreePerm[i] != -1;

                while (jaapImage[coords[freeTwist[i]][0]][coords[freeTwist[i]][1]] != ORIENTATION_U &&
                    jaapImage[coords[freeTwist[i]][0]][coords[freeTwist[i]][1]] != ORIENTATION_D) {
                    freeTwist[i]++;
                    assert freeTwist[i] < 3;
                }
            }

            SkewbSolverState state = new SkewbSolverState();
            state.perm = SkewbSolver.packCenterPerm(centerPerm) * SkewbSolver.FREE_CORNER_PERM + SkewbSolver.packCornerPerm(currentFreePerm);
            state.twst = SkewbSolver.packCornerOrient(freeTwist, fixedTwist);

            return state;
        }

        @Override
        public String solveIn(int n) {
            return skewbSolver.solveIn(toSkewbSolverState(), n);
        }

        @Override
        public Map<String, PuzzleState> getSuccessorsByName() {
            Map<String, PuzzleState> successors = new LinkedHashMap<>();
            String axes = "RULB";
            for(int axis = 0; axis < axes.length(); axis++) {
                char face = axes.charAt(axis);
                for(int pow = 1; pow <= 2; pow++) {
                    String turn = "" + face;
                    if(pow == 2) {
                        turn += "'";
                    }
                    int[][] imageCopy = new int[image.length][image[0].length];
                    deepCopy(image, imageCopy);
                    turn(axis, pow, imageCopy);
                    successors.put(turn, new SkewbState(imageCopy));
                }
            }

            return successors;
        }

        @Override
        public boolean equals(Object other) {
            // Sure this could blow up with a cast exception, but shouldn't it? =)
            return Arrays.deepEquals(image, ((SkewbState) other).image);
        }

        @Override
        public int hashCode() {
            return Arrays.deepHashCode(image);
        }
    }

}
