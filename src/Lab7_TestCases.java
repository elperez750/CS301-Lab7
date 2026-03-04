import java.lang.reflect.Method;
import java.util.Arrays;

public class Lab7_TestCases {

    // -------------------------------
    // Reflection helpers (call private static methods in Lab7)
    // -------------------------------
    private static Method getLab7Method(String name, Class<?>... params) {
        try {
            Method m = Lab7.class.getDeclaredMethod(name, params);
            m.setAccessible(true);
            return m;
        } catch (Exception e) {
            throw new RuntimeException("Cannot find Lab7." + name + " with params "
                    + Arrays.toString(params) + ". Did you rename it?", e);
        }
    }

    private static void callProblem1(Graph g) {
        try {
            Method p1 = getLab7Method("problem1", Graph.class);
            p1.invoke(null, g);
        } catch (Exception e) {
            throw new RuntimeException("Failed invoking Lab7.problem1(Graph).", e);
        }
    }

    private static int[] callProblem2(Graph g, int startId) {
        try {
            Method p2 = getLab7Method("problem2", Graph.class, int.class);
            Object res = p2.invoke(null, g, startId);
            return (int[]) res;
        } catch (Exception e) {
            throw new RuntimeException("Failed invoking Lab7.problem2(Graph,int).", e);
        }
    }

    // -------------------------------
    // Main
    // -------------------------------
    public static void main(String[] args) {
        System.out.println("===== Problem 1 (Graph-based) =====");
        runProblem1Tests();

        System.out.println();
        System.out.println("===== Problem 2 (Graph-based) =====");
        runProblem2Tests();
    }

    // ============================================================
    // Problem 1 tests (unweighted adjacency lists)
    // ============================================================
    private static void runProblem1Tests() {
        // Case A (matches typical “sort neighbors” idea)
        int[][] tc1 = new int[][]{
                {1},        // 0
                {3, 0, 2},  // 1
                {3, 1},     // 2
                {1, 4, 2},  // 3
                {3}         // 4
        };
        assertProblem1("P1-CaseA", tc1);

        // Case B (duplicates + messy order)
        int[][] tc2 = new int[][]{
                {2, 2, 1, 0},  // 0
                {3, 0},        // 1
                {0, 4, 0, 3},  // 2
                {2, 1},        // 3
                {2}            // 4
        };
        assertProblem1("P1-CaseB", tc2);

        // Case C (isolated vertex)
        int[][] tc3 = new int[][]{
                {1},     // 0
                {2, 0},  // 1
                {1},     // 2
                {}       // 3 isolated
        };
        assertProblem1("P1-CaseC", tc3);
    }

    private static void assertProblem1(String name, int[][] testCase) {
        Graph g = new Graph(testCase, false);

        // Call YOUR Lab7 implementation
        callProblem1(g);

        boolean ok = true;

        // Check: each adjacency list is sorted ascending
        for (int v = 0; v < g.noOfVertices; v++) {
            int[] a = g.edges[v];
            for (int i = 1; i < a.length; i++) {
                if (a[i - 1] > a[i]) {
                    ok = false;
                    break;
                }
            }
            if (!ok) break;
        }

        if (ok) {
            System.out.println(name + " PASS");
        } else {
            System.out.println(name + " FAIL");
            // Print a small debug view
            System.out.println("Adj lists after your problem1:");
            for (int v = 0; v < g.noOfVertices; v++) {
                System.out.println("  " + v + ": " + Arrays.toString(g.edges[v]));
            }
        }
    }

    // ============================================================
    // Problem 2 tests (weighted DAG distances)
    //
    // IMPORTANT: Graph's weighted constructor expects data length = 2*V.
    // Lab7's createProblem2() adds an extra last row for startId.
    // We'll follow the same structure here and then split like Lab7 does.
    // ============================================================
    private static void runProblem2Tests() {

        // Case A: basic DAG, includes negative edge
        // V=5, start=0
        int[][] tcA = new int[][]{
                {1, 2}, {2, 4},     // 0 -> 1(2), 2(4)
                {2, 3}, {-1, 7},    // 1 -> 2(-1), 3(7)
                {3},    {2},        // 2 -> 3(2)
                {4},    {1},        // 3 -> 4(1)
                {},     {},         // 4
                {0}                 // start
        };
        int[] expA = new int[]{0, 2, 1, 3, 4};
        assertProblem2("P2-CaseA", tcA, expA);

        // Case B: unreachable vertex
        // V=4, start=0
        int[][] tcB = new int[][]{
                {1}, {5},       // 0 -> 1(5)
                {2}, {-2},      // 1 -> 2(-2)
                {},  {},        // 2
                {},  {},        // 3 unreachable
                {0}             // start
        };
        int[] expB = new int[]{0, 5, 3, Integer.MAX_VALUE};
        assertProblem2("P2-CaseB", tcB, expB);

        // Case C: start != 0, negative best path
        // V=6, start=3
        int[][] tcC = new int[][]{
                {1, 2}, {1, 2},     // 0 -> 1(1), 2(2)
                {4},    {2},        // 1 -> 4(2)
                {4},    {-5},       // 2 -> 4(-5)
                {2, 5}, {1, 0},     // 3 -> 2(1), 5(0)
                {},     {},         // 4
                {4},    {2},        // 5 -> 4(2)
                {3}                 // start
        };
        int[] expC = new int[]{
                Integer.MAX_VALUE, Integer.MAX_VALUE, 1, 0, -4, 0
        };
        assertProblem2("P2-CaseC", tcC, expC);
    }

    private static void assertProblem2(String name, int[][] testCaseWithStart, int[] expected) {
        // Split like Lab7.testProblem2 does:
        int[][] graphData = Arrays.copyOf(testCaseWithStart, testCaseWithStart.length - 1);
        int startId = testCaseWithStart[testCaseWithStart.length - 1][0];

        Graph g = new Graph(graphData, true);

        // Call YOUR Lab7 implementation
        int[] actual = callProblem2(g, startId);

        boolean ok = Arrays.equals(actual, expected);

        if (ok) {
            System.out.println(name + " PASS");
        } else {
            System.out.println(name + " FAIL");
            System.out.println("Expected: " + Arrays.toString(expected));
            System.out.println("Actual  : " + Arrays.toString(actual));

            // Extra ruthless check: compare against Graph's Bellman-Ford (should match in DAG too)
            // (If your output differs from Bellman-Ford, your logic is wrong, period.)
            int[] bf = g.bellmanFord(startId);
            System.out.println("BellmanF: " + Arrays.toString(bf));
        }
    }
}