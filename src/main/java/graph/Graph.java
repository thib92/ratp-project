package graph;

import java.util.*;

public class Graph<T> {
    private int maxDistance = Integer.MAX_VALUE;

    private List<Vertex<T>> vertices;
    private List<Edge<T>> edges;

    public Graph() {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public Graph(int maxDistance) {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        this.maxDistance = maxDistance;
    }

    public Edge<T> getEdge(Vertex<T> from, Vertex<T> to) {
        for (Edge<T> e : edges) {
            if (e.getFrom().equals(from) && e.getTo().equals(to)) {
                return e;
            }
        }
        return null;
    }

    public Vertex<T> getVertex(T value) {
        for (Vertex<T> v : vertices) {
            if (v.getValue().equals(value)) {
                return v;
            }
        }
        return null;
    }

    public void addVertex(T name) {
        Vertex<T> v = new Vertex<>(name);
        vertices.add(v);
    }

    public void addEdge(Vertex<T> from, Vertex<T> to, Integer weight) {
        Edge<T> e = new Edge<>(from, to, weight);
        edges.add(e);
    }

    public List<Vertex<T>> getNeighbours(Vertex<T> vertex) {
        List<Vertex<T>> neighbours = new ArrayList<>();
        for (Edge<T> edge: edges) {
            if (edge.getFrom().equals(vertex)) {
                neighbours.add(edge.getTo());
            }
        }
        return neighbours;
    }

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
            distances.put(vertex, maxDistance);
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

        if (distances.get(target) == maxDistance) {
            return null;
        }

        List<Vertex<T>> path = new ArrayList<>();

        Vertex<T> vertex = target;
        while (!vertex.equals(source)) {
            path.add(previous.get(vertex));
            vertex = previous.get(vertex);
        }

        Collections.reverse(path);

        return path;

    }
}
