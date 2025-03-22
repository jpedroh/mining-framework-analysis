package org.jgrapht.alg;
import org.jgrapht.alg.flow.*;
import org.junit.runner.*;
import org.junit.runners.*;

/**
 * A TestSuite for all tests in this package.
 *
 * @author Barak Naveh
 */
@RunWith(value = Suite.class) @Suite.SuiteClasses(value = { AStarShortestPathTest.class, AllDirectedPathsTest.class, BellmanFordShortestPathTest.class, BiconnectivityInspectorTest.class, BidirectionalDijkstraShortestPathTest.class, BlockCutpointGraphTest.class, BronKerboschCliqueFinderTest.class, ChromaticNumberTest.class, ConnectivityInspectorTest.class, CycleDetectorTest.class, DijkstraShortestPathTest.class, EdmondsKarpMFImplTest.class, EdmondsKarpMinimumSTCutTest.class, EulerianCircuitTest.class, FloydWarshallShortestPathsTest.class, GusfieldEquivalentFlowTreeTest.class, GusfieldGomoryHuCutTreeTest.class, HamiltonianCycleTest.class, KShortestPathCostTest.class, KShortestPathKValuesTest.class, KSPDiscardsValidPathsTest.class, KSPExampleTest.class, NaiveLcaFinderTest.class, NeighborIndexTest.class, PadbergRaoOddMinimumCutsetTest.class, PushRelabelMFImplTest.class, PushRelabelMinimumSTCutTest.class, StoerWagnerMinimumCutTest.class, StrongConnectivityAlgorithmTest.class, TarjanLowestCommonAncestorTest.class, TransitiveClosureTest.class, VertexCoverTest.class, WeightedVertexCoverTest.class }) public final class AllAlgTests {
}