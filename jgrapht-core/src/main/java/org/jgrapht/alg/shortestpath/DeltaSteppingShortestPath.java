package org.jgrapht.alg.shortestpath;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.alg.util.UnorderedPair;

import java.util.*;
import java.util.stream.Collectors;

public class DeltaSteppingShortestPath<V, E> extends BaseShortestPathAlgorithm<V, E> {
    private static final String NEGATIVE_EDGE_WEIGHT_NOT_ALLOWED = "Negative edge weight not allowed";
    private static final String CONTAINER_IS_EMPTY = "Container is empty";
    private static final String DELTA_SHOULD_POSITIVE = "Delta should be positive";
    private static final String EDGE_SET_IS_EMPTY = "Edge set is empty";
    private static final Double HASH_ARRAY_DEFAULT_VALUE = Double.POSITIVE_INFINITY;

    private double delta;
    private boolean useShortcuts;

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
        super(graph);
        delta = findDelta();
        useShortcuts = false;
        initializeFields();
    }

    public DeltaSteppingShortestPath(Graph<V, E> graph, double delta, boolean useShortcuts) {
        super(graph);
        if (delta <= 0) {
            throw new IllegalArgumentException(DELTA_SHOULD_POSITIVE);
        }
        this.delta = delta;
        this.useShortcuts = useShortcuts;
        initializeFields();
    }

    private void initializeFields() {
        light = new HashMap<>();
        heavy = new HashMap<>();
        tent = new HashMap<>();
        bucketsContainer = new BucketsContainer<>(numOfBuckets());
        distanceAndPredecessorMap = new HashMap<>();
    }

    private int numOfBuckets() {
        return maxEdgeWeight().map(max -> (int) (Math.ceil(max / delta) + 1)).orElse(0);
    }

    private void assertPositiveWeights() {
        boolean allAdgesWithNonNegativeWeights = graph.edgeSet().stream()
                .allMatch(e -> graph.getEdgeWeight(e) >= 0.0);
        if (!allAdgesWithNonNegativeWeights) {
            throw new IllegalArgumentException(NEGATIVE_EDGE_WEIGHT_NOT_ALLOWED);
        }
    }

    private Optional<Double> minEdgeWeight() {
        return graph.edgeSet().stream().map(graph::getEdgeWeight).min(Double::compare);
    }

    private Optional<Double> maxEdgeWeight() {
        return graph.edgeSet().stream().map(graph::getEdgeWeight).max(Double::compare);
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
        if(useShortcuts){
            computeShortestPathsWithShortcuts(source);
        }else{
            computeShortestPaths(source);
        }
        return new TreeSingleSourcePathsImpl<>(graph, source, distanceAndPredecessorMap);
    }

    private void computeShortestPaths(V source) {
        for (V vertex : graph.vertexSet()) {
            light.put(vertex, new HashSet<>());
            heavy.put(vertex, new HashSet<>());
            tent.put(vertex, Double.POSITIVE_INFINITY);

            for (V successor : Graphs.successorListOf(graph, vertex)) {
                E e = graph.getEdge(vertex, successor);
                if (graph.getEdgeWeight(e) > delta) {
                    heavy.get(vertex).add(e);
                } else {
                    light.get(vertex).add(e);
                }
            }
        }
        relax(source, null, 0.0);

        while (!bucketsContainer.isEmpty()) {
            int i = bucketsContainer.firstNonEmptyBucket();
            Set<V> r = new HashSet<>();
            while (!bucketsContainer.bucketEmpty(i)) {
                Set<Triple<V, E, Double>> lightRelaxRequests = findRequests(bucketsContainer.bucketElements(i), light);
                r.addAll(bucketsContainer.bucketElements(i));
                bucketsContainer.clearBucket(i);

                relaxRequests(lightRelaxRequests);
            }
            Set<Triple<V, E, Double>> heavyRelaxRequests = findRequests(r, heavy);
            relaxRequests(heavyRelaxRequests);
        }
    }

    private void computeShortestPathsWithShortcuts(V source) {
        throw new UnsupportedClassVersionError();
    }

    private Set<Triple<V, E, Double>> findRequests(Collection<V> vertices, Map<V, Set<E>> edgesKind) {
        Set<Triple<V, E, Double>> result = new HashSet<>();
        for (V v : vertices) {
            for (E e : edgesKind.get(v)) {
                V op = Graphs.getOppositeVertex(graph, e, v);
                result.add(Triple.of(op, e, tent.get(v) + graph.getEdgeWeight(e)));
            }
        }
        return result;
    }

    private void relaxRequests(Set<Triple<V, E, Double>> requests) {
        requests.forEach(triple -> relax(triple.getFirst(), triple.getSecond(), triple.getThird()));
    }

    private void relax(V v, E e, double distance) {
        if (distance < tent.get(v)) {
            int currentBucket = bucketIndex(tent.get(v));
            int newBucket = bucketIndex(distance);
            if (currentBucket >= 0) {
                bucketsContainer.remove(currentBucket, v);
            }
            bucketsContainer.add(newBucket, v);
            tent.put(v, distance);
            distanceAndPredecessorMap.put(v, Pair.of(distance, e));
        }
    }

    private int bucketIndex(double distance) {
        int result;
        if (distance == Double.POSITIVE_INFINITY) {
            result = -1;
        } else {
            result = ((int) Math.round(distance / delta)) % bucketsContainer.size();
        }
        return result;
    }


    private double findDelta() {
        Map<Pair<V, V>, Double> found = new HashMap<>();

        Set<Triple<V, V, Double>> q = graph.vertexSet().stream()
                .map(v -> Triple.of(v, v, 0.0)).collect(Collectors.toCollection(HashSet::new));

        double maxWeight = maxEdgeWeight().orElseThrow(() -> new RuntimeException(EDGE_SET_IS_EMPTY));
        double deltaZero = minEdgeWeight().orElseThrow(() -> new RuntimeException(EDGE_SET_IS_EMPTY));
        double currentDelta = deltaZero;

        Map<V, Map<Integer, List<E>>> blocks = blocks(deltaZero);

        int upperBound = (int) Math.ceil(Math.log(maxWeight / deltaZero) / Math.log(2));
        List<Set<Quad<V, V, Double, Integer>>> t = new ArrayList<>(upperBound);
        for (int i = 0; i <= upperBound; i++) {
            t.add(new HashSet<>());
        }

        Set<Pair<V, V>> s = new HashSet<>();
        Map<Pair<V, V>, List<Double>> qPrime = new HashMap<>();

        for (int i = 0; i <= upperBound; i++) {
            System.out.println("\t\t" + i);
            System.out.println(String.join("\n", t.stream().map(Object::toString).collect(Collectors.toList())));
            Set<Pair<V, V>> sNext = new HashSet<>();
            Set<Triple<V, V, Double>> qNext = new HashSet<>();
            for (Quad<V, V, Double, Integer> quad : t.get(i)) {
                System.out.println("processed todo-list");
                V first = quad.getFirst();
                V second = quad.getSecond();
                double distance = quad.getThird();
                int block = quad.getFourth();
                for (E e : blocks.get(second).get(block)) {
                    V source = graph.getEdgeSource(e);
                    V target = graph.getEdgeTarget(e);
                    qPrime.get(Pair.of(source, target)).add(quad.getThird() + graph.getEdgeWeight(e));
                }
                if (blocks.get(second).containsKey(block + 1)) {
                    System.out.println("add to todo-list");
                    E firstEdgeFromNextBlock = blocks.get(second).get(block + 1).get(0);// block b+1 might simply not be present
                    int j = (int) Math.round(Math.log(
                            (distance + graph.getEdgeWeight(firstEdgeFromNextBlock)) / deltaZero) / Math.log(2));// might need min value not the first one
                    t.get(j).add(Quad.of(first, second, distance, block + 1));
                }
            }
            System.out.println("q " + q.size());
            System.out.println("qPrime " + qPrime.size());
            while (!q.isEmpty()) {
                double finalCurrentDelta = currentDelta;
                Set<E> lighterEdges = graph.edgeSet().stream()
                        .filter(e -> graph.getEdgeWeight(e) <= finalCurrentDelta).collect(Collectors.toSet());
                for (Triple<V, V, Double> triple : q) {
                    for (E e : lighterEdges) {
                        double edgeWeight = graph.getEdgeWeight(e);
                        if (edgeWeight <= currentDelta) {
                            V source = graph.getEdgeSource(e);
                            V target = graph.getEdgeTarget(e);
                            Pair<V, V> pairKey = Pair.of(source, target);
                            qPrime.putIfAbsent(pairKey, new ArrayList<>());
                            qPrime.get(pairKey).add(triple.getThird() + graph.getEdgeWeight(e));
                        }
                    }
                }
                Set<Triple<V, V, Double>> h = new HashSet<>();
                for (Map.Entry<Pair<V, V>, List<Double>> entry : qPrime.entrySet()) {
                    double min = entry.getValue().stream().min(Double::compare).orElseThrow(RuntimeException::new);
                    V first = entry.getKey().getFirst();
                    V second = entry.getKey().getSecond();
                    if (min < found.getOrDefault(Pair.of(first, second), HASH_ARRAY_DEFAULT_VALUE)) {
                        h.add(Triple.of(first, second, min));
                    }
                }
                q.clear();
                for (Triple<V, V, Double> triple : h) {
                    V first = triple.getFirst();
                    V second = triple.getSecond();
                    double third = triple.getThird();
                    Pair<V, V> key = Pair.of(first, second);
                    if (third < currentDelta) {
                        q.add(triple);
                        if (!found.containsKey(key)) {
                            s.add(Pair.of(first, second));
                        }
                    } else {
                        qNext.add(triple);
                        if (!found.containsKey(key)) {
                            sNext.add(UnorderedPair.of(first, second));
                        }
                    }
                    found.put(key, third);
                }
                qPrime.clear();
            }
            System.out.println("processed s");
            for (Pair<V, V> pair : s) {
                V first = pair.getFirst();
                V second = pair.getSecond();
                double x = found.get(Pair.of(first, second));
                int b = blockIndex(currentDelta, deltaZero);
                if (blocks.get(second).containsKey(b)) {
                    System.out.println("add to todo-list");
                    int j = (int) Math.round(Math.log(
                            (x + graph.getEdgeWeight(blocks.get(second).get(b).get(0))) / deltaZero));// !!!!!!!!!!! might need min value not the first
                    t.get(j).add(Quad.of(first, second, x, b));
                }
            }
            q = qNext;
            s = sNext;
            currentDelta *= 2;
        }

        return maxWeight;
    }

    private Map<V, Map<Integer, List<E>>> blocks(double deltaZero) {
        Map<V, Map<Integer, List<E>>> blocks = new HashMap<>(graph.edgeSet().size());
        for (E e : graph.edgeSet()) {
            V source = graph.getEdgeSource(e);
            int blockIndex = blockIndex(graph.getEdgeWeight(e), deltaZero);
            blocks.putIfAbsent(source, new HashMap<>());
            blocks.get(source).putIfAbsent(blockIndex, new ArrayList<>());
            blocks.get(source).get(blockIndex).add(e);
        }
        return blocks;
    }

    private int blockIndex(double edgeWeight, double deltaZero) {
        double start = deltaZero;
        double end = deltaZero * 2;
        int bucketIndex = 0;
        while (!(edgeWeight >= start && edgeWeight < end)) {
            start *= 2;
            end *= 2;
            bucketIndex += 1;
        }
        return bucketIndex;
    }

    private Set<Triple<V, V, Double>> findShortcuts(double delta) {
        Map<Pair<V, V>, Double> found = new HashMap<>();

        Set<Triple<V, V, Double>> activeConnections = graph.vertexSet().stream()
                .map(v -> Triple.of(v, v, 0.0)).collect(Collectors.toCollection(HashSet::new));

        while (!activeConnections.isEmpty()) {
            Map<Pair<V, V>, List<Double>> foundConnections = new HashMap<>();
            for (Triple<V, V, Double> triple : activeConnections) {
                for (E e : light.get(triple.getSecond())) {
                    Pair<V, V> edgeKey = Pair.of(triple.getFirst(),
                            Graphs.getOppositeVertex(graph, e, triple.getSecond()));
                    foundConnections.putIfAbsent(edgeKey, new ArrayList<>());
                    foundConnections.get(edgeKey).add(triple.getThird() + graph.getEdgeWeight(e));
                }
            }

            activeConnections.clear();
            for (Map.Entry<Pair<V, V>, List<Double>> entry : foundConnections.entrySet()) {
                Double foundConnectionsMinWeight = entry.getValue()
                        .stream().min(Double::compare).orElseThrow(RuntimeException::new);
                V first = entry.getKey().getFirst();
                V second = entry.getKey().getSecond();
                double foundShortestConnectionWeight = found.getOrDefault(Pair.of(first, second), HASH_ARRAY_DEFAULT_VALUE);
                if (foundConnectionsMinWeight <= delta && foundConnectionsMinWeight < foundShortestConnectionWeight) {
                    activeConnections.add(Triple.of(first, second, foundConnectionsMinWeight));
                }
            }
            for (Triple<V, V, Double> triple : activeConnections) {
                found.put(Pair.of(triple.getFirst(), triple.getSecond()), triple.getThird());
            }
        }

        return found.entrySet().stream()
                .map(entry -> Triple.of(entry.getKey().getFirst(),
                        entry.getKey().getSecond(),
                        entry.getValue())).collect(Collectors.toCollection(HashSet::new));
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

    static class Triple<A, B, C> {
        private final A first;
        private final B second;
        private final C third;

        Triple(A first, B second, C third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        public A getFirst() {
            return first;
        }

        public B getSecond() {
            return second;
        }

        public C getThird() {
            return third;
        }

        public static <A, B, C> Triple<A, B, C> of(A a, B b, C c) {
            return new Triple<>(a, b, c);
        }

        @Override
        public String toString() {
            return "(" + this.first + "," + this.second + "," + this.third + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Triple) {
                @SuppressWarnings("unchecked") Triple<A, B, C> casted = (Triple<A, B, C>) obj;
                return Objects.equals(first, casted.first)
                        && Objects.equals(second, casted.second)
                        && Objects.equals(third, casted.third);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, second, third);
        }
    }

    static class Quad<A, B, C, D> {
        private final A first;
        private final B second;
        private final C third;
        private final D fourth;

        public Quad(A first, B second, C third, D fourth) {
            this.first = first;
            this.second = second;
            this.third = third;
            this.fourth = fourth;
        }

        public A getFirst() {
            return first;
        }

        public B getSecond() {
            return second;
        }

        public C getThird() {
            return third;
        }

        public D getFourth() {
            return fourth;
        }

        public static <A, B, C, D> Quad<A, B, C, D> of(A a, B b, C c, D d) {
            return new Quad<>(a, b, c, d);
        }

        @Override
        public String toString() {
            return "(" + first + "," + second + "," + third + "," + fourth + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof DeltaSteppingShortestPath.Quad) {
                @SuppressWarnings("unchecked") Quad<A, B, C, D> casted = (Quad<A, B, C, D>) obj;
                return Objects.equals(first, casted.first)
                        && Objects.equals(second, casted.second)
                        && Objects.equals(third, casted.third)
                        && Objects.equals(fourth, casted.fourth);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, second, third, fourth);
        }
    }

    static class BucketsContainer<BT> {
        private final Bucket[] buckets;
        private int numOfBuckets;
        private int numOfNonEmptyBuckets;

        BucketsContainer(int numOfBuckets) {
            buckets = new Bucket[numOfBuckets];
            this.numOfBuckets = numOfBuckets;
        }

        boolean add(int bucket, BT element) {
            assertLegalBucketIndex(bucket);
            if (buckets[bucket] == null) {
                buckets[bucket] = new Bucket<BT>();
            }

            boolean wasEmpty = buckets[bucket].isEmpty();
            @SuppressWarnings("unchecked") boolean added = buckets[bucket].add(element);
            if (wasEmpty && added) {
                numOfNonEmptyBuckets++;
            }
            return added;
        }

        boolean remove(int bucket, BT element) {
            assertLegalBucketIndex(bucket);
            boolean wasEmpty = buckets[bucket].isEmpty();
            @SuppressWarnings("unchecked") boolean result = buckets[bucket].remove(element);
            boolean becameEmpty = buckets[bucket].isEmpty();
            if (!wasEmpty && becameEmpty) {
                numOfNonEmptyBuckets--;
            }
            return result;
        }

        private void assertLegalBucketIndex(double bucket) {
            if (bucket >= numOfBuckets) {
                throw new IndexOutOfBoundsException("Bucket: " + bucket + ", size: " + numOfBuckets);
            }
        }

        List<BT> bucketElements(int bucket) {
            assertLegalBucketIndex(bucket);
            @SuppressWarnings("unchecked") List<BT> result = buckets[bucket].elements();
            return result;
        }

        void clearBucket(int bucket) {
            assertLegalBucketIndex(bucket);
            if (!buckets[bucket].isEmpty()) {
                buckets[bucket].clear();
                numOfNonEmptyBuckets--;
            }
        }

        int firstNonEmptyBucket() {
            if (isEmpty()) {
                throw new RuntimeException(CONTAINER_IS_EMPTY);
            }
            int i = 0;
            for (; i < numOfBuckets; i++) {
                if (buckets[i] != null && !buckets[i].isEmpty()) {
                    break;
                }
            }
            return i;
        }

        boolean bucketEmpty(int bucket) {
            assertLegalBucketIndex(bucket);
            return buckets[bucket].isEmpty();
        }

        boolean isEmpty() {
            return numOfNonEmptyBuckets == 0;
        }

        int size() {
            return numOfBuckets;
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
