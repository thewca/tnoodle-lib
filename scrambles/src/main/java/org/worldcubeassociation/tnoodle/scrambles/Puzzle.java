package org.worldcubeassociation.tnoodle.scrambles;

import org.worldcubeassociation.tnoodle.svglite.Color;
import org.worldcubeassociation.tnoodle.svglite.Dimension;
import org.worldcubeassociation.tnoodle.svglite.InvalidHexColorException;
import org.worldcubeassociation.tnoodle.svglite.Svg;
import org.worldcubeassociation.tnoodle.svglite.Group;
import org.worldcubeassociation.tnoodle.svglite.Element;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.worldcubeassociation.tnoodle.scrambles.AlgorithmBuilder.MergingMode;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportClosure;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.NoExport;

import static java.lang.Math.ceil;

/**
 * Puzzle and TwistyPuzzle encapsulate all the information to filter out
 * scrambles &lt;= wcaMinScrambleDistance (defaults to 1)
 * move away from solved (see generateWcaScramble),
 * and to generate random turn scrambles generically (see generateRandomMoves).
 *
 * The original proposal for these classes is accessible here:
 * https://docs.google.com/document/d/11ZfQPxAw0EhNNwE1yn5lZUO383qvAH6kJa2s3O9_6Zg/edit
 *
 * @author jeremy
 *
 */
@ExportClosure
public abstract class Puzzle implements Exportable {
    protected static final Logger l = Logger.getLogger(Puzzle.class.getName());
    protected int wcaMinScrambleDistance = 2;

    /**
     * Returns a String describing this Scrambler
     * appropriate for use in a url. This shouldn't contain any periods.
     * @return a url appropriate String unique to this Scrambler
     */
    @Export
    public abstract String getShortName();

    /**
     * Returns a String fully describing this Scrambler.
     * Unlike shortName(), may contain spaces and other url-inappropriate characters.
     * This will also be used for the toString method of this Scrambler.
     * @return a String
     */
    @Export
    public abstract String getLongName();

    /**
     * Returns a number between 0 and 1 representing how "initialized" this
     * Scrambler is. 0 means nothing has been accomplished, and 1 means
     * we're done, and are generating scrambles.
     * @return A double between 0 and 1, inclusive.
     */

    public double getInitializationStatus() {
        return 1;
    }

    /**
     * Returns the minimum distance from solved that any scramble this Puzzle
     * generates will be.
     *
     * @return The integer representing the exact minimum scramble distance
     */
    public int getWcaMinScrambleDistance() {
        return wcaMinScrambleDistance;
    }

    /**
     * Generates a scramble appropriate for this Scrambler. It's important to note that
     * it's ok if this method takes some time to run, as it's going to be called many times and get queued up
     * by ScrambleCacher.
     * NOTE:  If a puzzle wants to provide custom scrambles
     * (for example: Pochmann style megaminx or MRSS), it should override generateRandomMoves.
     * @param r The instance of Random you must use as your source of randomness when generating scrambles.
     * @return A String containing the scramble, where turns are assumed to be separated by whitespace.
     */
    public final String generateWcaScramble(Random r) {
        PuzzleStateAndGenerator psag;
        do {
            psag = generateRandomMoves(r);
        } while(psag.state.solveIn(wcaMinScrambleDistance - 1) != null);
        return psag.generator;
    }

    /**
     * @return A *new* HashMap mapping face names to Colors.
     */
    public abstract Map<String, Color> getDefaultColorScheme();

    private String[] generateScrambles(Random r, int count) {
        String[] scrambles = new String[count];
        for(int i = 0; i < count; i++) {
            scrambles[i] = generateWcaScramble(r);
        }
        return scrambles;
    }

    private SecureRandom r = getSecureRandom();
    public static final SecureRandom getSecureRandom() {
        try {
            try {
                return SecureRandom.getInstance("SHA1PRNG", "SUN");
            } catch(NoSuchProviderException e) {
                l.log(Level.SEVERE, "Couldn't get SecureRandomInstance", e);
                return SecureRandom.getInstance("SHA1PRNG");
            }
        } catch(NoSuchAlgorithmException e) {
            l.log(Level.SEVERE, "Couldn't get SecureRandomInstance", e);
            throw new RuntimeException(e);
        }
    }

    @Export
    public final String generateScramble() {
        return generateWcaScramble(r);
    }
    @Export
    public final String[] generateScrambles(int count) {
        return generateScrambles(r, count);
    }

    /**
     * seeded scrambles, these can't be cached, so they'll be a little slower
     *
     * @param seed The seed to be used for generating this scramble
     * @return A scramble similar to {@link #generateScramble}, except that it is guaranteed to be based on {@code seed}
     */
    @Export
    public final String generateSeededScramble(String seed) {
        return generateSeededScramble(seed.getBytes());
    }
    @Export
    public final String[] generateSeededScrambles(String seed, int count) {
        return generateSeededScrambles(seed.getBytes(), count);
    }

    private String generateSeededScramble(byte[] seed) {
        // We must create our own Random because
        // other threads can access the static one.
        // Also, setSeed supplements an existing seed,
        // rather than replacing it.
        // TODO - consider using something other than SecureRandom for seeded scrambles,
        // because we really, really want this to be portable across platforms (desktop java, gwt, and android)
        // https://github.com/thewca/tnoodle/issues/146
        SecureRandom r = getSecureRandom();
        r.setSeed(seed);
        return generateWcaScramble(r);
    }
    private String[] generateSeededScrambles(byte[] seed, int count) {
        // We must create our own Random because
        // other threads can access the static one.
        // Also, setSeed supplements an existing seed,
        // rather than replacing it.
        SecureRandom r = getSecureRandom();
        r.setSeed(seed);
        return generateScrambles(r, count);
    }

    /**
     * @return Simply returns getLongName()
     */
    @Export
    public String toString() {
        return getLongName();
    }

    /**
     * TODO - document! alphabetical
     * @return TODO, see above
     */
    @Export
    public String[] getFaceNames() {
        return getDefaultColorScheme().keySet().stream()
            .sorted()
            .toArray(String[]::new);
    }

    /**
     * TODO - document!
     * @param scheme TODO, see above
     * @return TODO, see above
     */
    public Map<String, Color> parseColorScheme(String scheme) {
        Map<String, Color> colorScheme = getDefaultColorScheme();
        if(scheme != null && !scheme.isEmpty()) {
            String[] faces = getFaceNames();
            String[] colors;
            if(scheme.indexOf(',') > 0) {
                colors = scheme.split(",");
            } else {
                char[] cols = scheme.toCharArray();
                colors = new String[cols.length];
                for(int i = 0; i < cols.length; i++) {
                    colors[i] = cols[i] + "";
                }
            }
            if(colors.length != faces.length) {
//              sendText(t, String.format("Incorrect number of colors specified (expecting %d, got %d)", faces.length, colors.length));
                //TODO - exception
                return null;
            }
            for(int i = 0; i < colors.length; i++) {
                try {
                    Color c = new Color(colors[i]);
                    colorScheme.put(faces[i], c);
                } catch(InvalidHexColorException e) {
//                  sendText(t, "Invalid color: " + colors[i]);
                    //TODO - exception
                    return null;
                }
            }
        }
        return colorScheme;
    }

    /**
     * Draws scramble as an Svg.
     * @param scramble The scramble to validate and apply to the puzzle. NOTE: May be null.
     * @param colorScheme A HashMap mapping face names to Colors.
     *          Any missing entries will be merged with the defaults from getDefaultColorScheme().
     *          If null, just the defaults are used.
     * @return An SVG object representing the drawn scramble.
     * @throws InvalidScrambleException If scramble is invalid.
     */
    public Svg drawScramble(String scramble, Map<String, Color> colorScheme) throws InvalidScrambleException {
        if(scramble == null) {
            scramble = "";
        }
        Map<String, Color> colorSchemeCopy = colorScheme;
        colorScheme = getDefaultColorScheme();
        if(colorSchemeCopy != null) {
            colorScheme.putAll(colorSchemeCopy);
        }

        PuzzleState state = getSolvedState();
        state = state.applyAlgorithm(scramble);
        Svg svg = state.drawScramble(colorScheme);

        // This is a hack I don't fully understand that prevents aliasing of
        // vertical and horizontal lines.
        // See http://stackoverflow.com/questions/7589650/drawing-grid-with-jquery-svg-produces-2px-lines-instead-of-1px
        Group g = new Group();
        List<Element> children = svg.getChildren();
        while(!children.isEmpty()) {
            g.appendChild(children.remove(0));
        }
        g.translate(0.5, 0.5);
        svg.appendChild(g);
        return svg;
    }

    public abstract Dimension getPreferredSize();

    /**
     * Computes the best size to draw the scramble image.
     * @param maxWidth The maximum allowed width of the resulting image, 0 if it doesn't matter.
     * @param maxHeight The maximum allowed height of the resulting image, 0 if it doesn't matter.
     * @return The best size of the resulting image, constrained to maxWidth and maxHeight.
     */
    @Export
    public Dimension getPreferredSize(int maxWidth, int maxHeight) {
        if(maxWidth == 0 && maxHeight == 0) {
            return getPreferredSize();
        }
        if(maxWidth == 0) {
            maxWidth = Integer.MAX_VALUE;
        } else if(maxHeight == 0) {
            maxHeight = Integer.MAX_VALUE;
        }
        double ratio = 1.0 * getPreferredSize().width / getPreferredSize().height;
        int resultWidth = (int) Math.min(maxWidth, ceil(maxHeight*ratio));
        int resultHeight = (int) Math.min(maxHeight, ceil(maxWidth/ratio));
        return new Dimension(resultWidth, resultHeight);
    }

    public static class Bucket<H> implements Comparable<Bucket<H>> {
        private final LinkedList<H> contents;
        private final int value;
        public Bucket(int value) {
            this.value = value;
            this.contents = new LinkedList<>();
        }

        public int getValue() {
            return this.value;
        }

        public H pop() {
            return contents.removeLast();
        }

        public void push(H element) {
            contents.addLast(element);
        }

        public boolean isEmpty() {
            return contents.isEmpty();
        }

        public String toString() {
            return "#: " + value + ": " + contents;
        }

        @Override
        public int compareTo(Bucket<H> other) {
            return this.value - other.value;
        }

        public int hashCode() {
            return this.value;
        }

        public boolean equals(Object o) {
            Bucket<?> other = (Bucket<?>) o;
            return this.value == other.value;
        }
    }

    public static class SortedBuckets<H> {
        private final TreeSet<Bucket<H>> buckets;
        public SortedBuckets() {
            buckets = new TreeSet<>();
        }

        public void add(H element, int value) {
            Bucket<H> bucket;
            Bucket<H> searchBucket = new Bucket<>(value);
            if(!buckets.contains(searchBucket)) {
                // There is no bucket yet for value, so we create one.
                bucket = searchBucket;
                buckets.add(bucket);
            } else {
                bucket = buckets.tailSet(searchBucket).first();
            }
            bucket.push(element);
        }

        public int smallestValue() {
            return buckets.first().getValue();
        }

        public boolean isEmpty() {
            return buckets.size() == 0;
        }

        public H pop() {
            Bucket<H> bucket = buckets.first();
            H h = bucket.pop();
            if(bucket.isEmpty()) {
                // We just removed the last element from this bucket,
                // so we can trash the bucket now.
                buckets.remove(bucket);
            }
            return h;
        }

        public String toString() {
            return buckets.toString();
        }

        public int hashCode() {
            throw new UnsupportedOperationException();
        }

        public boolean equals(Object o) {
            throw new UnsupportedOperationException();
        }
    }

    protected String solveIn(PuzzleState ps, int n) {
        if(ps.isSolved()) {
            return "";
        }

        Map<PuzzleState, Integer> seenSolved = new HashMap<>();
        SortedBuckets<PuzzleState> fringeSolved = new SortedBuckets<>();
        Map<PuzzleState, Integer> seenScrambled = new HashMap<>();
        SortedBuckets<PuzzleState> fringeScrambled = new SortedBuckets<>();

        // We're only interested in solutions of cost <= n
        int bestIntersectionCost = n + 1;
        PuzzleState bestIntersection = null;

        PuzzleState solvedNormalized = getSolvedState().getNormalized();
        fringeSolved.add(solvedNormalized, 0);
        seenSolved.put(solvedNormalized, 0);
        fringeScrambled.add(ps.getNormalized(), 0);
        seenScrambled.put(ps.getNormalized(), 0);

        int fringeTies = 0;

        // The task here is to do a breadth-first search starting from both the solved state and the scrambled state.
        // When we got an intersection from the two hash maps, we are done!
        int minFringeScrambled = -1, minFringeSolved = -1;
        while(!fringeSolved.isEmpty() || !fringeScrambled.isEmpty()) {
            // We have to choose on which side we are extending our search.
            // I'm choosing the non empty fringe with the node nearest
            // its origin. In the event of a tie, we make sure to alternate.
            if(!fringeScrambled.isEmpty()) {
                minFringeScrambled = fringeScrambled.smallestValue();
            }
            if(!fringeSolved.isEmpty()) {
                minFringeSolved = fringeSolved.smallestValue();
            }
            boolean extendSolved;
            if(fringeSolved.isEmpty() || fringeScrambled.isEmpty()) {
                // If the solved fringe is not empty, we'll expand it.
                // Otherwise, we're expanding the scrambled fringe.
                extendSolved = !fringeSolved.isEmpty();
            } else {
                if(minFringeSolved < minFringeScrambled) {
                    extendSolved = true;
                } else if(minFringeSolved > minFringeScrambled) {
                    extendSolved = false;
                } else {
                    extendSolved = (fringeTies++) % 2 == 0;
                }
            }

            // We are using references for a more concise code.
            Map<PuzzleState, Integer> seenExtending;
            SortedBuckets<PuzzleState> fringeExtending;
            Map<PuzzleState, Integer> seenComparing;
            SortedBuckets<PuzzleState> fringeComparing;
            int minExtendingFringe, minComparingFringe;
            if(extendSolved) {
                seenExtending = seenSolved;
                fringeExtending = fringeSolved;
                minExtendingFringe = minFringeSolved;
                seenComparing = seenScrambled;
                fringeComparing = fringeScrambled;
                minComparingFringe = minFringeScrambled;
            } else {
                seenExtending = seenScrambled;
                fringeExtending = fringeScrambled;
                minExtendingFringe = minFringeScrambled;
                seenComparing = seenSolved;
                fringeComparing = fringeSolved;
                minComparingFringe = minFringeSolved;
            }

            PuzzleState node = fringeExtending.pop();
            int distance = seenExtending.get(node);
            if(seenComparing.containsKey(node)) {
                // We found an intersection! Compute the total cost of the
                // path going through this node.
                int cost = seenComparing.get(node) + distance;
                if(cost < bestIntersectionCost) {
                    bestIntersection = node;
                    bestIntersectionCost = cost;
                }
                continue;
            }
            // The best possible solution involving this node would
            // be through a child of this node that gets us across to
            // the other fringe's smallest distance node.
            int bestPossibleSolution = distance + minComparingFringe;
            if(bestPossibleSolution >= bestIntersectionCost) {
                continue;
            }
            if(distance >= (n+1)/2) {
                // The +1 is because if n is odd, we would have to search
                // from one side with distance n/2 and from the other side
                // distance n/2 + 1. Because we don't know which is which,
                // let's take (n+1)/2 for both.
                continue;
            }


            Map<? extends PuzzleState, String> movesByState = node.getCanonicalMovesByState();
            for(PuzzleState next : movesByState.keySet()) {
                int moveCost = node.getMoveCost(movesByState.get(next));
                int nextDistance = distance + moveCost;
                next = next.getNormalized();
                if(seenExtending.containsKey(next)) {
                    if(nextDistance >= seenExtending.get(next)) {
                        // We already found a better path to next.
                        continue;
                    }
                    // Go on to clobber seenExtending with our updated
                    // distance. Unfortunately, we're going have 2 copies
                    // of next in our fringe. This doesn't change correctness,
                    // it just means a bit of wasted work when we get around
                    // to popping off the second one.
                }
                fringeExtending.add(next, nextDistance);
                seenExtending.put(next, nextDistance);
            }
        }

        if(bestIntersection == null) {
            return null;
        }

        // We have found a solution, but we still have to recover the move sequence.
        // the `bestIntersection` is the bound between the solved and the scrambled states.
        // We can travel from `bestIntersection` to either states, like that:
        // solved <----- bestIntersection -----> scrambled
        // However, to build a solution, we need to travel like that:
        // solved <----- bestIntersection <----- scrambled
        // So we have to travel backward for the scrambled side.

        // Step 1: bestIntersection -----> scrambled

        assert bestIntersection.isNormalized();
        PuzzleState state = bestIntersection;
        int distanceFromScrambled = seenScrambled.get(state);

        // We have to keep track of all states we have visited
        PuzzleState[] linkedStates = new PuzzleState[distanceFromScrambled + 1];
        linkedStates[distanceFromScrambled] = state;

    outer:
        while(distanceFromScrambled > 0) {
            for(PuzzleState next : state.getCanonicalMovesByState().keySet()) {
                next = next.getNormalized();
                if(seenScrambled.containsKey(next)) {
                    int newDistanceFromScrambled = seenScrambled.get(next);
                    if(newDistanceFromScrambled < distanceFromScrambled) {
                        state = next;
                        distanceFromScrambled = newDistanceFromScrambled;
                        linkedStates[distanceFromScrambled] = state;
                        continue outer;
                    }
                }
            }
            assert false;
        }

        // Step 2: bestIntersection <----- scrambled

        AlgorithmBuilder solution = new AlgorithmBuilder(MergingMode.CANONICALIZE_MOVES, ps);
        state = ps;
        distanceFromScrambled = 0;

    outer:
        while(!state.equalsNormalized(bestIntersection)) {
            for(Entry<? extends PuzzleState, String> next : state.getCanonicalMovesByState().entrySet()) {
                PuzzleState nextState = next.getKey();
                String moveName = next.getValue();
                if(nextState.equalsNormalized(linkedStates[distanceFromScrambled+1])) {
                    state = nextState;
                    try {
                        solution.appendMove(moveName);
                    } catch(InvalidMoveException e) {
                        throw new RuntimeException(e);
                    }
                    distanceFromScrambled = seenScrambled.get(state.getNormalized());
                    continue outer;
                }
            }
            assert false;
        }

        // Step 3: solved <----- bestIntersection

        int distanceFromSolved = seenSolved.get(state.getNormalized());
    outer:
        while(distanceFromSolved > 0) {
            for(Entry<? extends PuzzleState, String> next : state.getCanonicalMovesByState().entrySet()) {
                PuzzleState nextState = next.getKey();
                PuzzleState nextStateNormalized = nextState.getNormalized();
                String moveName = next.getValue();
                if(seenSolved.containsKey(nextStateNormalized)) {
                    int newDistanceFromSolved = seenSolved.get(nextStateNormalized);
                    if(newDistanceFromSolved < distanceFromSolved) {
                        state = nextState;
                        distanceFromSolved = newDistanceFromSolved;
                        try {
                            solution.appendMove(moveName);
                        } catch(InvalidMoveException e) {
                            throw new RuntimeException(e);
                        }
                        continue outer;
                    }
                }
            }
            assert false;
        }

        return solution.toString();
    }

    public abstract class PuzzleState {
        public PuzzleState() {}

        /**
         *
         * @param algorithm A space separated String of moves to apply to state
         * @return The resulting PuzzleState
         * @throws InvalidScrambleException If the scramble is invalid, for example if it uses invalid notation.
         */
        public PuzzleState applyAlgorithm(String algorithm) throws InvalidScrambleException {
            PuzzleState state = this;
            for(String move : AlgorithmBuilder.splitAlgorithm(algorithm)) {
                try {
                    state = state.apply(move);
                } catch(InvalidMoveException e) {
                    throw new InvalidScrambleException(algorithm, e);
                }
            }
            return state;
        }

        /**
         * Canonical successors are all the successor states that
         * are "normalized" unique.
         * @return A mapping of canonical PuzzleState's to the name of
         *         the move that gets you to them.
         */
        public Map<? extends PuzzleState, String> getCanonicalMovesByState() {
            Map<String, ? extends PuzzleState> successorsByName =
                getSuccessorsByName();
            Map<PuzzleState, String> uniqueSuccessors =
                new HashMap<>();
            Set<PuzzleState> statesSeenNormalized = new HashSet<>();
            // We're not interested in any successor states are just a
            // rotation away.
            statesSeenNormalized.add(this.getNormalized());
            for(Entry<String, ? extends PuzzleState> next : successorsByName.entrySet()) {
                PuzzleState nextState = next.getValue();
                PuzzleState nextStateNormalized = nextState.getNormalized();
                String moveName = next.getKey();
                // Only add nextState if it's "unique"
                if(!statesSeenNormalized.contains(nextStateNormalized)) {
                    uniqueSuccessors.put(nextState, moveName);
                    statesSeenNormalized.add(nextStateNormalized);
                }
            }

            return uniqueSuccessors;
        }

        /**
         * There exist PuzzleState's that are 0 moves apart, but are
         * not .equal(). This is because we consider the visibly different
         * PuzzleState's to be not equals (consider the state achieved by
         * applying L to a solved 3x3x3, and the state after applying Rw.
         * These puzzles "look" different, but they are 0 moves apart.
         * @return A PuzzleState that all rotations of state will all
         *         return when normalized. This makes it possible to check
         *         if 2 puzzle states are 0 moves apart, even if they
         *         "look" different.
         * TODO - This method could be implemented in this superclass by
         *        defining a "cost" for moves (which we will have to do for
         *        sq1 anyways), and walking the complete
         *        0 cost state tree for this state. Then we'd return one
         *        element from that state tree in a deterministic way.
         *        We could do something simple like returning the state
         *        that has the smallest hash, but that wouldn't work if
         *        we have hash collisions. I think the best thing to do
         *        would be to require all PuzzleStates to implement
         *        a marshall() function that returns a unique string. Then
         *        we can just do an alphabetical sort of these and return the
         *        min or max.
         */
        public PuzzleState getNormalized() {
            return this;
        }

        public boolean isNormalized() {
            return this.equals(getNormalized());
        }

        /**
         * Most puzzles are happy to split an algorithm by turns, and declare
         * each turn a move. However, this simple model doesn't work for all
         * puzzles. For example, square one may wish to declare (3,3) as 1
         * move. Another possible use for this would be rotations, which
         * count as 0 moves.
         * @param move The move for which to compute costs
         * @return The cost of doing this move.
         */
        public int getMoveCost(String move) {
            return 1;
        }

        /**
         * @return A LinkedHashMap mapping move Strings to resulting PuzzleStates.
         *         The move Strings may not contain spaces.
         *         Multiple keys (moves) in the returned LinkedHashMap may
         *         map to the same state, or states that are .equal().
         *         Preferred notations should appear earlier in the
         *         LinkedHashMap.
         */
        public abstract Map<String, ? extends PuzzleState> getSuccessorsByName();

        /**
         * By default, this method returns getSuccessorsByName(). Some
         * puzzles may wish to override this method to provide a reduced set
         * of moves to be used for scrambling.
         * <br><br>
         * One example of where this is useful is a puzzle like the square
         * one. Someone extending Puzzle to implement SquareOnePuzzle is left
         * with the question of whether to allow turns that leave the puzzle
         * incapable of doing a /.
         * <br><br>
         * If getSuccessorsByName() returns states that cannot do a /, then
         * generateRandomMoves() will hang because any move that can be
         * applied to one of those states is redundant.
         * <br><br>
         * Alternatively, if getSuccessorsByName() only returns states that
         * can do a /, AlgorithmBuilder's isRedundant() breaks.
         * Here's why:<br>
         * Imagine a solved square one. Lets say we pick the turn (1,0) to
         * apply to it, and now we're considering applying (2,0) to it.
         * Obviously this is the exact same state you would have achieved by
         * just applying (3,0) to the solved puzzle, but isRedundant()
         * only checks for this against the previous moves that commute with
         * (2,0). movesCommute("(1,0)", "(2,0)") will only return
         * true if (2,0) can be applied to a solved square one, even though
         * it results in a state that cannot
         * be slashed.

         * @return A HashMap mapping move Strings to resulting PuzzleStates.
         *         The move Strings may not contain spaces.
         */
        public Map<String, ? extends PuzzleState> getScrambleSuccessors() {
            Map<String, PuzzleState> reversed = new HashMap<>();

            for (Map.Entry<? extends PuzzleState, String> entry : getCanonicalMovesByState().entrySet()) {
                reversed.put(entry.getValue(), entry.getKey());
            }

            return reversed;
        }

        /**
         * Returns true if this state is equal to other.
         * Note that a puzzle like 4x4 must compare all orientations of the puzzle, otherwise
         * generateRandomMoves() will allow for trivial sequences of turns like Lw Rw'.
         * @param other The other object to check for equality
         * @return true if this is equal to other
         */
        public abstract boolean equals(Object other);
        public abstract int hashCode();

        public boolean equalsNormalized(PuzzleState other) {
            return getNormalized().equals(other.getNormalized());
        }

        /**
         * Draws the state of the puzzle.
         * NOTE: It is assumed that this method is thread safe! That means unless you know what you're doing,
         * use the synchronized keyword when implementing this method:<br>
         * <code>protected synchronized void drawScramble();</code>
         * @param colorScheme The color scheme to use while drawing
         * @return An Svg instance representing this scramble.
         */
        protected abstract Svg drawScramble(Map<String, Color> colorScheme);

        public Puzzle getPuzzle() {
            return Puzzle.this;
        }

        public boolean isSolved() {
            return equalsNormalized(getPuzzle().getSolvedState());
        }

        /**
         * Applies the given move to this PuzzleState. This method is non destructive,
         * that is, it does not mutate the current state, instead it returns a new state.
         * @param move The move to apply
         * @return The PuzzleState achieved after applying move
         * @throws InvalidMoveException if the move is unrecognized.
         */
        public PuzzleState apply(String move) throws InvalidMoveException {
            Map<String, ? extends PuzzleState> successors = getSuccessorsByName();
            if(!successors.containsKey(move)) {
                throw new InvalidMoveException("Unrecognized turn " + move);
            }
            return successors.get(move);
        }

        public String solveIn(int n) {
            return getPuzzle().solveIn(this, n);
        }

        /**
         * Two moves A and B commute on a puzzle if regardless of
         * the order you apply A and B, you end up in the same state.
         * Interestingly enough, the set of moves that commute can change
         * with the state a puzzle is in. That's why this is a method of
         * PuzzleState instead of Puzzle.
         * @param move1
         * @param move2
         * @return True iff move1 and move2 commute.
         */
        boolean movesCommute(String move1, String move2) {
            try {
                PuzzleState state1 = apply(move1).apply(move2);
                PuzzleState state2 = apply(move2).apply(move1);
                return state1.equals(state2);
            } catch (InvalidMoveException e) {
                return false;
            }
        }
    }

    /**
     * @return A PuzzleState representing the solved state of our puzzle
     * from where we will begin scrambling.
     */
    public abstract PuzzleState getSolvedState();

    /**
     * @return The number of random moves we must apply to call a puzzle
     * sufficiently scrambled.
     */
    protected abstract int getRandomMoveCount();

    /**
     * This function will generate getRandomTurnCount() number of non cancelling,
     * random turns. If a puzzle wants to provide custom scrambles
     * (for example: Pochmann style megaminx or MRSS), it should override this method.
     *
     * NOTE: It is assumed that this method is thread safe! That means that if you're
     * overriding this method and you don't know what you're doing,
     * use the synchronized keyword when implementing this method:<br>
     * <code>protected synchronized String generateScramble(Random r);</code>
     * @param r An instance of Random
     * @return A PuzzleStateAndGenerator that contains a scramble string, and the
     *         state achieved by applying that scramble.
     */
    @NoExport
    public PuzzleStateAndGenerator generateRandomMoves(Random r) {
        AlgorithmBuilder ab = new AlgorithmBuilder(this, MergingMode.NO_MERGING);
        fillWithRandomMoves(ab, r, getRandomMoveCount());
        return ab.getStateAndGenerator();
    }

    protected static void fillWithRandomMoves(AlgorithmBuilder ab, Random r, int targetCost) {
        while(ab.getTotalCost() < targetCost) {
            String move = chooseRandomSuccessorMove(ab, r);
            try {
                ab.appendMove(move);
            } catch(InvalidMoveException e) {
                l.log(Level.SEVERE, "", e);
                throw new RuntimeException(e);
            }
        }
    }

    protected static String chooseRandomSuccessorMove(AlgorithmBuilder ab, Random r) {
        Map<String, ? extends PuzzleState> successors =
            ab.getState().getScrambleSuccessors();
        Set<String> moveSet = successors.keySet();

        String move;
        try {
            do {
                move = choose(r, moveSet);
                // If this move happens to be redundant, there is no
                // reason to select this move again in vain.
                successors.remove(move);
            } while(ab.isRedundant(move));
        } catch(InvalidMoveException e) {
            l.log(Level.SEVERE, "", e);
            throw new RuntimeException(e);
        }

        return move;
    }

    public static int[] cloneArr(int[] src) {
        int[] dest = new int[src.length];
        System.arraycopy(src, 0, dest, 0, src.length);
        return dest;
    }

    public static void deepCopy(int[][] src, int[][] dest) {
        for(int i = 0; i < src.length; i++) {
            System.arraycopy(src[i], 0, dest[i], 0, src[i].length);
        }
    }

    public static void deepCopy(int[][][] src, int[][][] dest) {
        for(int i = 0; i < src.length; i++) {
            deepCopy(src[i], dest[i]);
        }
    }

    public static <H> H choose(Random r, Iterable<H> keySet) {
        H chosen = null;
        int count = 0;
        for(H element : keySet) {
            if(r.nextInt(++count) == 0) {
                chosen = element;
            }
        }
        assert count > 0;
        return chosen;
    }

    public static int[] copyOfRange(int[] src, int from, int to) {
        int[] dest = new int[to - from];
        System.arraycopy(src, from, dest, 0, dest.length);
        return dest;
    }
}
