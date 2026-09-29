package levigibson.fto3phase;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import static levigibson.fto3phase.Util.*;

/**
 * <h2>FtoCubie</h2>
 * <h3>Purpose</h3>
 * <p>
 * FtoCubie is an array-based representation of the FTO. It is
 * a little bit slow and therefore not used for the search.
 *</p>
 * <h3>Turning / General Use</h3>
 * FtoCubie provides two public APIs for turning the cube.
 * FtoCubie::turn() mutates the internal state.
 * FtoCubie::turnInto() leaves the internal state untouched.
 * Instead, you pass your own FtoCubie as an output buffer.
 * <pre>{@code
 *     FtoCubie fto = new FtoCubie(); // Initialize to the solved state
 *     fto.turn(R); // Mutate the internal state
 *     fto.turn(RP); // Undo
 *     assert(fto.equals(new FtoCubie())); // Assert the FTO is solved
 *
 *     FtoCubie turnedFto = new FtoCubie(); // Allocate a new FTO
 *     fto.turnInto(move, turnedFto); // Turn fto, copy the state into turnedFto
 *     assert(fto.equals(new FtoCubie())); // the state of fto is untouched by turnInto()
 * }</pre>
 *
 * <h3>Index Packing / Unpacking</h3>
 * <p>
 * The search uses a more optimized state representation
 * where each piece type is stored in an integer, and the
 * moves are performed with lookup tables. These integers
 * will always have a bijection with possible states relevant
 * to the specific phase. The main purpose of this class
 * is to provide easy conversion from these packed integers
 * to state and back again. See FtoCoord for further details.
 *</p>
 * <h4>Phase 1 (G1)</h4>
 * <p>
 * Phase 1 solves the yellow triangles (a.k.a centers) and
 * the yellow edges. The edges do not need to be completely
 * solved, they just need to be a D / D' away from solved.
 * Solving these pieces reduce the moveset required to solve
 * the puzzle, similar to Edge Orientation on 3x3.
 *</p>
 * {R L B D U F BR BL} to {R L B D U}
 *<p>
 * The locations of the yellow triangles are restricted to
 * a single orbit (12 spots). The locations of these are
 * stored in a single integer between 0 and C(12,3)-1.
 *</p>
 * <p>
 * The locations of the yellow edges are also stored
 * with a integer between 0 and C(12,3)-1, but with
 * one extra bit for the parity of the three edges.
 *</p>
 * {@link  FtoCubie#g1PackEdges()  g1PackEdges}
 * {@link  FtoCubie#g1SetEdges(int)  g1SetEdges}
 * {@link  FtoCubie#g1PackTriangles()  g1PackTriangles}
 * {@link  FtoCubie#g1SetTriangles(int)  g1SetTriangles}
 *
 * <h4>Phase 2 (G2)</h4>
 * <p>
 * Phase 2 is the biggest search of the lot. It further
 * reduces the moveset needed to solve the Fto. This step
 * is also known as Octaminx Reduction. In order to solve
 * this, you need to solve three different sets of pieces.
 * Each piece type has a separate pruning table.
 * </p>
 * {R L B D U} to {R L B D}
 * <h4>G2 Edges</h4>
 * <p>
 * In g2, all the red, purple, and blue edges are
 * on their respective faces. They have some freedom of
 * movement. Much like G1, you can do R, L, B moves without
 * breaking G2. The same way you can cycle the yellow edges
 * in G1 with D moves without breaking G1.
 * </p>
 * <p>
 * Another way to think of this is that the sequences "U"
 * and "R U" when applied to a solved cube should produce
 * the same index. They both are solvable to G2 with "U",
 * even though the permutation of the edges is different.
 * </p>
 * <p>
 * This is accomplished the same way as g1. The locations
 * of the edges are stored, with an extra bit for parity.
 * </p>
 * {@link  FtoCubie#g2PackEdges()  g2PackEdges}
 * {@link  FtoCubie#g2SetEdges(int)  g2SetEdges}
 *
 * <h4>G2 Triangles</h4>
 * <p>
 * Triangles are pretty simple in g2. The red, purple,
 * and blue triangles must all be solved. Note that this
 * section only refers to the triangles on the RLBD orbit.
 * The other orbit also has triangles that need to be solved.
 * </p>
 * <p>
 * The locations of the red and purple triangles are packed into
 * a single index. The locations of the blue triangles are implied.
 * This state space is C(9,3) * C(6,3).
 * </p>
 * {@link  FtoCubie#g2PackTriangles()  g2PackTris}
 * {@link  FtoCubie#g2SetTriangles(int)  g2SetTriangles}
 *
 * <h4>G2 Triples</h4>
 * <p>
 * In g2, all the triples must be solved on the UFBrBl orbit. The
 * state space for the triples is much too large to store in a single
 * pruning table, so we have to compromise here.
 * </p>
 * <p>
 * Instead of storing the entire state space in a single compact index,
 * we split it into four different compact indices. One for each color
 * on the orbit (White, Green, Gray, Orange). Each compact index stores
 * the position and orientation of 3 corners, and the location of 3 triangles.
 * </p>
 * <p>
 * Storing the individual colors has a lot of advantages. Firstly, you don't
 * need to generate a pruning table for each color. Instead, you can
 * just generate a single pruning table for an arbitrary color, and use it for
 * the rest, as all the colors behave identically. Secondly, this gets
 * around a lot of weirdness with the triples. The triples have multiple solved
 * states, and these multiple solved states are not trivial like with the other
 * phases. Each corner can pair with two triangles, and each of these two triangles
 * has a bunch of options for which one. Plus, the corner can end up anywhere on
 * the cube.
 * </p>
 * <p>
 * Packing the individual colors works because you KNOW that each triangle
 * of a specific color will have to be paired up with a corner of that same
 * color. There's no wishy-washy nonsense with choices of what triangle to
 * pair with.
 * </p>
 * {@link  FtoCubie#g2PackTriples(int)  g2PackTriples}
 * {@link  FtoCubie#g2SetTriples(int, int)  g2SetTriples}
 * {@link  FtoCubie#g2PackTripleTris(int)  g2PackTripleTris}
 * {@link  FtoCubie#g2SetTripleTris(int, int)  g2SetTripleTris}
 * {@link  FtoCubie#g2PackTripleCorners(int)  g2PackTripleCorners}
 * {@link  FtoCubie#g2SetTripleCorners(int, int)  g2SetTripleCorners}
 *
 * <h4>Phase 3 (G3)</h4>
 * <p>
 * Phase 3 is trivial. So trivial that a straight search with no pruning
 * runs decently fast on its own. Nonetheless, we do have an index packing
 * system and a pruning table for the sake of consistency. The corners and
 * edges are packed into two different indices.
 * </p>
 * {@link  FtoCubie#g3PackCorners()  g3PackCorners}
 * {@link  FtoCubie#g3SetCorners(int)  g3SetCorners}
 * {@link  FtoCubie#g3PackEdges()  g3PackEdges}
 * {@link  FtoCubie#g3SetEdges(int)  g3SetEdges}
 *
 */
public class FtoCubie {

    //-------------- State --------------//

    private final int[] cornerPerm;
    private final int[] cornerOri;
    private final int[] edges;
    private final int[] trianglesUFBrBl;
    private final int[] trianglesRLBD;

    //Used as helper array when mutating the internal state
    //Lazily initialized when turn() is called
    private FtoCubie temps = null; //= new FtoCubie()

    //-------------- Constructors --------------//

    /**
     * Main Constructor
     * Initializes to a solved FTO
     */
    public FtoCubie(){
        cornerPerm = new int[6];
        cornerOri = new int[6];
        edges = new int[12];
        trianglesUFBrBl = new int[12];
        trianglesRLBD = new int[12];

        for (int i = 0; i < 6; i++) {
            cornerPerm[i] = i;
            cornerOri[i] = 0;
        }

        for (int i = 0; i < 12; i++) {
            edges[i] = i;
            trianglesUFBrBl[i] = i / 3; // centers1 = 000 111 222 333
            trianglesRLBD[i] = i / 3;
        }
    }

    /**
     * Copy Constructor
     * @param other FtoCubie to copy
     */
    public FtoCubie(FtoCubie other) {
        this.cornerPerm = other.cornerPerm.clone();
        this.cornerOri = other.cornerOri.clone();
        this.edges = other.edges.clone();
        this.trianglesUFBrBl = other.trianglesUFBrBl.clone();
        this.trianglesRLBD = other.trianglesRLBD.clone();
    }

    //-------------- Turning Functions --------------//

    /**
     * Turn the FTO! Mutates the internal state.
     * @param move the move to apply
     */
    public void turn(int move){
        if (temps == null) temps = new FtoCubie();
        turnInto(move, temps);
        temps.copyInto(this);
    }

    /**
     * Turn the FTO! Writes result to out without mutating this.
     * @param move the move to apply
     * @param out output of the move
     */
    public void turnInto(int move, FtoCubie out){
        if (out == this) throw new IllegalArgumentException("out can not be this");

        MoveEffect cycles = moveEffects[move];

        for (int i = 0; i < 6; i++) {
            out.cornerPerm[i] = this.cornerPerm[cycles.cp[i]];
            out.cornerOri[i] = this.cornerOri[cycles.cp[i]] ^ cycles.co[i];
        }

        for (int i = 0; i < 12; i++) {
            out.edges[i] = this.edges[cycles.ep[i]];
            out.trianglesUFBrBl[i] = this.trianglesUFBrBl[cycles.tpU[i]];
            out.trianglesRLBD[i] = this.trianglesRLBD[cycles.tpR[i]];
        }
    }

    //-------------- Handy Public Stuff --------------//

    /**
     * Generates an FTO in a random state
     * This FTO can be passed directly to Search::solution for a random state scramble!
     * @param r PRNG to use
     * @return random state FTO
     */
    public static FtoCubie randomCube(Random r){
        FtoCubie fto = new FtoCubie();
        fto.setAllEdges(r.nextInt(FtoCoord.ALL_EDGES_SIZE)); // 12! / 2
        fto.setAllCornerOrientation(r.nextInt(FtoCoord.ALL_CORNER_ORIENTATION_SIZE)); // 2 ^ 5
        fto.setAllCornerPermutation(r.nextInt(FtoCoord.ALL_CORNER_PERMUTATION_SIZE)); // 6! / 2
        fto.setAllTriangles(r.nextInt(FtoCoord.ALL_TRIANGLE_SIZE), 0); // C(12,3) * C(9,3) * C(6,3)
        fto.setAllTriangles(r.nextInt(FtoCoord.ALL_TRIANGLE_SIZE), 1); // C(12,3) * C(9,3) * C(6,3)
        return fto;
    }

    /**
     * Copy the state from this FTO into another FTO
     * @param c other FTO
     */
    public void copyInto(FtoCubie c) {
        System.arraycopy(this.cornerPerm, 0, c.cornerPerm, 0, 6);
        System.arraycopy(this.cornerOri, 0, c.cornerOri, 0, 6);
        System.arraycopy(this.edges, 0, c.edges, 0, 12);
        System.arraycopy(this.trianglesUFBrBl, 0, c.trianglesUFBrBl, 0, 12);
        System.arraycopy(this.trianglesRLBD, 0, c.trianglesRLBD, 0, 12);
    }

    /**
     * Apply moves to current FTO and write the output to given output FTO
     * @param moves sequence of moves to apply
     * @param out output buffer
     */
    public void applyMovesInto(int[] moves, FtoCubie out){
        this.copyInto(out);
        for (int move : moves) {
            out.turn(move);
        }
    }

    //-------------- Lehmer Indexing Functions --------------//

    /**
     * Packs all edges into a compact index using the Lehmer Index Algorithm
     * This index has a bijection with possible edge states
     * Index Range:
     * [0, (12!/2) - 1]
     * [0, 239500799]
     * @return compact index
     */
    public int packAllEdges(){
        return packPerm(edges, true);
    }

    /**
     * Set all edges from a compact index generated by {@link  FtoCubie#packAllEdges() packAllEdges}
     * Index Range:
     * [0, (12!/2) - 1]
     * [0, 239500799]
     * @param idx compact index
     */
    public void setAllEdges(int idx){
        if (idx < 0 || idx >= FtoCoord.ALL_EDGES_SIZE) throw new IllegalArgumentException("Index " + idx + " out of range");
        unpackPerm(edges, idx, true);
    }

    /**
     * Packs the permutation of the corners into a compact index using the Lehmer Index Algorithm.
     * This does not store the orientation. That is done separately. See {@link  FtoCubie#packAllCornerOrientation() packAllCornerOrientation}
     * This index has a bijection with possible corner permutation states
     * Index Range:
     * [0, (6!/2) - 1]
     * [0, 359]
     * @return compact index
     */
    public int packAllCornerPermutation(){
        return packPerm(cornerPerm, true);
    }

    /**
     * Set the corner permutation from a compact index generated by {@link  FtoCubie#packAllCornerPermutation() packAllCornerPermutation}
     * Index Range:
     * [0, (6!/2) - 1]
     * [0, 359]
     * @param idx compact index
     */
    public void setAllCornerPermutation(int idx){
        if (idx < 0 || idx >= FtoCoord.ALL_CORNER_PERMUTATION_SIZE) throw new IllegalArgumentException("Index " + idx + " out of range");
        unpackPerm(cornerPerm, idx, true);
    }

    /**
     * Packs the orientation of the corners into a compact index.
     * This does not store the permutation. That is done separately. See {@link  FtoCubie#packAllCornerPermutation() packAllCornerPermutation}
     * This index has a bijection with possible corner orientation states
     * Index Range:
     * [0, (2 ^ 5) - 1]
     * [0, 31]
     * @return compact index
     */
    public int packAllCornerOrientation(){
        int index = 0;
        for (int i = 0; i < 5; i++) {
            index |= cornerOri[i] << i;
        }
        return index;
    }

    /**
     * Set the corner permutation from a compact index generated by {@link  FtoCubie#packAllCornerOrientation() packAllCornerOrientation}
     * Index Range:
     * [0, (6!/2) - 1]
     * [0, 359]
     * @param idx compact index
     */
    public void setAllCornerOrientation(int idx){
        if (idx < 0 || idx >= FtoCoord.ALL_CORNER_ORIENTATION_SIZE) throw new IllegalArgumentException("Index " + idx + " out of range");
        for (int i = 0; i < 5; i++) {
            cornerOri[i] = (idx >> i) & 1;
        }
        cornerOri[5] = Integer.bitCount(idx) % 2;
    }

    private static final int[] ALL_TRIANGLES_INDEX_COEFFICIENTS = {
        nCr(9, 3) * nCr(6, 3),
        nCr(6, 3),
        1
    };

    /**
     * Packs the permutation of the triangles of one orbit into a compact index.
     * This index has a bijection with possible triangle states (for one orbit)
     * Index Range:
     * [0, C(12,3) * C(9,3) * C(6,3) * C(3,3)]
     * [0, 369600]
     * @param orbit 0 = UFBrBl, 1 = RLBD
     * @return compact index
     */
    public int packAllTriangles(int orbit){
        if (orbit != 0 && orbit != 1)
            throw new IllegalArgumentException("Orbit must be 0 or 1");

        int[] triangles = orbit == 1 ? trianglesRLBD : trianglesUFBrBl;

        boolean[] used = new boolean[12];
        int[][] loc = new int[3][3];

        for (int xo = 0; xo < 3; xo++) {
            int found = 0;
            int passed = 0;
            for (int i = 0; i < 12; i++) {
                if (triangles[i] == xo){
                    loc[xo][found++] = passed;
                    used[i] = true;
                    passed++;
                } else if (!used[i]){
                    passed++;
                }
            }

            if (found != 3)
                throw new IllegalStateException("Expected found=3. Instead, found=" + found);
        }

        return (packSubset(loc[0]) * ALL_TRIANGLES_INDEX_COEFFICIENTS[0]) +
            packSubset(loc[1]) * ALL_TRIANGLES_INDEX_COEFFICIENTS[1] +
            packSubset(loc[2]) * ALL_TRIANGLES_INDEX_COEFFICIENTS[2];
    }

    /**
     * Set the triangles on a single orbit from a compact index generated by {@link  FtoCubie#packAllTriangles(int orbit) packAllTriangles}
     * Index Range:
     * [0, C(12,3) * C(9,3) * C(6,3) * C(3,3)]
     * [0, 369600]
     * @param idx compact index
     * @param orbit 0 = UFBrBl, 1 = RLBD
     */

    public void setAllTriangles(int idx, int orbit){
        if (idx < 0 || idx >= FtoCoord.ALL_TRIANGLE_SIZE) throw new IllegalArgumentException("Index " + idx + " out of range");

        if (orbit != 0 && orbit != 1)
            throw new IllegalArgumentException("Orbit must be 0 or 1");

        int[] triangles = orbit == 1 ? trianglesRLBD : trianglesUFBrBl;

        int[][] loc = new int[3][3];

        int remaining = idx;
        for (int color = 0; color < 3; color++) {
            int coefficient = ALL_TRIANGLES_INDEX_COEFFICIENTS[color];
            int digit = remaining / coefficient;

            unpackSubset(loc[color], digit);

            remaining -= coefficient * digit;
        }

        Arrays.fill(triangles, 3);

        for (int color = 0; color < 3; color++) {
            int nz = 0;
            int li = 0;
            for (int i = 0; i < 12; i++) {
                if (triangles[i] < color) continue;
                if (li < 3 && nz == loc[color][li]) {
                    triangles[i] = color;
                    li++;
                }
                nz++;
            }
        }
    }

    /**
     * Packs the location and parity of the g1 edges into a compact index.
     * Index Range:
     * [0, C(12,3) * 2]
     * [0, 440]
     * @return compact index
     */
    public int g1PackEdges() {
        int[] loc = new int[3];
        int[] perm = new int[3];
        int count = 0;

        for (int i = 0; i < 12; i++) {
            if (edges[i] >= EDF) {
                loc[count] = i;
                perm[count] = edges[i] - EDF;
                count++;
            }
        }

        if (count != 3) {
            throw new IllegalStateException("Expected 3 D-face edges. This is not a possible FTO state.");
        }

        int parity = isParity(perm) ? 1 : 0;

        return packSubset(loc) * 2 + parity;
    }

    /**
     * Set the g1 edges based on a compact index generated by {@link  FtoCubie#g1PackEdges() g1PackEdges}
     * Index Range:
     * [0, C(12,3) * 2]
     * [0, 440]
     * @param idx compact index
     */
    public void g1SetEdges(int idx){
        if (idx < 0 || idx >= FtoCoord.G1_EDGES_SIZE) throw new IllegalArgumentException("Index " + idx + " out of range");

        int locIdx = idx / 2;
        int parity = idx % 2;

        int[] loc = new int[3];
        int[] perm = {EDF, EDBR, EDBL};
        if (parity == 1) swap(perm, 1, 2);

        //Unpack loc
        unpackSubset(loc, locIdx);

        //set the edges
        Arrays.fill(edges, -1);
        for (int i = 0; i < 3; i++) {
            edges[loc[i]] = perm[i];
        }
    }

    /**
     * Packs the location of the 3 D-face (TOD) triangles in the RLBD orbit into a compact index.
     * In G1, the D-face triangles are fixed to their solved positions on the D face,
     * so we only need to track their locations among the 12 RLBD triangles.
     * Index Range:
     * [0, C(12,3) - 1]
     * [0, 219]
     * @return compact index
     */
    public int g1PackTriangles(){
        int[] idx = new int[3];
        int count = 0;

        for (int i = 0; i < 12; i++) {
            if (trianglesRLBD[i] == TOD) idx[count++] = i;
        }

        if (count != 3) throw new IllegalStateException("Less than 3 d-layer triangles");

        return packSubset(idx);
    }

    /**
     * Set the RLBD triangles from a compact index generated by {@link  FtoCubie#g1PackTriangles() g1PackTriangles}
     * Sets the 3 D-face triangles to TOD at the locations encoded by the index,
     * and clears all other positions to -1.
     * Index Range:
     * [0, C(12,3) - 1]
     * [0, 219]
     * @param idx compact index
     */
    public void g1SetTriangles(int idx){
        //C(12,3)
        if (idx < 0 || idx >= FtoCoord.G1_TRIANGLES_SIZE) throw new IllegalArgumentException("Index " + idx + " out of range");

        //Unpack index
        int[] loc = new int[3];
        unpackSubset(loc, idx);

        //Set the relevant centers
        for (int i = 0; i < 12; i++) {
            trianglesRLBD[i] = -1;
        }
        for (int i = 0; i < 3; i++) {
            trianglesRLBD[loc[i]] = TOD;
        }
    }

    /**
     * Pack the relevant information about the edges for g2 in a single compact index.
     * This index is computed with:
     * <ul>
     *     <li>The locations of the red edges</li>
     *     <li>The locations of the purple edges</li>
     *     <li>The parity of the red edges</li>
     *     <li>The parity of the purple edges</li>
     * </ul>
     *
     * The blue edges are also relevant to g2, but packing the red and purple edges imply the
     * locations of the blue edges, so we don't consider the blue edges.
     * Index Range:
     * [0, C(9,3) * C(6,3) * 2^2]
     * [0, 6719]
     * Why so complicated? G2 has this weird thing where each face of edges (R, L, B) can be in
     * one of three states, and it's still in G2. Take a solved FTO, perform an R move, and it's
     * still in G2. But if you took the raw permutation of the edges it would produce two different
     * indices. This wouldn't be a truly compact index. Instead, we just store the locations of the
     * relevant edges, as well as a single bit for parity. Boom! Compact Index!
     *
     * @return compact index
     */
    public int g2PackEdges(){
        for (int i = 0; i < 9; i++) {
            if (edges[i] > 8) throw new IllegalStateException("Edges not in phase 1");
        }

        boolean[] used = new boolean[9];
        int[][] loc = new int[2][3];
        int[][] perm = new int[2][3];
        int[] parity = new int[2];

        for (int xo = 0; xo < 2; xo++) {
            int found = 0;
            int passed = 0;
            for (int i = 0; i < 9; i++) {
                if (G2_EDGE_COLORS[edges[i]] == xo){
                    perm[xo][found] = G2_EDGE_NORM[edges[i]];
                    loc[xo][found++] = passed;
                    used[i] = true;
                    passed++;
                } else if (!used[i]){
                    passed++;
                }
            }

            if (found != 3)
                throw new IllegalStateException("Expected found=3. Instead, found=" + found);
        }

        for (int i = 0; i < 2; i++) {
            parity[i] = isParity(perm[i]) ? 1 : 0;
        }

        int subsetIndex = packSubset(loc[1]) +
            packSubset(loc[0]) * nCr(6, 3);

        int parityIndex = parity[1] * 2 + parity[0];

        return (subsetIndex * 4) + parityIndex;
    }


    /**
     * Set the g1 edges based on a compact index generated by {@link  FtoCubie#g2PackEdges() g2PackEdges}
     * Index Range:
     * [0, C(9,3) * C(6,3) * 2^2]
     * [0, 6719]
     * @param idx compact index
     */
    public void g2SetEdges(int idx){
        if (idx < 0 || idx >= FtoCoord.G2_EDGES_SIZE) throw new IllegalArgumentException("Index " + idx + " out of range");

        int subsetIndex = idx/4;
        int parityIndex = idx % 4;

        int[][] loc = new int[2][3];
        int[][] perm = new int[2][3];
        int[] parity = new int[2];

        parity[0] = parityIndex & 1;
        parity[1] = (parityIndex >> 1) & 1;

        unpackSubset(loc[0], subsetIndex / nCr(6, 3));
        unpackSubset(loc[1], subsetIndex % nCr(6, 3));

        for (int i = 0; i < 2; i++) {
            System.arraycopy(G2_EDGE_NORM_INV[i], 0, perm[i], 0, 3);
            if (parity[i] == 1){
                swap(perm[i], 1, 2);
            }
        }

        //Set edges to blank slate with G1 solved
        Arrays.fill(edges, -1);
        edges[EDF] = EDF;
        edges[EDBL] = EDBL;
        edges[EDBR] = EDBR;

        for (int i = 0; i < 3; i++) {
            edges[loc[0][i]] = perm[0][i];
        }

        int nz = 0;
        int li = 0;
        for (int i = 0; i < 9; i++) {
            if (edges[i] != -1) continue;
            if (li < 3 && nz == loc[1][li]) {
                edges[i] = perm[1][li];
                li++;
            }
            nz++;
        }

        int[] bloc = new int[3];
        li = 0;
        for (int i = 0; i < 9; i++) {
            if (edges[i] == -1) {
                bloc[li] = i;
                edges[i] = G2_EDGE_NORM_INV[2][li++];
            }
        }

        if (isParity(edges)){
            swap(edges, bloc[0], bloc[1]);
        }
    }

    /**
     * Packs the locations of the red and purple triangles into a compact index.
     * The last 3 positions (indices 9-11) must already be in phase 1 (set to TOD), so only
     * the first 9 positions are packed into a combined subset index.
     * Index Range:
     * [0, C(9,3) * C(6,3) - 1]
     * [0, 1679]
     * @return compact index
     */
    public int g2PackTriangles(){
        for (int i = 9; i < 12; i++) {
            if (trianglesRLBD[i] != TOD)
                throw new IllegalStateException("Tris must be in phase 1");
        }

        boolean[] used = new boolean[9];
        int[][] loc = new int[2][3];

        for (int xo = 0; xo < 2; xo++) {
            int found = 0;
            int passed = 0;
            for (int i = 0; i < 9; i++) {
                if (trianglesRLBD[i] == xo){
                    loc[xo][found++] = passed;
                    used[i] = true;
                    passed++;
                } else if (!used[i]){
                    passed++;
                }
            }

            if (found != 3)
                throw new IllegalStateException("Expected found=3. Instead, found=" + found);
        }
        return packSubset(loc[1]) +
            packSubset(loc[0]) * nCr(6, 3);

    }

    /**
     * Set the red, purple, and blue triangles from a compact index generated by {@link  FtoCubie#g2PackTriangles() g2PackTris}
     * and sets the yellow (d-face) triangles to solved.
     * Index Range:
     * [0, C(9,3) * C(6,3) - 1]
     * [0, 1679]
     * @param idx compact index
     */
    public void g2SetTriangles(int idx){
        if (idx < 0 || idx >= FtoCoord.G2_TRIANGLES_SIZE) throw new IllegalArgumentException("Index " + idx + " out of range");

        int[] loc0 = new int[3];
        int[] loc1 = new int[3];
        unpackSubset(loc0, idx / nCr(6, 3));
        unpackSubset(loc1, idx % nCr(6, 3));

        Arrays.fill(trianglesRLBD, 0, 9, 2);
        for (int v : loc0) trianglesRLBD[v] = 0;

        int nz = 0;
        int li = 0;
        for (int i = 0; i < 9; i++) {
            if (trianglesRLBD[i] == 0) continue;
            if (li < 3 && nz == loc1[li]) {
                trianglesRLBD[i] = 1;
                li++;
            }
            nz++;
        }
        for (int i = 9; i < 12; i++) trianglesRLBD[i] = TOD;
    }

    private static final int G2_TRIPLE_CORNER_SIZE = nCr(6, 3) * pow(2,3);

    /**
     * Packs the state of the triples (corners + triangles) of a single color into a compact index.
     * Index Range:
     * [0, C(12,3) * C(6,3) * 2^3 - 1]
     * [0, 35199]
     * @param color the color to pack (0 = U, 1 = F, 2 = BR, 3 = BL)
     * @return compact index
     */
    public int g2PackTriples(int color){
        if (color > 3 || color < 0){
            throw new IllegalArgumentException("color must be U, F, BR, or BL");
        }

        return (G2_TRIPLE_CORNER_SIZE * g2PackTripleTris(color)) + g2PackTripleCorners(color);
    }

    /**
     * Set the triples (corners + triangles) of a single color from a compact index generated by
     * {@link  FtoCubie#g2PackTriples(int) g2PackTriples}
     * Index Range:
     * [0, C(12,3) * C(6,3) * 2^3 - 1]
     * [0, 35199]
     * @param idx compact index
     * @param color the color to set (0 = U, 1 = F, 2 = BR, 3 = BL)
     */
    public void g2SetTriples(int idx, int color){
        if (idx < 0 || idx >= FtoCoord.G2_TRIPLE_SIZE) throw new IllegalArgumentException("Index " + idx + " out of range");

        int tris = idx / G2_TRIPLE_CORNER_SIZE;
        int corners = idx % G2_TRIPLE_CORNER_SIZE;

        g2SetTripleTris(tris, color);
        g2SetTripleCorners(corners, color);
    }


    //[triple color][corner]
    //0/1 = which side the color is on
    //-1 = this corner does not have this color on it
    private static final int[][] CORNER_TRIPLE_COLOR_LOOKUP = {
        {0,0,0,-1,-1,-1},
        {1,-1,-1,0,1,-1},
        {-1,1,-1,-1,0,1},
        {-1,-1,1,1,-1,0}};

    /**
     * Packs the location and orientation of the 3 corners matching a given color into a compact index.
     * The corners matching the color are determined by {@link #CORNER_TRIPLE_COLOR_LOOKUP}.
     * Encodes the 3 positions (subset of 6) and 3 orientation bits.
     * Index Range:
     * [0, C(6,3) * 2^3 - 1]
     * [0, 159]
     * @param color the color to pack (0 = U, 1 = F, 2 = BR, 3 = BL)
     * @return compact index
     */
    private int g2PackTripleCorners(int color){
        int[] idx = new int[3];
        int orientation = 0;

        int found = 0;
        for (int i = 0; i < 6; i++) {
            int corner = cornerPerm[i];
            int ori = cornerOri[i];

            if (corner == -1) continue;

            int parity = CORNER_TRIPLE_COLOR_LOOKUP[color][corner];

            if (parity != -1){
                orientation |= (parity ^ ori) << found;
                idx[found++] = i;
            }
        }

        if (found != 3){
            throw new IllegalStateException("Expected 3 matching corners");
        }

        assert (orientation < 8);

        return packSubset(idx) * 8 + orientation;
    }

    /**
     * Packs the locations of the 3 triangles of a given color in the UFBrBl orbit into a subset index.
     * Index Range:
     * [0, C(12,3) - 1]
     * [0, 219]
     * @param color the color to pack (0 = U, 1 = F, 2 = BR, 3 = BL)
     * @return compact index
     */
    private int g2PackTripleTris(int color){
        int[] idx = new int[3];

        int found = 0;
        for (int i = 0; i < 12; i++) {
            if (trianglesUFBrBl[i] == color){
                idx[found++] = i;
            }
        }

        return packSubset(idx);
    }

    private static final int G2_TRIPLE_TRIANGLES_SIZE = Util.nCr(12,3);
    private static final int G2_TRIPLE_CORNERS_SIZE = Util.nCr(6,3) * pow(2,3);

    /**
     * Set the UFBrBl triangles of a given color from a subset index generated by
     * {@link  FtoCubie#g2PackTripleTris(int) g2PackTripleTris}
     * Clears all other triangle positions in the UFBrBl orbit to -1.
     * Index Range:
     * [0, C(12,3) - 1]
     * [0, 219]
     * @param idx compact index
     * @param color the color to set (0 = U, 1 = F, 2 = BR, 3 = BL)
     */
    private void g2SetTripleTris(int idx, int color){
        if (idx < 0 || idx >= G2_TRIPLE_TRIANGLES_SIZE) throw new IllegalArgumentException("Index " + idx + " out of range");

        int[] loc = new int[3];
        unpackSubset(loc, idx);
        Arrays.fill(trianglesUFBrBl, -1);

        for (int i = 0; i < 3; i++) {
            trianglesUFBrBl[loc[i]] = color;
        }
    }

    /**
     * Set the corners matching a given color from a compact index generated by
     * {@link  FtoCubie#g2PackTripleCorners(int) g2PackTripleCorners}
     * The index encodes the 3 corner positions (subset of 6) and 3 orientation bits.
     * The relevant corners are determined by {@link #CORNER_TRIPLE_COLOR_LOOKUP}.
     * Index Range:
     * [0, C(6,3) * 2^3 - 1]
     * [0, 159]
     * @param idx compact index
     * @param color the color to set (0 = U, 1 = F, 2 = BR, 3 = BL)
     */
    private void g2SetTripleCorners(int idx, int color){
        if (idx < 0 || idx >= G2_TRIPLE_CORNERS_SIZE) throw new IllegalArgumentException("Index " + idx + " out of range");

        int[] loc = new int[3];
        unpackSubset(loc, idx / 8);

        int orientation = idx % 8;

        int[] relevantCorners = new int[3];
        int found = 0;
        for (int i = 0; i < 6; i++) {
            if (CORNER_TRIPLE_COLOR_LOOKUP[color][i] != -1){
                relevantCorners[found++] = i;
            }
        }

        Arrays.fill(cornerPerm, -1);
        Arrays.fill(cornerOri, -1);

        for (int i = 0; i < 3; i++) {
            int perm = loc[i];
            int ori = (orientation >> i) & 1;

            cornerPerm[perm] = relevantCorners[i];
            cornerOri[perm] = ori ^ CORNER_TRIPLE_COLOR_LOOKUP[color][relevantCorners[i]];
        }

    }

    /**
     * Packs the full corner state (permutation + orientation) into a single compact index.
     * This is used for the g3 search, as the entire state of the corner orientation + permutation
     * is relevant to the g3 search.
     * Index Range:
     * [0, (6!/2) * 2^5 - 1]
     * [0, 11519]
     * @return compact index
     */
    public int g3PackCorners(){
        return (packAllCornerPermutation() * FtoCoord.ALL_CORNER_ORIENTATION_SIZE) + packAllCornerOrientation();
    }

    /**
     * Set the full corner state from a compact index generated by {@link  FtoCubie#g3PackCorners() g3PackCorners}
     * Index Range:
     * [0, (6!/2) * 2^5 - 1]
     * [0, 11519]
     * @param idx compact index
     */
    public void g3SetCorners(int idx){
        if (idx < 0 || idx >= FtoCoord.ALL_CORNER_ORIENTATION_SIZE * FtoCoord.ALL_CORNER_PERMUTATION_SIZE)
            throw new IllegalArgumentException("Index " + idx + " out of range");

        setAllCornerPermutation(idx/FtoCoord.ALL_CORNER_ORIENTATION_SIZE);
        setAllCornerOrientation(idx%FtoCoord.ALL_CORNER_ORIENTATION_SIZE);
    }

    /**
     * Packs the state of the edges in phase 3 into a compact index.
     * Each of the 4 edge axes ({@link #G3_EDGES}) can be in one of 3 configurations,
     * corresponding to which edge from that axis is in the reference position.
     * Index Range:
     * [0, 3^4 - 1]
     * [0, 80]
     * @return compact index
     */
    public int g3PackEdges(){
        int[] loc = new int[4];
        Arrays.fill(loc, -1);

        for (int axis = 0; axis < 4; axis++) {
            for (int i = 0; i < 3; i++) {
                if (G3_EDGES[axis][i] == edges[G3_EDGES[axis][0]]){
                    loc[axis] = i;
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            if (loc[i] == -1)
                throw new IllegalStateException("Cound not find G3 edges");
        }

        return (27 * loc[0]) + (9 * loc[1]) + (3 * loc[2]) + (loc[3]);
    }

    /**
     * Set the edges from a compact index generated by {@link  FtoCubie#g3PackEdges() g3PackEdges}
     * Index Range:
     * [0, 3^4 - 1]
     * [0, 80]
     * @param idx compact index
     */
    public void g3SetEdges(int idx){
        if (idx < 0 || idx >= FtoCoord.G3_EDGE_SIZE) throw new IllegalArgumentException("Index " + idx + " out of range");
        for (int i = 0; i < 12; i++) {
            edges[i] = i;
        }

        int remaining = idx;
        for (int axis = 0; axis < 4; axis++) {
            int coefficient = pow(3, 3-axis);
            int digit = remaining / coefficient;

            for (int iteration = 0; iteration < 3; iteration++) {
                if (edges[G3_EDGES[axis][0]] == G3_EDGES[axis][digit])
                    break;
                for (int i = 0; i < 2; i++) {
                    swap(edges, G3_EDGES[axis][0], G3_EDGES[axis][i + 1]);
                }
            }

            remaining -= coefficient * digit;
        }

    }

    /**
     * equals compares the internal state and returns true
     * if all the pieces are in the same places
     * @param obj   the reference object with which to compare.
     * @return true / false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        FtoCubie fto = (FtoCubie) obj;

        return Arrays.equals(fto.edges, this.edges) &&
            Arrays.equals(fto.trianglesUFBrBl, this.trianglesUFBrBl) &&
            Arrays.equals(fto.trianglesRLBD, this.trianglesRLBD) &&
            Arrays.equals(fto.cornerOri, this.cornerOri) &&
            Arrays.equals(fto.cornerPerm, this.cornerPerm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(this.edges),
            Arrays.hashCode(this.trianglesUFBrBl),
            Arrays.hashCode(this.trianglesRLBD),
            Arrays.hashCode(this.cornerOri),
            Arrays.hashCode(this.cornerPerm));
    }

    //Getter functions used by tests
    public int[] getCornerPerm(){ return cornerPerm.clone(); }
    public int[] getCornerOri(){ return cornerOri.clone(); }
    public int[] getEdges(){ return edges.clone(); }
    public int[] getTrianglesUFBrBl(){ return trianglesUFBrBl.clone(); }
    public int[] getTrianglesRLBD(){ return trianglesRLBD.clone(); }

    //-------------- Move & Cubie Definitions --------------//

    //Moves
    public static final int
        R = 0, RP = 1, L = 2, LP = 3,
        B = 4, BP = 5, U = 6, UP = 7,
        D = 8, DP = 9, F = 10, FP = 11,
        BR = 12, BRP = 13, BL = 14, BLP = 15;

    //Triangle Ordinals
    //Represents the colors of the triangles
    private static final int
        TOU = 0, TOF = 1, TOBR = 2, TOBL = 3,
        TOR = 0, TOL = 1, TOB = 2, TOD = 3;

    //Triangle Indices
    //Represents the index of a specific triangle
    private static final int
        TIUBL = 0, TIUBR = 1, TIUF = 2, TIFU = 3,
        TIFBR = 4, TIFBL = 5, TIBRU = 6, TIBRBL = 7,
        TIBRF = 8, TIBLU = 9, TIBLF = 10, TIBLBR = 11,
        TIRL = 0,  TIRB = 1,  TIRD = 2,  TILB = 3,
        TILR = 4,  TILD = 5,  TIBR = 6,  TIBL = 7,
        TIBD = 8,  TIDR = 9,  TIDL = 10, TIDB = 11;

    //Edges
    private static final int
        EUB = 0, EUR = 1, EUL = 2, EFL = 3,
        EFR = 4, ERBR = 5, EBRB = 6, EBLB = 7,
        ELBL = 8, EDF = 9, EDBR = 10, EDBL = 11;

    //Corners
    private static final int
        CUF = 0, CUBR = 1, CUBL = 2,
        CDL = 3, CDR = 4, CDB = 5;

    private static final int[] G2_EDGE_COLORS = {TOB, TOR, TOL, TOL, TOR, TOR, TOB, TOB, TOL};
    private static final int[] G2_EDGE_NORM = {0, 0, 0, 1, 1, 2, 1, 2, 2};
    private static final int[][] G2_EDGE_NORM_INV = {
        {EUR, EFR, ERBR},
        {EUL, EFL, ELBL},
        {EUB, EBRB, EBLB},
    };

    private static class MoveEffect{
        public int[] co;
        public int[] cp;
        public int[] tpU;
        public int[] tpR;
        public int[] ep;
    }

    //EUB = 0, EUR = 1, EUL
    private static final int[][] G3_EDGES = {
        {EUB, EBRB, EBLB},
        {EUR, EFR, ERBR},
        {EUL, ELBL, EFL},
        {EDF, EDBR, EDBL}
    };

    private static final MoveEffect[] moveEffects;

    //Hard-coded data about what cycles the different moves make
    static {
        moveEffects = new MoveEffect[16];
        for (int i = 0; i < 16; i++) { moveEffects[i] = new MoveEffect(); }

        //R
        moveEffects[R].cp = new int[]{CDR, CUF, CUBL, CDL, CUBR, CDB};
        moveEffects[R].co = new int[]{1, 1, 0, 0, 0, 0};
        moveEffects[R].ep = new int[]{EUB, EFR, EUL, EFL, ERBR, EUR, EBRB, EBLB, ELBL, EDF, EDBR, EDBL};
        moveEffects[R].tpU = new int[]{TIUBL, TIFU, TIFBR, TIBRF, TIBRU, TIFBL, TIUF, TIBRBL, TIUBR, TIBLU, TIBLF, TIBLBR};
        moveEffects[R].tpR = new int[]{TIRD, TIRL, TIRB, TILB, TILR, TILD, TIBR, TIBL, TIBD, TIDR, TIDL, TIDB};
        //L
        moveEffects[L].cp = new int[]{CUBL,  CUBR, CDL, CUF, CDR, CDB};
        moveEffects[L].co = new int[]{1, 0, 1, 0, 0, 0};
        moveEffects[L].ep = new int[]{EUB, EUR, ELBL, EUL, EFR, ERBR, EBRB, EBLB, EFL, EDF, EDBR, EDBL};
        moveEffects[L].tpU = new int[]{TIBLF, TIUBR, TIBLU, TIUBL, TIFBR, TIUF, TIBRU, TIBRBL, TIBRF, TIFBL, TIFU, TIBLBR};
        moveEffects[L].tpR = new int[]{TIRL, TIRB, TIRD, TILD, TILB, TILR, TIBR, TIBL, TIBD, TIDR, TIDL, TIDB};
        //B
        moveEffects[B].cp = new int[]{CUF, CDB, CUBR, CDL, CDR, CUBL};
        moveEffects[B].co = new int[]{0, 1, 1, 0, 0, 0};
        moveEffects[B].ep = new int[]{EBRB, EUR, EUL, EFL, EFR, ERBR, EBLB, EUB, ELBL, EDF, EDBR, EDBL};
        moveEffects[B].tpU = new int[]{TIBRU, TIBRBL, TIUF, TIFU, TIFBR, TIFBL, TIBLBR, TIBLU, TIBRF, TIUBR, TIBLF, TIUBL};
        moveEffects[B].tpR = new int[]{TIRL, TIRB, TIRD, TILB, TILR, TILD, TIBD, TIBR, TIBL, TIDR, TIDL, TIDB};
        //D
        moveEffects[D].cp = new int[]{CUF, CUBR, CUBL, CDB, CDL, CDR};
        moveEffects[D].co = new int[]{0, 0, 0, 0, 0, 0};
        moveEffects[D].ep = new int[]{EUB, EUR, EUL, EFL, EFR, ERBR, EBRB, EBLB, ELBL, EDBL, EDF, EDBR};
        moveEffects[D].tpU = new int[]{TIUBL, TIUBR, TIUF, TIFU, TIBLF, TIBLBR, TIBRU, TIFBR, TIFBL, TIBLU, TIBRBL, TIBRF};
        moveEffects[D].tpR = new int[]{TIRL, TIRB, TIRD, TILB, TILR, TILD, TIBR, TIBL, TIBD, TIDL, TIDB, TIDR};
        //U
        moveEffects[U].cp = new int[]{CUBR, CUBL, CUF, CDL, CDR, CDB};
        moveEffects[U].co = new int[]{0, 0, 0, 0, 0, 0};
        moveEffects[U].ep = new int[]{EUL, EUB, EUR, EFL, EFR, ERBR, EBRB, EBLB, ELBL, EDF, EDBR, EDBL};
        moveEffects[U].tpU = new int[]{TIUF, TIUBL, TIUBR, TIFU, TIFBR, TIFBL, TIBRU, TIBRBL, TIBRF, TIBLU, TIBLF, TIBLBR};
        moveEffects[U].tpR = new int[]{TIBR, TIBL, TIRD, TIRL, TIRB, TILD, TILB, TILR, TIBD, TIDR, TIDL, TIDB};
        //F
        moveEffects[F].cp = new int[]{CDL, CUBR, CUBL, CDR, CUF, CDB};
        moveEffects[F].co = new int[]{1, 0, 0, 1, 0, 0};
        moveEffects[F].ep = new int[]{EUB, EUR, EUL, EDF, EFL, ERBR, EBRB, EBLB, ELBL, EFR, EDBR, EDBL};
        moveEffects[F].tpU = new int[]{TIUBL, TIUBR, TIUF, TIFBL, TIFU, TIFBR, TIBRU, TIBRBL, TIBRF, TIBLU, TIBLF, TIBLBR};
        moveEffects[F].tpR = new int[]{TILD, TIRB, TILR, TILB, TIDL, TIDR, TIBR, TIBL, TIBD, TIRL, TIRD, TIDB};
        //BR
        moveEffects[BR].cp = new int[]{CUF, CDR, CUBL, CDL, CDB, CUBR};
        moveEffects[BR].co = new int[]{0, 1, 0, 0, 1, 0};
        moveEffects[BR].ep = new int[]{EUB, EUR, EUL, EFL, EFR, EDBR, ERBR, EBLB, ELBL, EDF, EBRB, EDBL};
        moveEffects[BR].tpU = new int[]{TIUBL, TIUBR, TIUF, TIFU, TIFBR, TIFBL, TIBRF, TIBRU, TIBRBL, TIBLU, TIBLF, TIBLBR};
        moveEffects[BR].tpR = new int[]{TIRL, TIDR, TIDB, TILB, TILR, TILD, TIRD, TIBL, TIRB, TIBD, TIDL, TIBR};
        //BL
        moveEffects[BL].cp = new int[]{CUF, CUBR, CDB, CUBL, CDR, CDL};
        moveEffects[BL].co = new int[]{0, 0, 1, 0, 0, 1};
        moveEffects[BL].ep = new int[]{EUB, EUR, EUL, EFL, EFR, ERBR, EBRB, EDBL, EBLB, EDF, EDBR, ELBL};
        moveEffects[BL].tpU = new int[]{TIUBL, TIUBR, TIUF, TIFU, TIFBR, TIFBL, TIBRU, TIBRBL, TIBRF, TIBLBR, TIBLU, TIBLF};
        moveEffects[BL].tpR = new int[]{TIRL, TIRB, TIRD, TIBD, TILR, TIBL, TIBR, TIDB, TIDL, TIDR, TILB, TILD};

        //No need to hard code the inverse moves
        //You can just invert the cycles
        for (int move = 1; move < 16; move += 2) {
            MoveEffect inverse = moveEffects[move];
            MoveEffect normal = moveEffects[move-1];

            inverse.co = new int[6];
            inverse.cp = new int[6];
            inverse.ep = new int[12];
            inverse.tpU = new int[12];
            inverse.tpR = new int[12];

            for (int i = 0; i < 6; i++) {
                inverse.cp[normal.cp[i]] = i;
            }
            for (int i = 0; i < 6; i++) {
                inverse.co[i] = normal.co[inverse.cp[i]];
            }
            for (int i = 0; i < 12; i++) {
                inverse.ep[normal.ep[i]] = i;
                inverse.tpU[normal.tpU[i]] = i;
                inverse.tpR[normal.tpR[i]] = i;
            }
        }
    }
}
