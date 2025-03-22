package org.jgrapht.alg;
import org.jgrapht.alg.flow.EdmondsKarpMaximumFlowTest;
import org.jgrapht.alg.flow.PushRelabelMFImplTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * A TestSuite for all tests in this package.
 *
 * @author Barak Naveh
 */
@RunWith(value = Suite.class) @Suite.SuiteClasses(value = { AStarShortestPathTest.class, AllDirectedPathsTest.class, BellmanFordShortestPathTest.class, BiconnectivityInspectorTest.class, BidirectionalDijkstraShortestPathTest.class, BlockCutpointGraphTest.class, BronKerboschCliqueFinderTest.class, ChromaticNumberTest.class, ConnectivityInspectorTest.class, CycleDetectorTest.class, DijkstraShortestPathTest.class, EdmondsBlossomShrinkingTest.class, EdmondsKarpMaximumFlowTest.class, PushRelabelMFImplTest.class, EulerianCircuitTest.class, FloydWarshallShortestPathsTest.class, HamiltonianCycleTest.class, HopcroftKarpBipartiteMatchingTest.class, KShortestPathCostTest.class, KShortestPathKValuesTest.class, KSPDiscardsValidPathsTest.class, KSPExampleTest.class, KuhnMunkresMinimalWeightBipartitePerfectMatchingTest.class, MinimumSpanningTreeTest.class, MinSourceSinkCutTest.class, NaiveLcaFinderTest.class, NeighborIndexTest.class, StoerWagnerMinimumCutTest.class, StrongConnectivityAlgorithmTest.class, TarjanLowestCommonAncestorTest.class, TransitiveClosureTest.class, VertexCoverTest.class, WeightedVertexCoverTest.class }) public final class AllAlgTests {
}