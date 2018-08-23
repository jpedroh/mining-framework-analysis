package org.jgrapht.alg.shortestpath;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.alg.util.Triple;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class DeltaSteppingShortestPath<V, E> extends BaseShortestPathAlgorithm<V, E> {
    private static final String NEGATIVE_EDGE_WEIGHT_NOT_ALLOWED = "Negative edge weight not allowed";
    private static final String DELTA_SHOULD_BE_POSITIVE = "Delta should be positive";

    private double delta;
    private int numOfBuckets;

    private Map<V, Set<E>> light;
    private Map<V, Set<E>> heavy;

    private Map<V, AtomicReference<Triple<Integer, Double, E>>> verticesDataMap;

    /**
     * Constructs a new instance of the algorithm for a given graph.
     *
     * @param graph the graph
     */
    public DeltaSteppingShortestPath(Graph<V, E> graph) {
        super(graph);
        delta = 0.0;
        initializeFields();
    }

    public DeltaSteppingShortestPath(Graph<V, E> graph, double delta) {
        super(graph);
        if (delta <= 0) {
            throw new IllegalArgumentException(DELTA_SHOULD_BE_POSITIVE);
        }
        this.delta = delta;
        initializeFields();
    }

    private void initializeFields() {
        light = new HashMap<>();
        heavy = new HashMap<>();
        verticesDataMap = new HashMap<>();
    }

    private void assertPositiveWeights() {
        boolean allEdgesWithNonNegativeWeights = graph.edgeSet().stream().allMatch(e -> graph.getEdgeWeight(e) >= 0.0);
        if (!allEdgesWithNonNegativeWeights) {
            throw new IllegalArgumentException(NEGATIVE_EDGE_WEIGHT_NOT_ALLOWED);
        }
    }

    private Optional<Double> maxEdgeWeight() {
        return graph.edgeSet().stream().map(graph::getEdgeWeight).max(Double::compare);
    }

    private Optional<Integer> maxEdgeOutDegree() {
        return graph.vertexSet().stream().map(graph::outDegreeOf).max(Integer::compare);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public GraphPath<V, E> getPath(V source, V sink) {
        if (!graph.containsVertex(source)) {
            throw new IllegalArgumentException(GRAPH_MUST_CONTAIN_THE_SOURCE_VERTEX);
        }
        if (!graph.containsVertex(sink)) {
            throw new IllegalArgumentException(GRAPH_MUST_CONTAIN_THE_SINK_VERTEX);
        }
        return getPaths(source).getPath(sink);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SingleSourcePaths<V, E> getPaths(V source) {
        if (!graph.containsVertex(source)) {
            throw new IllegalArgumentException(GRAPH_MUST_CONTAIN_THE_SOURCE_VERTEX);
        }

        assertPositiveWeights();
        if (delta == 0.0) {
            delta = findDelta();
        }
        fillMaps();
        numOfBuckets = numOfBuckets();

        computeShortestPaths(source);

        Map<V, Pair<Double, E>> distanceAndPredecessorMap = new HashMap<>();
        for (Map.Entry<V, AtomicReference<Triple<Integer, Double, E>>> entry : verticesDataMap.entrySet()) {
            distanceAndPredecessorMap.put(entry.getKey(),
                    Pair.of(entry.getValue().get().getSecond(),
                            entry.getValue().get().getThird()));
        }
        return new TreeSingleSourcePathsImpl<>(graph, source, distanceAndPredecessorMap);
    }

    private double findDelta() {
        double maxEdgeWeight = maxEdgeWeight().orElse(0.0);
        int maxOutDegree = maxEdgeOutDegree().orElse(1);
        return maxEdgeWeight / maxOutDegree;
    }


    private void computeShortestPaths(V source) {
        relax(source, null, 0.0);

        int firstNonEmptyBucket = 0;
        while (firstNonEmptyBucket != -1) {
            List<V> removed = new ArrayList<>();
            List<V> bucketElements = bucketElements(firstNonEmptyBucket);
            while (!bucketElements.isEmpty()) {
                removed.addAll(bucketElements);
                clearBucket(firstNonEmptyBucket);

                findAndRelaxRequests(bucketElements, light);
                bucketElements = bucketElements(firstNonEmptyBucket);
            }
            findAndRelaxRequests(removed, heavy);
            firstNonEmptyBucket = firstNonEmptyBucket();
        }
    }

    private void fillMaps() {
        graph.vertexSet().forEach(v -> {
            light.put(v, new HashSet<>());
            heavy.put(v, new HashSet<>());
            verticesDataMap.putIfAbsent(v, new AtomicReference<>(Triple.of(-1, Double.POSITIVE_INFINITY, null)));
        });
        graph.vertexSet().parallelStream().forEach(v -> {
            for (E e : graph.outgoingEdgesOf(v)) {
                if (graph.getEdgeWeight(e) > delta) {
                    heavy.get(v).add(e);
                } else {
                    light.get(v).add(e);
                }
            }
        });
    }


    private void findAndRelaxRequests(List<V> vertices, Map<V, Set<E>> edgesKind) {
        vertices.parallelStream().forEach(v -> {
            for (E e : edgesKind.get(v)) {
                relax(Graphs.getOppositeVertex(graph, e, v), e,
                        verticesDataMap.get(v).get().getSecond() + graph.getEdgeWeight(e));
            }
        });
    }

    private void relax(V v, E e, double distance) {
        boolean updated = false;
        AtomicReference<Triple<Integer, Double, E>> dataReference = verticesDataMap.get(v);
        Triple<Integer, Double, E> updatedData = Triple.of(bucketIndex(distance), distance, e);
        while (!updated) {
            Triple<Integer, Double, E> oldData = dataReference.get();
            if (distance < oldData.getSecond()) {
                updated = dataReference.compareAndSet(oldData, updatedData);
            } else {
                updated = true;
            }
        }
    }


    private int numOfBuckets() {
        return maxEdgeWeight().map(max -> (int) (Math.ceil(max / delta) + 1)).orElse(0);
    }

    private int bucketIndex(double distance) {
        return ((int) Math.round(distance / delta)) % numOfBuckets;
    }

    private int firstNonEmptyBucket() {
        return verticesDataMap.values().stream()
                .map(triple -> triple.get().getFirst())
                .filter(bucketIndex -> bucketIndex >= 0)
                .min(Integer::compare).orElse(-1);
    }

    private List<V> bucketElements(int bucket) {
        return verticesDataMap.entrySet().stream()
                .filter(entry -> entry.getValue().get().getFirst() == bucket)
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void clearBucket(int bucket) {
        List<V> bucketElements = bucketElements(bucket);
        for (V v : bucketElements) {
            Triple<Integer, Double, E> data = verticesDataMap.get(v).get();
            verticesDataMap.get(v).set(Triple.of(
                    -1,
                    data.getSecond(),
                    data.getThird()
            ));
        }
    }


    /**
     * Find a path between two vertices.
     *
     * @param graph  the graph to be searched
     * @param source the vertex at which the path should start
     * @param sink   the vertex at which the path should end
     * @param <V>    the graph vertex type
     * @param <E>    the graph edge type
     * @return a shortest path, or null if no path exists
     */
    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph, V source, V sink) {
        return new DeltaSteppingShortestPath<>(graph).getPath(source, sink);
    }
}
