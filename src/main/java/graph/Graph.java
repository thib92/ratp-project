package graph;

import network.dto.Station;

import java.util.*;

public class Graph<T> {

    private List<Vertex<T>> vertices;
    private List<Edge<T>> edges;

    /**
     * Graph constructor
     * Create an empty graph
     */
    public Graph() {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
    }

    /**
     * Get an Edge of the graph
     * @param from The Vertex from which the Edge starts
     * @param to The Vertex to which the Edge goes
     * @return The Edge connecting the two Vertices, or null if they are not linked by an Edge
     */
    public Edge<T> getEdge(Vertex<T> from, Vertex<T> to) {
        for (Edge<T> e : edges) {
            if (e.getFrom().equals(from) && e.getTo().equals(to)) {
                return e;
            }
        }
        return null;
    }

    /**
     * Get the Vertex representing the given value
     * @param value The value held by the researched Vertex
     * @return The Vertex holding the value, or null if the value is not in the Graph
     */
    public Vertex<T> getVertex(T value) {
        for (Vertex<T> v : vertices) {
            if (v.getValue().equals(value)) {
                return v;
            }
        }
        return null;
    }

    /**
     * Add a Vertex to the graph holding the provided value, disconnected from the rest
     * @param name The value held by the new Vertex
     */
    public void addVertex(T name) {
        Vertex<T> v = new Vertex<>(name);
        vertices.add(v);
    }

    /**
     * Connect two Vertices with a new Edge
     * @param from The Vertex from which the Edge goes
     * @param to The Vertex to which the Edge goes
     * @param weight The weight of the Edge
     */
    public void addEdge(Vertex<T> from, Vertex<T> to, Integer weight) {
        Edge<T> e = new Edge<>(from, to, weight);
        edges.add(e);
    }

    /**
     * Get all the neighbours of a Vertex
     * @param vertex The input Vertex
     * @return A List of all the Vertices where an Edge goes from the input Vertex to it
     */
    public List<Vertex<T>> getNeighbours(Vertex<T> vertex) {
        List<Vertex<T>> neighbours = new ArrayList<>();
        for (Edge<T> edge: edges) {
            if (edge.getFrom().equals(vertex)) {
                neighbours.add(edge.getTo());
            }
        }
        return neighbours;
    }

    public List<Vertex<T>> bfs(T sourceName, T targetName) {
        Vertex<T> source = getVertex(sourceName);
        Vertex<T> target = getVertex(targetName);

        if (source == null || target == null) {
            return null;
        }

        // Minimal distances to a Vertex
        Map<Vertex<T>, Integer> distances = new HashMap<>();

        // Stores if we visited a given vertex
        Map<Vertex<T>, Boolean> visited = new HashMap<>();

        // Stores the previous vertex to a vertex on the shortest path to it
        Map<Vertex<T>, Vertex<T>> previous = new HashMap<>();

        // Initialize the arrays
        for (Vertex<T> vertex: vertices) {
            distances.put(vertex, Integer.MAX_VALUE);
            visited.put(vertex, false);
            previous.put(vertex, null);
        }

        // Distance to source is 0
        distances.put(source, 0);

        visited.put(source, true);

        Stack<Vertex<T>> stack = new Stack<>();

        stack.add(source);

        System.out.println(String.format("Searching from %s to %s", sourceName, targetName));

        while (!stack.isEmpty()) {
            Vertex<T> vertex = stack.pop();
            System.out.println(stack.size());
            System.out.println(String.format("Fetching neighbors for %s", vertex.getValue()));
            for (Vertex<T> neighbor: getNeighbours(vertex)) {
                if (visited.get(neighbor)) {
                    continue;
                }
                System.out.println(String.format("Neighbor: %s", neighbor.getValue()));
                visited.put(neighbor, true);
                distances.put(neighbor, distances.get(vertex) + 1);
                previous.put(neighbor, vertex);
                stack.addAll(getNeighbours(neighbor));
            }
        }

        if (distances.get(target) == Integer.MAX_VALUE) {
            return null;
        }

        return buildPath(source, target, previous);
    }

    /**
     * Run the Dijkstra algorithm on the current Graph to find the shortest path from the source to the target
     * @param sourceName The value of the starting point
     * @param targetName The value of the end point
     * @return A List of all the Vertices to visit in order to go from the source to the target in the shortest path possible
     */
    public List<Vertex<T>> dijkstra(T sourceName, T targetName) {
        Vertex<T> source = getVertex(sourceName);
        Vertex<T> target = getVertex(targetName);

        if (source == null || target == null) {
            return null;
        }

        // Minimal distances to a Vertex
        Map<Vertex<T>, Integer> distances = new HashMap<>();

        // Stores if we visited a given vertex
        Map<Vertex<T>, Boolean> visited = new HashMap<>();

        // Stores the previous vertex to a vertex on the shortest path to it
        Map<Vertex<T>, Vertex<T>> previous = new HashMap<>();

        // Initialize the arrays
        for (Vertex<T> vertex: vertices) {
            distances.put(vertex, Integer.MAX_VALUE);
            visited.put(vertex, false);
            previous.put(vertex, null);
        }

        // Distance to source is 0
        distances.put(source, 0);

        Queue<Vertex<T>> queue = new PriorityQueue<>(Comparator.comparing(distances::get));
        queue.add(source);

        while (!queue.isEmpty()) {
            Vertex<T> vertex = queue.poll();
            if (visited.get(target)) {
                continue;
            }
            visited.put(vertex, true);

            List<Vertex<T>> neighbours = getNeighbours(vertex);

            for (Vertex<T> neighbour: neighbours) {

                if (visited.get(neighbour)) {
                    continue;
                }

                Edge<T> edge = getEdge(vertex, neighbour);

                if (edge == null) {
                    continue;
                }

                // Current distance + weight of the edge
                int totalDistance = distances.get(vertex) + edge.getWeight();

                if (totalDistance < distances.get(neighbour)) {
                    distances.put(neighbour, distances.get(vertex) + edge.getWeight());
                    previous.put(neighbour, vertex);
                    queue.remove(neighbour);
                    queue.add(neighbour);
                }
            }
        }

        if (distances.get(target) == Integer.MAX_VALUE) {
            return null;
        }

        return buildPath(source, target, previous);
    }

    private List<Vertex<T>> buildPath(Vertex<T> source, Vertex<T> target, Map<Vertex<T>, Vertex<T>> previous) {
        List<Vertex<T>> path = new ArrayList<>();

        Vertex<T> vertex = target;
        while (!source.equals(vertex)) {
            path.add(previous.get(vertex));
            vertex = previous.get(vertex);
        }

        Collections.reverse(path);

        return path;
    }
}
