package org.worldcubeassociation.tnoodle.puzzle;

import org.worldcubeassociation.tnoodle.svglite.*;

import java.util.*;
import java.util.logging.Logger;

import org.worldcubeassociation.tnoodle.scrambles.InvalidScrambleException;
import org.worldcubeassociation.tnoodle.scrambles.Puzzle;
import org.worldcubeassociation.tnoodle.scrambles.PuzzleStateAndGenerator;

import org.timepedia.exporter.client.Export;

@Export
public class ClockPuzzle extends Puzzle {
    private static final Logger l = Logger.getLogger(ClockPuzzle.class.getName());

    private static final String[] turns={"UR","DR","DL","UL","U","R","D","L","ALL"};
    private static final int STROKE_WIDTH = 2;
    private static final int FACE_STROKE_WIDTH = 1;
    private static final int radius = 70;
    private static final int clockRadius = 14;
    private static final int clockOuterRadius = 21;
    private static final int pointRadius = (clockRadius + clockOuterRadius) / 2;
    private static final int tickMarkRadius = 1;
    private static final int topTickMarkRadius = 2;
    private static final int arrowHeight = 10;
    private static final int arrowRadius = 2;
    private static final int pinRadius = 4;
    private static final double arrowAngle = Math.PI / 2 - Math.acos( (double)arrowRadius / (double)arrowHeight );

    private static final int gap = 5;

    @Override
    public String getLongName() {
        return "Clock";
    }

    @Override
    public String getShortName() {
        return "clock";
    }

    private static final int[][] moves = {
        {0,1,1,0,1,1,0,0,0,  -1, 0, 0, 0, 0, 0, 0, 0, 0},// UR
        {0,0,0,0,1,1,0,1,1,   0, 0, 0, 0, 0, 0,-1, 0, 0},// DR
        {0,0,0,1,1,0,1,1,0,   0, 0, 0, 0, 0, 0, 0, 0,-1},// DL
        {1,1,0,1,1,0,0,0,0,   0, 0,-1, 0, 0, 0, 0, 0, 0},// UL
        {1,1,1,1,1,1,0,0,0,  -1, 0,-1, 0, 0, 0, 0, 0, 0},// U
        {0,1,1,0,1,1,0,1,1,  -1, 0, 0, 0, 0, 0,-1, 0, 0},// R
        {0,0,0,1,1,1,1,1,1,   0, 0, 0, 0, 0, 0,-1, 0,-1},// D
        {1,1,0,1,1,0,1,1,0,   0, 0,-1, 0, 0, 0, 0, 0,-1},// L
        {1,1,1,1,1,1,1,1,1,  -1, 0,-1, 0, 0, 0,-1, 0,-1},// A
    };

    private static final Map<String, Color> defaultColorScheme = new HashMap<>();
    static {
        Color bright = new Color(0xccddee);
        Color dark = new Color(0x113366);

        defaultColorScheme.put("Front", dark);
        defaultColorScheme.put("FrontClock", bright);
        defaultColorScheme.put("FrontTopClock", new Color(0xffcc44));
        defaultColorScheme.put("FrontHand", dark);
        defaultColorScheme.put("FrontHandBorder", dark);
        defaultColorScheme.put("FrontPin", new Color(0x88aacc));
        defaultColorScheme.put("Back", bright);
        defaultColorScheme.put("BackClock", dark);
        defaultColorScheme.put("BackTopClock", new Color(0xcc6600));
        defaultColorScheme.put("BackHand", bright);
        defaultColorScheme.put("BackHandBorder", bright);
        defaultColorScheme.put("BackPin", new Color(0x446699));
    }
    @Override
    public Map<String, Color> getDefaultColorScheme() {
        return new HashMap<>(defaultColorScheme);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(4*(radius+gap), 2*(radius+gap));
    }

    @Override
    public PuzzleState getSolvedState() {
        return new ClockState();
    }

    @Override
    protected int getRandomMoveCount() {
        return 19;
    }

    @Override
    public PuzzleStateAndGenerator generateRandomMoves(Random r) {
        StringBuilder scramble = new StringBuilder();

        for(int x=0; x<9; x++) {
            int turn = r.nextInt(12)-5;
            boolean clockwise = ( turn >= 0 );
            turn = Math.abs(turn);
            scramble.append(turns[x]).append(turn).append(clockwise ? "+" : "-").append(" ");
        }
        scramble.append( "y2 ");
        for(int x=4; x<9; x++) {
            int turn = r.nextInt(12)-5;
            boolean clockwise = ( turn >= 0 );
            turn = Math.abs(turn);
            scramble.append(turns[x]).append(turn).append(clockwise ? "+" : "-").append(" ");
        }

        String scrambleStr = scramble.toString().trim();

        PuzzleState state = getSolvedState();
        try {
            state = state.applyAlgorithm(scrambleStr);
        } catch(InvalidScrambleException e) {
            throw new RuntimeException(e);
        }
        return new PuzzleStateAndGenerator(state, scrambleStr);
    }

    public class ClockState extends PuzzleState {

        private final int[] posit;
        private final boolean rightSideUp;
        public ClockState() {
            posit = new int[] {0,0,0,0,0,0,0,0,0,  0,0,0,0,0,0,0,0,0};
            rightSideUp = true;
        }

        public ClockState(int[] posit, boolean rightSideUp) {
            this.posit = posit;
            this.rightSideUp = rightSideUp;
        }

        @Override
        public Map<String, PuzzleState> getSuccessorsByName() {
            Map<String, PuzzleState> successors = new LinkedHashMap<>();

            for(int turn = 0; turn < turns.length; turn++) {
                for(int rot = 0; rot < 12; rot++) {
                    // Apply the move
                    int[] positCopy = new int[18];
                    for( int p=0; p<18; p++) {
                        positCopy[p] = (posit[p] + rot*moves[turn][p] + 12)%12;
                    }

                    // Build the move string
                    boolean clockwise = ( rot < 7 );
                    String move = turns[turn] + (clockwise?(rot+"+"):((12-rot)+"-"));

                    successors.put(move, new ClockState(positCopy, rightSideUp));
                }
            }

            // Still y2 to implement
            int[] positCopy = new int[18];
            System.arraycopy(posit, 0, positCopy, 9, 9);
            System.arraycopy(posit, 9, positCopy, 0, 9);
            successors.put("y2", new ClockState(positCopy, !rightSideUp));

            return successors;
        }

        @Override
        public boolean equals(Object other) {
            ClockState o = ((ClockState) other);
            return Arrays.equals(posit, o.posit);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(posit);
        }

        @Override
        protected Svg drawScramble(Map<String, Color> colorScheme) {
            Svg svg = new Svg(getPreferredSize());
            svg.setStroke(STROKE_WIDTH, 10, "round");
            drawBackground(svg, colorScheme);

            for(int i = 0; i < 18; i++) {
                drawClock(svg, i, posit[i], colorScheme);
            }

            drawPins(svg, colorScheme);
            return svg;
        }

        protected void drawBackground(Svg g, Map<String, Color> colorScheme) {
            String[] colorString;
            if(rightSideUp) {
                colorString = new String[]{"Front", "Back"};
            } else {
                colorString = new String[]{"Back", "Front"};
            }

            for(int s = 0; s < 2; s++) {
                Transform t = Transform.getTranslateInstance((s*2+1)*(radius + gap), radius + gap);

                // Draw puzzle
                for(int centerX : new int[] { -2*clockOuterRadius, 2*clockOuterRadius }) {
                    for(int centerY : new int[] { -2*clockOuterRadius, 2*clockOuterRadius }) {
                        Circle c = new Circle(centerX, centerY, clockOuterRadius);
                        c.setTransform(t);
                        c.setStroke(Color.BLACK);
                        g.appendChild(c);
                    }
                }

                Circle outerCircle = new Circle(0, 0, radius);
                outerCircle.setTransform(t);
                outerCircle.setStroke(Color.BLACK);
                outerCircle.setFill(colorScheme.get(colorString[s]));
                g.appendChild(outerCircle);

                for(int centerX : new int[] { -2*clockOuterRadius, 2*clockOuterRadius }) {
                    for(int centerY : new int[] { -2*clockOuterRadius, 2*clockOuterRadius }) {
                        // We don't want to clobber part of our nice
                        // thick outer border.
                        float innerClockOuterRadius = clockOuterRadius - STROKE_WIDTH/2f;
                        Circle c = new Circle(centerX, centerY, innerClockOuterRadius);
                        c.setTransform(t);
                        c.setFill(colorScheme.get(colorString[s]));
                        g.appendChild(c);
                    }
                }

                // Draw clocks
                for(int i = -1; i <= 1; i++) {
                    for(int j = -1; j <= 1; j++) {
                        Transform tCopy = new Transform(t);
                        tCopy.translate(2*i*clockOuterRadius, 2*j*clockOuterRadius);


                        Circle clockFace = new Circle(0, 0, clockRadius);
                        clockFace.setStroke(FACE_STROKE_WIDTH, 10, "round");
                        clockFace.setStroke(Color.BLACK);
                        clockFace.setFill(colorScheme.get(colorString[s]+ "Clock"));
                        clockFace.setTransform(tCopy);
                        g.appendChild(clockFace);

                        for(int k = 0; k < 12; k++) {
                            Circle tickMark = new Circle(0, -pointRadius, k == 0 ? topTickMarkRadius : tickMarkRadius);
                            tickMark.setFill(colorScheme.get(colorString[s] + (k == 0 ? "Top" : "") +  "Clock"));
                            tickMark.rotate(Math.toRadians(30*k));
                            tickMark.transform(tCopy);
                            g.appendChild(tickMark);
                        }

                    }
                }
            }
        }

        protected void drawClock(Svg g, int clock, int position, Map<String, Color> colorScheme) {
            Transform t = new Transform();
            t.rotate(Math.toRadians(position*30));
            int netX = 0;
            int netY = 0;
            int deltaX, deltaY;
            String sidePrefix = ((clock < 9) ^ (rightSideUp)) ? "Back" : "Front";
            if(clock < 9) {
                deltaX = radius + gap;
                deltaY = radius + gap;
                t.translate(deltaX, deltaY);
                netX += deltaX;
                netY += deltaY;
            } else {
                deltaX = 3*(radius + gap);
                deltaY = radius + gap;
                t.translate(deltaX, deltaY);
                netX += deltaX;
                netY += deltaY;
                clock -= 9;
            }

            deltaX = 2*((clock%3) - 1)*clockOuterRadius;
            deltaY = 2*((clock/3) - 1)*clockOuterRadius;
            t.translate(deltaX, deltaY);
            netX += deltaX;
            netY += deltaY;

            Path arrow = new Path();
            arrow.moveTo(0, 0);
            arrow.lineTo(arrowRadius*Math.cos(arrowAngle), -arrowRadius*Math.sin(arrowAngle));
            arrow.lineTo(0, -arrowHeight);
            arrow.lineTo(-arrowRadius*Math.cos( arrowAngle ), -arrowRadius*Math.sin(arrowAngle));
            arrow.closePath();
            arrow.setStroke(colorScheme.get(sidePrefix + "HandBorder"));
            arrow.setTransform(t);
            g.appendChild(arrow);

            Circle handBase = new Circle(0, 0, arrowRadius);
            handBase.setStroke(colorScheme.get(sidePrefix + "HandBorder"));
            handBase.setTransform(t);
            g.appendChild(handBase);

            arrow = new Path(arrow);
            arrow.setFill(colorScheme.get(sidePrefix + "Hand"));
            arrow.setStroke(null);
            arrow.setTransform(t);
            g.appendChild(arrow);

            handBase = new Circle(handBase);
            handBase.setFill(colorScheme.get(sidePrefix + "Hand"));
            handBase.setStroke(null);
            handBase.setTransform(t);
            g.appendChild(handBase);
        }

        protected void drawPins(Svg g, Map<String, Color> colorScheme) {
            Transform t = new Transform();
            t.translate(radius + gap, radius + gap);
            for(int i = -1; i <= 1; i += 2) {
                for(int j = -1; j <= 1; j += 2) {
                    Transform tt = new Transform(t);
                    tt.translate(j*clockOuterRadius, i*clockOuterRadius);
                    drawPin(g, tt, colorScheme.get(rightSideUp ? "BackPin" : "FrontPin"));
                }
            }

            t.translate(2*(radius + gap), 0);
            for(int i = -1; i <= 1; i += 2) {
                for(int j = -1; j <= 1; j += 2) {
                    Transform tt = new Transform(t);
                    tt.translate(j*clockOuterRadius, i*clockOuterRadius);
                    drawPin(g, tt, colorScheme.get(rightSideUp ? "FrontPin" :  "BackPin" ));
                }
            }
        }

        protected void drawPin(Svg g, Transform t, Color color) {
            Circle pin = new Circle(0, 0, pinRadius);
            pin.setTransform(t);
            pin.setFill(color);
            g.appendChild(pin);
        }

    }
}
