package org.jgrapht.alg.shortestpath;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.util.Pair;

import java.util.*;

public class DeltaSteppingShortestPath<V, E> extends BaseShortestPathAlgorithm<V, E> {
    private static final String NEGATIVE_EDGE_WEIGHT_NOT_ALLOWED = "Negative edge weight not allowed";
    private static final String CONTAINER_IS_EMPTY = "Container is empty";
    private static final double DEFAULT_DELTA = 1.0d;
    private double delta;

    private Map<V, Set<E>> light;
    private Map<V, Set<E>> heavy;
    private Map<V, Double> tent;

    private BucketsContainer<V> bucketsContainer;
    private Map<V, Pair<Double, E>> distanceAndPredecessorMap;

    /**
     * Constructs a new instance of the algorithm for a given graph.
     *
     * @param graph the graph
     */
    public DeltaSteppingShortestPath(Graph<V, E> graph) {
        this(graph, DEFAULT_DELTA);
    }

    public DeltaSteppingShortestPath(Graph<V, E> graph, double delta) {
        super(graph);
        this.delta = delta;
        light = new HashMap<>();
        heavy = new HashMap<>();
        tent = new HashMap<>();
        bucketsContainer = new BucketsContainer<>();
        distanceAndPredecessorMap = new HashMap<>();
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

    private void ensurePositiveWeights(Graph<V, E> graph) {
        for (E e : graph.edgeSet()) {
            if (graph.getEdgeWeight(e) < 0.0) {
                throw new IllegalArgumentException(NEGATIVE_EDGE_WEIGHT_NOT_ALLOWED);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SingleSourcePaths<V, E> getPaths(V source) {
        if (!graph.containsVertex(source)) {
            throw new IllegalArgumentException(GRAPH_MUST_CONTAIN_THE_SOURCE_VERTEX);
        }
        ensurePositiveWeights(graph);

        for (V vertex : graph.vertexSet()) {
            light.put(vertex, new HashSet<>());
            heavy.put(vertex, new HashSet<>());
            tent.put(vertex, Double.POSITIVE_INFINITY);

            for (V successor : Graphs.successorListOf(graph, vertex)) {
                E e = graph.getEdge(vertex, successor);
                if (graph.getEdgeWeight(e) > delta) {
                    heavy.get(vertex).add(e);
                } else {
                    heavy.get(vertex).add(e);
                }
            }
        }

        relax(source, null);

        while (!bucketsContainer.isEmpty()) {
            double i = bucketsContainer.firstNonEmptyBucket();

            Set<V> r = new HashSet<>();
            while (!bucketsContainer.bucketEmpty(i)) {
                Set<Pair<V, E>> lightRelaxRequests = findRequests(bucketsContainer.bucketElements(i), light);

                r.addAll(bucketsContainer.bucketElements(i));
                bucketsContainer.clearBucket(i);

                relaxRequests(lightRelaxRequests);
            }
            Set<Pair<V, E>> heavyRelaxRequests = findRequests(r, heavy);
            relaxRequests(heavyRelaxRequests);
        }
        return new TreeSingleSourcePathsImpl<>(graph, source, distanceAndPredecessorMap);
    }




    private Set<Pair<V, E>> findRequests(Collection<V> vertices, Map<V, Set<E>> edgesKind) {
        Set<Pair<V, E>> result = new HashSet<>();
        for (V vertex : vertices) {
            for (E edge : edgesKind.get(vertex)) {
                result.add(Pair.of(graph.getEdgeTarget(edge), edge));
            }
        }
        return result;
    }

    private void relaxRequests(Set<Pair<V, E>> requests) {
        requests.forEach(pair -> relax(pair.getFirst(), pair.getSecond()));
    }


    private void relax(V vertex, E edge) {
        double distance;
        if (edge == null) {
            distance = 0.0;
        } else {
            V predecessor = Graphs.getOppositeVertex(graph, edge, vertex);
            distance = tent.get(predecessor) + graph.getEdgeWeight(edge);
        }
        if (distance < tent.get(vertex)) {
            double currentBucket = Math.round(tent.get(vertex) / delta);
            double newBucket = Math.round(distance / delta);
            bucketsContainer.remove(currentBucket, vertex);
            bucketsContainer.add(newBucket, vertex);
            tent.put(vertex, distance);
            distanceAndPredecessorMap.put(vertex, Pair.of(distance, edge));
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

    static class BucketsContainer<BT> {
        private Map<Double, Bucket<BT>> buckets;
        private int numOfNonEmptyBuckets;

        BucketsContainer() {
            buckets = new HashMap<>();
        }

        boolean add(double bucketIdentifier, BT element) {
            if (!buckets.containsKey(bucketIdentifier)) {
                buckets.put(bucketIdentifier, new Bucket<>());
            }
            boolean wasEmpty = buckets.get(bucketIdentifier).isEmpty();
            boolean added = buckets.get(bucketIdentifier).add(element);
            if (wasEmpty && added) {
                numOfNonEmptyBuckets++;
            }
            return added;
        }

        boolean remove(double bucketIdentifier, BT element) {
            boolean result = false;
            if (buckets.containsKey(bucketIdentifier)) {
                boolean wasEmpty = buckets.get(bucketIdentifier).isEmpty();
                result = buckets.get(bucketIdentifier).remove(element);
                boolean becameEmpty = buckets.get(bucketIdentifier).isEmpty();
                if (!wasEmpty && becameEmpty) {
                    numOfNonEmptyBuckets--;
                }
            }
            return result;
        }

        List<BT> bucketElements(double bucketIdentifier) {
            List<BT> result = null;
            if (buckets.containsKey(bucketIdentifier)) {
                result = buckets.get(bucketIdentifier).elements();
            }
            return result;
        }

        void clearBucket(double bucketIdentifier) {
            if (buckets.containsKey(bucketIdentifier)) {
                boolean wasEmpty = buckets.get(bucketIdentifier).isEmpty();
                buckets.get(bucketIdentifier).clear();
                if (!wasEmpty) {
                    numOfNonEmptyBuckets--;
                }
            }
        }

        double firstNonEmptyBucket() {
            if (isEmpty()) {
                throw new RuntimeException(CONTAINER_IS_EMPTY);
            }
            double result = Double.POSITIVE_INFINITY;
            for (Double identifier : buckets.keySet()) {
                if (identifier < result && !buckets.get(identifier).isEmpty()) {
                    result = identifier;
                }
            }
            return result;
        }

        boolean bucketEmpty(double bucketIdentifier) {
            if (!buckets.containsKey(bucketIdentifier)) {
                throw new IllegalArgumentException("There is not bucket with identifier "
                        + bucketIdentifier + " in the container");
            }
            return buckets.get(bucketIdentifier).isEmpty();
        }

        boolean isEmpty() {
            return numOfNonEmptyBuckets == 0;
        }
    }

    static class Bucket<T> {
        private Map<T, Node<T>> nodesMap;
        private Node<T> head;
        private int size;

        Bucket() {
            nodesMap = new HashMap<>();
        }

        boolean add(T element) {
            Node<T> node = new Node<>(element);
            linkFirst(node);
            nodesMap.put(element, node);
            size++;
            return true;
        }

        private void linkFirst(Node<T> node) {
            node.next = head;
            head = node;
        }


        boolean remove(T element) {
            boolean result = false;
            Node<T> node = nodesMap.get(element);
            if (node != null) {
                unlink(node);
                nodesMap.remove(element);
                size--;
                result = true;
            }
            return result;
        }

        private void unlink(Node<T> node) {
            if (node.prev != null) {
                node.prev.next = node.next;
            }
            if (node.next != null) {
                node.next.prev = node.prev;
            }
            node.prev = null;
            node.next = null;
            if (node.equals(head)) {
                head = null;
            }
        }

        List<T> elements() {
            List<T> result = new ArrayList<>();
            Node<T> it = head;
            while (it != null) {
                result.add(it.value);
                it = it.next;
            }
            return result;
        }

        boolean isEmpty() {
            return size == 0;
        }

        void clear() {
            head = null;
            size = 0;
            nodesMap.clear();
        }

        static class Node<NT> {
            NT value;
            Node<NT> prev;
            Node<NT> next;

            public Node(NT value) {
                this.value = value;
            }
        }
    }
}
