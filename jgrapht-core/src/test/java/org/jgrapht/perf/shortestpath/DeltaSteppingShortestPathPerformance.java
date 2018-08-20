package org.jgrapht.perf.shortestpath;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DeltaSteppingShortestPath;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class DeltaSteppingShortestPathPerformance {


    @Test
    public void runBenchmark() throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + DeltaSteppingCompleteGraphBenchmark.class.getSimpleName() + ".*")
//                .include(".*" + DeltaSteppingSparseGraphBenchmark.class.getSimpleName() + ".*")
                .mode(Mode.SingleShotTime)
                .timeUnit(TimeUnit.MILLISECONDS)
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .shouldFailOnError(true)
                .shouldDoGC(true).build();
        new Runner(opt).run();
    }

    public static class DeltaSteppingCompleteGraphBenchmark {

        @Benchmark
        public ShortestPathAlgorithm.SingleSourcePaths<Integer, DefaultWeightedEdge> runBenchmark(CompleteGraphData data) {
            return new DeltaSteppingShortestPath<>(data.graph, 1.0 / data.graphsSize, false).getPaths(0);
        }

        @State(Scope.Benchmark)
        public static class CompleteGraphData {
            @Param({"1000"})
            int graphsSize;
            DefaultUndirectedWeightedGraph<Integer, DefaultWeightedEdge> graph;

            @Setup(Level.Trial)
            public void generateGraph() {
                this.graph = new DefaultUndirectedWeightedGraph<>(DefaultWeightedEdge.class);
                graph.setVertexSupplier(new Supplier<Integer>() {
                    private int i;

                    @Override
                    public Integer get() {
                        return i++;
                    }
                });
                CompleteGraphGenerator<Integer, DefaultWeightedEdge> generator = new CompleteGraphGenerator<>(graphsSize);
                generator.generateGraph(graph, null);
                graph.edgeSet().forEach(e -> graph.setEdgeWeight(e, Math.random()));
            }
        }
    }

    public static class DeltaSteppingSparseGraphBenchmark {

        @Benchmark
        public ShortestPathAlgorithm.SingleSourcePaths<Integer, DefaultWeightedEdge> runBenchmark(SparseGraphData data) {
            return new DeltaSteppingShortestPath<>(data.graph, 1.0 / data.edgeDegree, false).getPaths(0);
        }

        @State(Scope.Benchmark)
        public static class SparseGraphData {
            @Param({"10000"})
            int graphSize;
            @Param({"16"})
            public int edgeDegree;
            public Graph<Integer, DefaultWeightedEdge> graph;

            @Setup(Level.Trial)
            public void generate() {
                this.graph = new DefaultUndirectedWeightedGraph<>(DefaultWeightedEdge.class);

                for (int i = 0; i < graphSize; i++) {
                    graph.addVertex(i);
                }
                for (int i = 0; i < graphSize; i++) {
                    for (int j = 0; j < edgeDegree; j++) {
                        Graphs.addEdge(graph, i, (i + j) % graphSize, Math.random());
                    }
                }
            }
        }
    }
}

