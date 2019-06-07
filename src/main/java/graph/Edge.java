package graph;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Object representing an Edge of a Graph, connecting two Vertices
 * @param <T> The type of the value represented in the Vertices of the Graph
 */
@Data
@AllArgsConstructor
public class Edge<T> {
    Vertex<T> from;
    Vertex<T> to;
    Integer weight;

    @Override
    public String toString() {
        return from.getValue() + " -> " + to.getValue();
    }
}
