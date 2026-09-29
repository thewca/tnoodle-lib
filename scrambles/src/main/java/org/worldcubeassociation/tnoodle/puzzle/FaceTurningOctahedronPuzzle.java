package org.worldcubeassociation.tnoodle.puzzle;

import levigibson.fto3phase.FtoCubie;
import levigibson.fto3phase.Search;
import org.timepedia.exporter.client.Export;
import org.worldcubeassociation.tnoodle.scrambles.InvalidScrambleException;
import org.worldcubeassociation.tnoodle.scrambles.Puzzle;
import org.worldcubeassociation.tnoodle.scrambles.PuzzleStateAndGenerator;
import org.worldcubeassociation.tnoodle.svglite.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Export
public class FaceTurningOctahedronPuzzle extends Puzzle {

    @Override
    public String getShortName() {
        return "fto";
    }

    @Override
    public String getLongName() {
        return "Face Turning Octahedron";
    }

    private final ThreadLocal<Search> threePhaseSearcher;

    public FaceTurningOctahedronPuzzle() {
        threePhaseSearcher = ThreadLocal.withInitial(Search::new);
    }

    @Override
    public Map<String, Color> getDefaultColorScheme() {
        return new HashMap<>(defaultColorScheme);
    }

    private static final Map<String, Color> defaultColorScheme = new HashMap<>();

    static {
        defaultColorScheme.put("B", Color.BLUE);
        defaultColorScheme.put("D", Color.YELLOW);
        defaultColorScheme.put("F", Color.GREEN);
        defaultColorScheme.put("L", new Color(124, 2, 158)); // Purple
        defaultColorScheme.put("R", Color.RED);
        defaultColorScheme.put("U", Color.WHITE);
        defaultColorScheme.put("BL", new Color(255, 128, 0)); // Orange
        defaultColorScheme.put("BR", Color.GRAY);
    }

    private static final int STICKER_SIZE = 30;
    private static final int CENTER_GAP_SIZE = 12;
    private static final int FACE_GAP_SIZE = 3;
    private static final int MARGIN = 5;

    private static final String[] MOVE_NAMES = {"U", "R", "F", "L", "B", "BL", "D", "BR", "U'", "R'", "F'", "L'", "B'", "BL'", "D'", "BR'"};

    @Override
    public PuzzleStateAndGenerator generateRandomMoves(Random r) {
        FtoCubie randomState = FtoCubie.randomCube(r);

        String scramble = threePhaseSearcher.get().solution(randomState).trim();
        PuzzleState state;
        try {
            state = getSolvedState().applyAlgorithm(scramble);
        } catch (InvalidScrambleException e) {
            throw new RuntimeException(e);
        }
        return new PuzzleStateAndGenerator(state, scramble);
    }

    public class FaceTurningOctahedronState extends PuzzleState {

        private final int[][] image = new int[8][9];

        public FaceTurningOctahedronState(){
            for (int face = 0; face < 8; face++) {
                for (int sticker = 0; sticker < 9; sticker++) {
                    image[face][sticker] = face;
                }
            }
        }

        //Copy Constructor
        public FaceTurningOctahedronState(FaceTurningOctahedronState other){
            for (int face = 0; face < 8; face++) {
                System.arraycopy(other.image[face], 0, this.image[face], 0, 9);
            }
        }

        @Override
        public Map<String, FaceTurningOctahedronState> getSuccessorsByName() {
            Map<String, FaceTurningOctahedronState> successors = new HashMap<>();

            for (int face = 0; face < 8; face++) {
                for (int dir = 1; dir < 3; dir++) {
                    String key = MOVE_NAMES[face + 8 * (dir-1)];
                    FaceTurningOctahedronState successor = new FaceTurningOctahedronState(this);
                    successor.turn(face, dir);
                    successors.put(key, successor);
                }
            }

            return successors;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }

            if (other == null || getClass() != other.getClass()) {
                return false;
            }

            FaceTurningOctahedronState otherFto = (FaceTurningOctahedronState) other;
            return Arrays.deepEquals(this.image, otherFto.image);
        }

        @Override
        public int hashCode() {
            return Arrays.deepHashCode(image);
        }

        private double[] rotatePoint(double[] point, double[] center){
            if (point.length != 2) {
                throw new IllegalArgumentException("Invalid point length");
            }
            double x = point[0];
            double y = point[1];
            double h = center[0];
            double k =  center[1];

            return new double[]{h - (y - k), k + (x-h)};
        }

        private double[] translatePointRight(double[] point){
            if (point.length != 2) {
                throw new IllegalArgumentException("Invalid point length");
            }
            return new double[]{point[0] + STICKER_SIZE * 3 + CENTER_GAP_SIZE, point[1]};
        }


        //barycentric interpolation
        //(a.k.a, a weighted mean of the three points where i and j are the weights)
        private double[] facePoint(double[] a, double[] b, double[] c, int i, int j) {
            double bWeight = i / 3.0;
            double cWeight = j / 3.0;
            double aWeight = 1 - bWeight - cWeight;
            return new double[] {
                aWeight * a[0] + bWeight * b[0] + cWeight * c[0],
                aWeight * a[1] + bWeight * b[1] + cWeight * c[1]
            };
        }

        private Path sticker(double[] a, double[] b, double[] c, Color color) {
            Path p = new Path();
            p.setStroke(Color.BLACK);
            p.setFill(color);
            p.moveTo(a[0], a[1]);
            p.lineTo(b[0], b[1]);
            p.lineTo(c[0], c[1]);
            p.closePath();

            return p;
        }

        private void drawFace(Svg svg, Color[] scheme, int[] stickerColors, boolean rightSide, int direction){
            int m = MARGIN;

            //Compute the three outer points of the face based on
            //dir and rightSide
            double[] a = {m + (STICKER_SIZE*1.5), m+(STICKER_SIZE*1.5) - FACE_GAP_SIZE};
            double[] b = {m, m - FACE_GAP_SIZE};
            double[] c = {3*STICKER_SIZE + m, m - FACE_GAP_SIZE};

            double[] center = {m + (STICKER_SIZE*1.5), m+(STICKER_SIZE*1.5)};

            //Move the outer points of the face to the right location
            //in the draw scramble
            for (int i = 0; i < direction; i++) {
                a = rotatePoint(a, center);
                b = rotatePoint(b, center);
                c = rotatePoint(c, center);
            }
            if (rightSide) {
                a = translatePointRight(a);
                b = translatePointRight(b);
                c = translatePointRight(c);
            }

            //Draw outline of face
            Path p = new Path();
            p.moveTo(a[0], a[1]);
            p.lineTo(b[0], b[1]);
            p.lineTo(c[0], c[1]);
            p.closePath();

            p.setFill(Color.WHITE);
            p.setStroke(Color.BLACK);

            svg.appendChild(p);

            //Draw all the stickers on the face
            //The location of the stickers are computed dynamically
            //based on the points a, b, and c from above
            //
            //Mess around with the coordinate system here if you want
            //https://www.desmos.com/calculator/k6k33zuxel
            int stickerIndex = 0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3-i; j++) {
                    double[] point0;
                    double[] point1;
                    double[] point2;

                    //Draw center stickers
                    if (i + j < 2) {
                        point0 = facePoint(a, b, c, i+1, j);
                        point1 = facePoint(a, b, c, i, j+1);
                        point2 = facePoint(a, b, c, i + 1, j + 1);

                        Color color = scheme[stickerColors[stickerIndex++]];

                        Path sticker = sticker(point0, point1, point2, color);
                        svg.appendChild(sticker);
                    }

                    //Draw corner and edge stickers
                    point0 = facePoint(a, b, c, i, j);
                    point1 = facePoint(a, b, c, i+1, j);
                    point2 = facePoint(a, b, c, i, j+1);
                    Color color = scheme[stickerColors[stickerIndex++]];

                    Path sticker = sticker(point0, point1, point2, color);

                    svg.appendChild(sticker);
                }
            }
        }

        @Override
        protected Svg drawScramble(Map<String, Color> colorScheme) {
            Dimension preferredSize = getPreferredSize();
            Svg svg = new Svg(preferredSize);
            svg.setStroke(2, 10, "round");

            Color[] scheme = new Color[8];
            for (int i = 0; i < scheme.length; i++) {
                scheme[i] = colorScheme.get(MOVE_NAMES[i]);
            }

            for (int face = 0; face < 8; face++) {
                drawFace(svg, scheme, image[face],
                    isFaceRenderedOnRightSide(face),
                    getDrawScrambleFaceRotation(face));
            }

            return svg;
        }

        private int getDrawScrambleFaceRotation(int face){
            return face % 4;
        }

        private boolean isFaceRenderedOnRightSide(int face) {
            return face > 3;
        }

        private void threeCycleStickers(int f1, int s1, int f2, int s2, int f3, int s3, int[][] image) {
            int temp = image[f3][s3];
            image[f3][s3] = image[f2][s2];
            image[f2][s2] = image[f1][s1];
            image[f1][s1] = temp;
        }

        private void turn(int side, int dir) {
            dir %= 3;
            for (int i = 0; i < dir; i++) {
                turn(side);
            }
        }

        private void turn(int side) {
            switch (side) {
                case 0: // U
                    threeCycleStickers(0, 0, 0, 5, 0, 2, image);
                    threeCycleStickers(0, 6, 0, 7, 0, 3, image);
                    threeCycleStickers(0, 1, 0, 8, 0, 4, image);
                    threeCycleStickers(2, 1, 5, 8, 7, 4, image);
                    threeCycleStickers(1, 1, 3, 4, 4, 8, image);
                    threeCycleStickers(1, 8, 3, 1, 4, 4, image);
                    threeCycleStickers(1, 6, 3, 3, 4, 7, image);
                    threeCycleStickers(1, 0, 3, 2, 4, 5, image);
                    threeCycleStickers(1, 5, 3, 0, 4, 2, image);
                    break;
                case 1: // R
                    threeCycleStickers(1, 0, 1, 5, 1, 2, image);
                    threeCycleStickers(1, 3, 1, 6, 1, 7, image);
                    threeCycleStickers(1, 1, 1, 8, 1, 4, image);
                    threeCycleStickers(2, 6, 0, 3, 7, 7, image);
                    threeCycleStickers(2, 1, 0, 4, 7, 8, image);
                    threeCycleStickers(0, 1, 7, 4, 2, 8, image);
                    threeCycleStickers(0, 0, 7, 2, 2, 5, image);
                    threeCycleStickers(2, 0, 0, 2, 7, 5, image);
                    threeCycleStickers(3, 1, 4, 8, 6, 4, image);
                    break;
                case 2: // F
                    threeCycleStickers(2, 2, 2, 0, 2, 5, image);
                    threeCycleStickers(2, 3, 2, 6, 2, 7, image);
                    threeCycleStickers(2, 1, 2, 8, 2, 4, image);
                    threeCycleStickers(3, 6, 1, 3, 6, 7, image);
                    threeCycleStickers(3, 0, 1, 2, 6, 5, image);
                    threeCycleStickers(3, 1, 1, 4, 6, 8, image);
                    threeCycleStickers(3, 5, 1, 0, 6, 2, image);
                    threeCycleStickers(3, 8, 1, 1, 6, 4, image);
                    threeCycleStickers(0, 1, 7, 8, 5, 4, image);
                    break;
                case 3: // L
                    threeCycleStickers(3, 3, 3, 6, 3, 7, image);
                    threeCycleStickers(3, 2, 3, 0, 3, 5, image);
                    threeCycleStickers(3, 4, 3, 1, 3, 8, image);
                    threeCycleStickers(0, 6, 2, 3, 5, 7, image);
                    threeCycleStickers(0, 8, 2, 1, 5, 4, image);
                    threeCycleStickers(0, 1, 2, 4, 5, 8, image);
                    threeCycleStickers(1, 1, 6, 8, 4, 4, image);
                    threeCycleStickers(0, 5, 2, 0, 5, 2, image);
                    threeCycleStickers(0, 0, 2, 2, 5, 5, image);
                    break;
                case 4: // B
                    threeCycleStickers(4, 0, 4, 5, 4, 2, image);
                    threeCycleStickers(4, 6, 4, 7, 4, 3, image);
                    threeCycleStickers(4, 1, 4, 8, 4, 4, image);
                    threeCycleStickers(5, 6, 7, 3, 0, 7, image);
                    threeCycleStickers(5, 5, 7, 0, 0, 2, image);
                    threeCycleStickers(5, 8, 7, 1, 0, 4, image);
                    threeCycleStickers(5, 0, 7, 2, 0, 5, image);
                    threeCycleStickers(5, 1, 7, 4, 0, 8, image);
                    threeCycleStickers(6, 1, 1, 8, 3, 4, image);
                    break;
                case 5: // BL
                    threeCycleStickers(5, 0, 5, 5, 5, 2, image);
                    threeCycleStickers(5, 6, 5, 7, 5, 3, image);
                    threeCycleStickers(5, 1, 5, 8, 5, 4, image);
                    threeCycleStickers(6, 6, 4, 3, 3, 7, image);
                    threeCycleStickers(6, 5, 4, 0, 3, 2, image);
                    threeCycleStickers(6, 8, 4, 1, 3, 4, image);
                    threeCycleStickers(6, 0, 4, 2, 3, 5, image);
                    threeCycleStickers(6, 1, 4, 4, 3, 8, image);
                    threeCycleStickers(7, 1, 0, 8, 2, 4, image);
                    break;
                case 6: // D
                    threeCycleStickers(6, 0, 6, 5, 6, 2, image);
                    threeCycleStickers(6, 3, 6, 6, 6, 7, image);
                    threeCycleStickers(6, 1, 6, 8, 6, 4, image);
                    threeCycleStickers(7, 6, 5, 3, 2, 7, image);
                    threeCycleStickers(7, 0, 5, 2, 2, 5, image);
                    threeCycleStickers(7, 1, 5, 4, 2, 8, image);
                    threeCycleStickers(7, 5, 5, 0, 2, 2, image);
                    threeCycleStickers(7, 8, 5, 1, 2, 4, image);
                    threeCycleStickers(1, 4, 4, 1, 3, 8, image);
                    break;
                case 7: // BR
                    threeCycleStickers(7, 2, 7, 0, 7, 5, image);
                    threeCycleStickers(7, 3, 7, 6, 7, 7, image);
                    threeCycleStickers(7, 4, 7, 1, 7, 8, image);
                    threeCycleStickers(4, 6, 6, 3, 1, 7, image);
                    threeCycleStickers(4, 5, 6, 0, 1, 2, image);
                    threeCycleStickers(4, 8, 6, 1, 1, 4, image);
                    threeCycleStickers(1, 5, 4, 0, 6, 2, image);
                    threeCycleStickers(1, 8, 4, 1, 6, 4, image);
                    threeCycleStickers(2, 8, 0, 4, 5, 1, image);
                    break;
                default:
                    assert false;
            }
        }
    }

    private int getPreferredWidth(){
        return (3 * 2 * STICKER_SIZE) + (CENTER_GAP_SIZE) + (2*MARGIN);
    }

    private int getPreferredHeight(){
        return (3 * STICKER_SIZE) + (2 * MARGIN);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getPreferredWidth(),
            getPreferredHeight());
    }

    @Override
    public PuzzleState getSolvedState() {
        return new FaceTurningOctahedronState();
    }

    @Override
    protected int getRandomMoveCount() {
        //34 moves is enough to produce a decently random scramble
        //not used in production for obvious reasons
        return 34;
    }
}

