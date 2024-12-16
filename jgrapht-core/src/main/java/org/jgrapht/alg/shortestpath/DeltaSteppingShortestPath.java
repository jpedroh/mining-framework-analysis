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
package org.jgrapht.alg.shortestpath;

import java.util.*;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.alg.util.Triple;


/**
 * An implementation of the parallel version of the delta-stepping algorithm.
 * <p>
 * <p>
 * The time complexity of the algorithm is
 * $O(\frac{(|V| + |E| + n_{\Delta} + m_{\Delta})}{p} + \frac{L}{\Delta}\cdot d\cdot l_{\Delta}\cdot \log n)$, where,
 * denoting $\Delta$-path as a path of total weight at most $\Delta$ with no edge repetition,
 * <ul>
 * <li>$n_{\Delta}$ - number of vertices pairs (u,v), where u and v are connected by some $\Delta$-path.</li>
 * <li>$m_{\Delta}$ - number of vertices triples (u,$v^{\prime}$,v), where u and $v^{\prime}$ are connected
 * by some $\Delta$-path and edge ($v^{\prime}$,v) has weight at most $\Delta$.</li>
 * <li>$L$ - maximal weight of a shortest path from selected source to any sink.</li>
 * <li>$d$ - maximal edge degree.</li>
 * <li>$l_{\Delta}$ - maximal number of edges in a $\Delta$-path $+1$.</li>
 * </ul>
 * <p>
 * <p>
 * The algorithm is described in the paper: U. Meyer, P. Sanders,
 * $\Delta$-stepping: a parallelizable shortest path algorithm, Journal of Algorithms,
 * Volume 49, Issue 1, 2003, Pages 114-152, ISSN 0196-6774.
 * <p>
 * <p>
 * The algorithm solves the single source shortest path problem in a graph with no
 * negative weight edges. Its advantage of the {@link DijkstraShortestPath}
 * algorithm is that it can benefit from multiple threads. While the Dijkstra`s
 * algorithm is fully sequential and the {@link BellmanFordShortestPath} algorithm
 * has high parallelism since all edges can be relaxed in parallel, the delta-stepping
 * introduces parameter delta, which, when chooses optimally, yields still good parallelism
 * and at the same time enables avoiding too many re-relaxations of the edges.
 * <p>
 * <p>
 * To prevent the necessity to synchronize threads the bucket structure is implemented here
 * as a map of vertices to their bucket indices. Furthermore, every time a vertex is inserted
 * in a bucket the tentative distance to the vertex and its predecessor should be updated in
 * order to reconstruct the shortest paths tree at the end of the computation. Therefore to be
 * able to update all the information safely it is kept in a single object which is wrapped
 * in {@link AtomicReference}.
 *
 * @param <V>
 * 		the graph vertex type
 * @param <E>
 * 		the graph edge type
 * @author Semen Chudakov
 * @see AtomicReference
 * @since August 2018
 */
public class DeltaSteppingShortestPath<V, E> extends BaseShortestPathAlgorithm<V, E> {
    /**
     * Error message for reporting the existence of an edge with negative weight.
     */
    private static final String NEGATIVE_EDGE_WEIGHT_NOT_ALLOWED = "Negative edge weight not allowed";

    /**
     * Error message for reporting that delta must be positive.
     */
    private static final String DELTA_MUST_BE_POSITIVE = "Delta must be positive";

    /**
     * The bucket width. A bucket with index $i$ therefore stores
     * a vertex v if and only if v is queued and tentative distance
     * to v $\in[i\cdot\Delta,(i+1)\cdot\Delta]$
     */
    private double delta;

    /**
     * Num of buckets in the bucket structure.
     */
    private int numOfBuckets;

    private double maxEdgeWeight;

    /**
     * Map with light edges for each vertex. An edge is considered
     * light if its weight is less than or equal to {@link #delta}.
     */
    private Map<V, Set<E>> light;

    /**
     * Map with heavy edges for each vertex. An edge is
     * considered heavy if its weight is greater than {@link #delta}.
     */
    private Map<V, Set<E>> heavy;

    /**
     * Map that stores information about each vertex.
     * <p>
     * <p>
     * In each triple the first value stands for the bucket index of a
     * vertex or $-1$ if a vertex does not belong to any bucket. The second
     * value stands for the tentative distance to a vertex. The third value
     * of each triple stands for the predecessor of a vertex in the the
     * shortest path tree. The second and the third values of each triple will
     * be used at the end of the computation to construct shortest paths tree.
     * <p>
     * <p>
     * Keeping vertex information in an {@link AtomicReference} objects allows
     * to avoid threads synchronisation. Thus a thread can safely update
     * the information using standard CAS function.
     */
    private Map<V, Triple<Integer, Double, E>> verticesDataMap;

    private ExecutorService executor;

    private ExecutorCompletionService<Void> completionService;

    private static final int NUMBER_OF_REQUESTS_PER_RELAX_TAKS = 2500;

    /**
     * Constructs a new instance of the algorithm for a given graph.
     * Initializes {@link #delta} to $0.0$ to preserve lazy computation style.
     *
     * @param graph
     * 		graph
     */
    public DeltaSteppingShortestPath(Graph<V, E> graph) {
        super(graph);
        delta = 0.0;
        init();
    }

    /**
     * Constructs a new instance of the algorithm for a given graph and delta.
     *
     * @param graph
     * 		the graph
     * @param delta
     * 		bucket width
     */
    public DeltaSteppingShortestPath(Graph<V, E> graph, double delta) {
        super(graph);
        if (delta <= 0) {
            throw new IllegalArgumentException(DELTA_MUST_BE_POSITIVE);
        }
        this.delta = delta;
        init();
    }

    /**
     * Initializes {@link #light}, {@link #heavy} and {@link #verticesDataMap} fields.
     */
    private void init() {
        light = new HashMap<>();
        heavy = new HashMap<>();
        verticesDataMap = new HashMap<>();
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        completionService = new ExecutorCompletionService<>(executor);
    }

    /**
     * Calculates max edge weight in the {@link #graph}.
     *
     * @return max edge weight
     */
    private double getMaxEdgeWeight() {
        double result = 0.0;
        double weight;
        for (E defaultWeightedEdge : graph.edgeSet()) {
            weight = graph.getEdgeWeight(defaultWeightedEdge);
            if (weight < 0) {
                throw new IllegalArgumentException(NEGATIVE_EDGE_WEIGHT_NOT_ALLOWED);
            }
            if (weight > result) {
                result = weight;
            }
        }
        return result;
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
        maxEdgeWeight = getMaxEdgeWeight();
        if (delta == 0.0) {
            delta = findDelta();
        }
        fillMaps();
        numOfBuckets = numOfBuckets();
        computeShortestPaths(source);
        Map<V, Pair<Double, E>> distanceAndPredecessorMap = new HashMap<>();
        for (Map.Entry<V, Triple<Integer, Double, E>> entry : verticesDataMap.entrySet()) {
            distanceAndPredecessorMap.put(entry.getKey(), Pair.of(entry.getValue().getSecond(), entry.getValue().getThird()));
        }
        return new TreeSingleSourcePathsImpl<>(graph, source, distanceAndPredecessorMap);
    }

    /**
     * Calculates value of {@link #delta}. The value is calculated to
     * maximal edge weight divided by maximal out-degree in the {@link #graph}
     * or $1.0$ if edge set of the {@link #graph} is empty.
     *
     * @return bucket width
     */
    private double findDelta() {
        if (maxEdgeWeight == 0) {
            return 1.0;
        } else {
            int maxOutDegree = graph.vertexSet().parallelStream().mapToInt(graph::outDegreeOf).max().orElse(0);
            return maxEdgeWeight / maxOutDegree;
        }
    }

    /**
     * Fills {@link #light}, {@link #heavy} and{@link #verticesDataMap} fields.
     */
    private void fillMaps() {
        graph.vertexSet().forEach(( v) -> {
            light.put(v, new HashSet<>());
            heavy.put(v, new HashSet<>());
            verticesDataMap.putIfAbsent(v, Triple.of(-1, Double.POSITIVE_INFINITY, null));
        });
        graph.vertexSet().parallelStream().forEach(( v) -> {
            for (E e : graph.outgoingEdgesOf(v)) {
                if (graph.getEdgeWeight(e) > delta) {
                    heavy.get(v).add(e);
                } else {
                    light.get(v).add(e);
                }
            }
        });
    }

    /**
     * Performs shortest paths computation.
     *
     * @param source the source vertex
     */
    private void computeShortestPaths(V source) {
        relax(source, null, 0.0);
        int firstNonEmptyBucket = 0;
        while (firstNonEmptyBucket <= numOfBuckets) {
            List<V> removed = new ArrayList<>();
            List<V> bucketElements = bucketElements(firstNonEmptyBucket);
            while (!bucketElements.isEmpty()) {
                removed.addAll(bucketElements);
                clearBucket(firstNonEmptyBucket);
                findAndRelaxRequests(bucketElements, light);
                bucketElements = bucketElements(firstNonEmptyBucket);
            } 
            findAndRelaxRequests(removed, heavy);
            firstNonEmptyBucket++;
        } 
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (java.lang.InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * For each vertex v in {@code vertices} relaxes all edges emanating from v
     * that are present in {@code edgesKind#get(v)}.
     * Elements in {@code vertices} are processed in parallel.
     *
     * @param vertices  vertices
     * @param edgesKind vertex to edges map
     */
    private void findAndRelaxRequests(List<V> vertices, Map<V, Set<E>> edgesKind) {
        List<Runnable> tasks = generateRelaxTasks(vertices, edgesKind);
        for (Runnable task : tasks) {
            completionService.submit(task, null);
        }
        for (int i = 0; i < tasks.size(); i++) {
            try {
                completionService.take();
            } catch (java.lang.InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Runnable> generateRelaxTasks(List<V> vertices, Map<V, Set<E>> edgesKind) {
        List<Runnable> tasks = new ArrayList<>();
        int begin = 0;
        int end = 0;
        while (begin < vertices.size()) {
            int numOfRequests = 0;
            while (numOfRequests < NUMBER_OF_REQUESTS_PER_RELAX_TAKS && end < vertices.size()) {
                numOfRequests += edgesKind.get(vertices.get(end)).size();
                end++;
            }
            if (numOfRequests != 0) {
                tasks.add(new RelaxTask(vertices.subList(begin, end), edgesKind));
            }
            begin = end;
        }
        return tasks;
    }

    class RelaxTask implements Runnable {
        private List<V> vertices;

        Map<V, Set<E>> edgesKind;

        RelaxTask(List<V> vertices, Map<V, Set<E>> edgesKind) {
            this.vertices = vertices;
            this.edgesKind = edgesKind;
        }

        @Override
        public void run() {
            for (V v : vertices) {
                for (E e : edgesKind.get(v)) {
                    relax(Graphs.getOppositeVertex(graph, e, v), e, verticesDataMap.get(v).getSecond() + graph.getEdgeWeight(e));
                }
            }
        }
    }

    /**
     * Performs relaxation in parallel-safe fashion. Vertex data {@code v} is
     * considered updated once either {@code distance} is greater than or equal
     * to tentative distance in {@link #verticesDataMap} or
     * {@link AtomicReference#compareAndSet(Object, Object)} returned {@code true}.
     *
     * @param v        vertex
     * @param e        edge to predecessor
     * @param distance distance
     */
    private void relax(V v, E e, double distance) {
        boolean updated = false;
        Triple<Integer, Double, E> updatedData = Triple.of(bucketIndex(distance), distance, e);
        while (!updated) {
            Triple<Integer, Double, E> oldData = verticesDataMap.get(v);
            updated = (!(distance < oldData.getSecond())) || verticesDataMap.replace(v, oldData, updatedData);
        } 
    }

    /**
     * Calculates num of buckets in the bucket structure.
     *
     * @return num of buckets
     */
    private int numOfBuckets() {
        return ((int) (Math.ceil(maxEdgeWeight / delta) + 1));
    }

    /**
     * Calculates bucket index for a given {@code distance}.
     *
     * @param distance distance
     * @return bucket index
     */
    private int bucketIndex(double distance) {
        return ((int) Math.round(distance / delta)) % numOfBuckets;
    }

    /**
     * Finds all elements of the given {@code bucket}.
     *
     * @param bucket bucket index
     * @return bucket elements
     */
    private List<V> bucketElements(int bucket) {
        return verticesDataMap.entrySet().stream().filter(( entry) -> entry.getValue().getFirst() == bucket).map(Map.Entry::getKey).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Empty given {@code bucket}. Sets bucket index
     * to $-1$ for all vertices in the given {@code bucket}.
     *
     * @param bucket bucket index
     */
    private void clearBucket(int bucket) {
        List<V> bucketElements = bucketElements(bucket);
        for (V v : bucketElements) {
            Triple<Integer, Double, E> data = verticesDataMap.get(v);
            verticesDataMap.put(v, Triple.of(-1, data.getSecond(), data.getThird()));
        }
    }
}