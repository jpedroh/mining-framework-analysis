/*
 * (C) Copyright 2018-2018, by Semen Chudakov and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package org.jgrapht.perf.shortestpath;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.alg.shortestpath.DeltaSteppingShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.util.Triple;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.util.SupplierUtil;
import org.openjdk.jmh.annotations.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A benchmark comparing {@link DeltaSteppingShortestPath} to {@link org.jgrapht.alg.shortestpath.DijkstraShortestPath}
 * and {@link org.jgrapht.alg.shortestpath.BellmanFordShortestPath}.
 * The benchmark test the algorithms on dense and sparse random graphs.
 *
 * @author Semen Chudakov
 */
@BenchmarkMode(Mode.SampleTime)
@Fork(value = 1, warmups = 0)
@Warmup(iterations = 3, time = 10)
@Measurement(iterations = 8, time = 10)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class DeltaSteppingShortestPathPerformance {

    @Benchmark
    public ShortestPathAlgorithm.SingleSourcePaths<Integer, DefaultWeightedEdge> testSparseDeltaStepping(SparseGraphData data) {
        return new DeltaSteppingShortestPath<>(data.graph, 1.0).getPaths(0);
    }

    @Benchmark
    public ShortestPathAlgorithm.SingleSourcePaths<Integer, DefaultWeightedEdge> testSparseDijkstra(SparseGraphData data) {
        return new DijkstraShortestPath<>(data.graph).getPaths(0);
    }

    @Benchmark
    public ShortestPathAlgorithm.SingleSourcePaths<Integer, DefaultWeightedEdge> testSparseBellmanFord(SparseGraphData data) {
        return new BellmanFordShortestPath<>(data.graph).getPaths(0);
    }

    @Benchmark
    public ShortestPathAlgorithm.SingleSourcePaths<Integer, DefaultWeightedEdge> testDenseDeltaStepping(DenseGraphData data) {
        return new DeltaSteppingShortestPath<>(data.graph, 1.0 / data.graphSize).getPaths(0);
    }

    @Benchmark
    public ShortestPathAlgorithm.SingleSourcePaths<Integer, DefaultWeightedEdge> testDijkstraDense(DenseGraphData data) {
        return new DijkstraShortestPath<>(data.graph).getPaths(0);
    }

    @Benchmark
    public ShortestPathAlgorithm.SingleSourcePaths<Integer, DefaultWeightedEdge> testBellmanFordDense(DenseGraphData data) {
        return new BellmanFordShortestPath<>(data.graph).getPaths(0);
    }

    @BenchmarkMode(Mode.SampleTime)
    @Fork(value = 1, warmups = 0)
    @Warmup(iterations = 3, time = 10)
    @Measurement(iterations = 8, time = 10)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public static class MaxEdgeWeightAssertPositiveWeightsBenchmark {
        @Benchmark
        public Object[] testSequentialStreams(DenseGraphData data) {
            Boolean allEdgesWithNonNegativeWeights = data.graph.edgeSet().stream().map(data.graph::getEdgeWeight).allMatch(weight -> weight >= 0);
            if (!allEdgesWithNonNegativeWeights) {
                throw new IllegalArgumentException("smth");
            }
            Double maxEdgeWeight = data.graph.edgeSet().stream().map(data.graph::getEdgeWeight).max(Double::compare).orElse(0.0);
            return new Object[]{allEdgesWithNonNegativeWeights, maxEdgeWeight};
        }

        @Benchmark
        public double testSequentialForeachInStream(DenseGraphData data) {
            final double[] result = {0.0};
            data.graph.edgeSet().stream().mapToDouble(data.graph::getEdgeWeight).forEach(weight -> {
                if (weight < 0) {
                    throw new IllegalArgumentException("smth");
                }
                if (weight > result[0]) {
                    result[0] = weight;
                }
            });
            return result[0];
        }

        @Benchmark
        public double testSequentialForeachLoop(DenseGraphData data) {
            double result = 0.0;
            double weight;
            for (DefaultWeightedEdge defaultWeightedEdge : data.graph.edgeSet()) {
                weight = data.graph.getEdgeWeight(defaultWeightedEdge);
                if (weight < 0) {
                    throw new IllegalArgumentException("smth");
                }
                if (weight > result) {
                    result = weight;
                }
            }
            return result;
        }

        @Benchmark
        public Object[] testTwoParallelStreams(DenseGraphData data) {
            Boolean allEdgesWithNonNegativeWeights = data.graph.edgeSet().parallelStream().map(data.graph::getEdgeWeight).allMatch(weight -> weight >= 0);
            if (!allEdgesWithNonNegativeWeights) {
                throw new IllegalArgumentException("smth");
            }
            Double maxEdgeWeight = data.graph.edgeSet().parallelStream().map(data.graph::getEdgeWeight).max(Double::compare).orElse(0.0);
            return new Object[]{allEdgesWithNonNegativeWeights, maxEdgeWeight};
        }
    }

    @BenchmarkMode(Mode.SampleTime)
    @Fork(value = 1, warmups = 0)
    @Warmup(iterations = 3, time = 10)
    @Measurement(iterations = 8, time = 10)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public static class MaxOutDegreeBenchmark {

        @Benchmark
        public int testSequentialStream(DenseGraphData data) {
            return data.graph.vertexSet().stream().mapToInt(data.graph::outDegreeOf).max().orElse(0);
        }

        @Benchmark
        public int testParallelStream(DenseGraphData data) {
            return data.graph.vertexSet().parallelStream().mapToInt(data.graph::outDegreeOf).max().orElse(0);
        }
    }

    @BenchmarkMode(Mode.SampleTime)
    @Fork(value = 1, warmups = 0)
    @Warmup(iterations = 3, time = 10)
    @Measurement(iterations = 8, time = 10)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public static class FillMapsBenchmark {
        @Benchmark
        public Object[] testHashMap(DenseGraphData data) {
            double delta = 1 / 1000;
            Map<Integer, Set<DefaultWeightedEdge>> light = new HashMap<>();
            Map<Integer, Set<DefaultWeightedEdge>> heavy = new HashMap<>();
            Map<Integer, AtomicReference<Triple<Integer, Double, DefaultWeightedEdge>>> verticesDataMap = new HashMap<>();
            data.graph.vertexSet().forEach(v -> {
                light.put(v, new HashSet<>());
                heavy.put(v, new HashSet<>());
                verticesDataMap.putIfAbsent(v, new AtomicReference<>(Triple.of(-1, Double.POSITIVE_INFINITY, null)));
            });
            data.graph.vertexSet().parallelStream().forEach(v -> {
                for (DefaultWeightedEdge e : data.graph.outgoingEdgesOf(v)) {
                    if (data.graph.getEdgeWeight(e) > delta) {
                        heavy.get(v).add(e);
                    } else {
                        light.get(v).add(e);
                    }
                }
            });
            return new Object[]{light, heavy, verticesDataMap};
        }

        @Benchmark
        public Object[] testConcurrentHashMap(DenseGraphData data) {
            double delta = 1 / 1000;
            Map<Integer, Set<DefaultWeightedEdge>> light = new ConcurrentHashMap<>();
            Map<Integer, Set<DefaultWeightedEdge>> heavy = new ConcurrentHashMap<>();
            Map<Integer, Triple<Integer, Double, DefaultWeightedEdge>> verticesDataMap = new ConcurrentHashMap<>();
            data.graph.vertexSet().parallelStream().forEach(v -> {
                light.put(v, new HashSet<>());
                heavy.put(v, new HashSet<>());
                verticesDataMap.putIfAbsent(v,Triple.of(-1, Double.POSITIVE_INFINITY, null));
                for (DefaultWeightedEdge e : data.graph.outgoingEdgesOf(v)) {
                    if (data.graph.getEdgeWeight(e) > delta) {
                        heavy.get(v).add(e);
                    } else {
                        light.get(v).add(e);
                    }
                }
            });
            return new Object[]{light, heavy, verticesDataMap};
        }
    }

    @State(Scope.Benchmark)
    public static class SparseGraphData {
        @Param({"10000"})
        int graphSize;
        @Param({"50"})
        int edgeDegree;
        Graph<Integer, DefaultWeightedEdge> graph;

        @Setup(Level.Iteration)
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

    @State(Scope.Benchmark)
    public static class DenseGraphData {
        @Param({"1000"})
        public int graphSize;
        public DefaultUndirectedWeightedGraph<Integer, DefaultWeightedEdge> graph;

        @Setup(Level.Iteration)
        public void generateGraph() {
            this.graph = new DefaultUndirectedWeightedGraph<>(DefaultWeightedEdge.class);
            graph.setVertexSupplier(SupplierUtil.createIntegerSupplier());
            CompleteGraphGenerator<Integer, DefaultWeightedEdge> generator = new CompleteGraphGenerator<>(graphSize);
            generator.generateGraph(graph);
            graph.edgeSet().forEach(e -> graph.setEdgeWeight(e, Math.random()));
        }
    }
}