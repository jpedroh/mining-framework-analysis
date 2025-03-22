package org.jgrapht.alg;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.jgrapht.experimental.isomorphism.IsomorphismInspectorTest;

/**
 * A TestSuite for all tests in this package.
 *
 * @author Barak Naveh
 */
public final class AllAlgTests {
  private AllAlgTests() {
  }

  /**
     * Creates a test suite for all tests in this package.
     *
     * @return a test suite for all tests in this package.
     */
  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new TestSuite(ConnectivityInspectorTest.class));
    suite.addTest(new TestSuite(DijkstraShortestPathTest.class));
    suite.addTest(new TestSuite(BellmanFordShortestPathTest.class));
    suite.addTest(new TestSuite(FloydWarshallShortestPathsTest.class));
    suite.addTest(new TestSuite(VertexCoversTest.class));
    suite.addTest(new TestSuite(CycleDetectorTest.class));
    suite.addTest(new TestSuite(BronKerboschCliqueFinderTest.class));
    suite.addTest(new TestSuite(TransitiveClosureTest.class));
    suite.addTest(new TestSuite(BiconnectivityInspectorTest.class));
    suite.addTest(new TestSuite(BlockCutpointGraphTest.class));
    suite.addTest(new TestSuite(KShortestPathCostTest.class));
    suite.addTest(new TestSuite(KShortestPathKValuesTest.class));
    suite.addTest(new TestSuite(KSPExampleTest.class));
    suite.addTest(new TestSuite(KSPDiscardsValidPathsTest.class));
    suite.addTestSuite(IsomorphismInspectorTest.class);
    suite.addTest(new TestSuite(EdmondsKarpMaximumFlowTest.class));
    suite.addTest(new TestSuite(ChromaticNumberTest.class));
    suite.addTest(new TestSuite(EulerianCircuitTest.class));
    suite.addTest(new TestSuite(HamiltonianCycleTest.class));
    suite.addTest(new TestSuite(MinimumSpanningTreeTest.class));
    suite.addTest(new TestSuite(StoerWagnerMinimumCutTest.class));
    suite.addTest(new TestSuite(EdmondsBlossomShrinkingTest.class));
    suite.addTest(new TestSuite(MinSourceSinkCutTest.class));
    suite.addTest(new TestSuite(HopcroftKarpBipartiteMatchingTest.class));
    suite.addTest(new TestSuite(KuhnMunkresMinimalWeightBipartitePerfectMatchingTest.class));
    suite.addTest(new TestSuite(
<<<<<<< /usr/src/app/output/jgrapht/jgrapht/25b41a1ec2a192c2523a53c3b75dd01495fb9d86/jgrapht-core/src/test/java/org/jgrapht/alg/AllAlgTests.java/left.java
    GabowSCCTest
=======
    TarjanLowestCommonAncestorTest
>>>>>>> /usr/src/app/output/jgrapht/jgrapht/25b41a1ec2a192c2523a53c3b75dd01495fb9d86/jgrapht-core/src/test/java/org/jgrapht/alg/AllAlgTests.java/right.java
    .class));
    return suite;
  }
}