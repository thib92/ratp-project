package graph;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Object representing a Vertex of a Graph, holding a value
 * @param <T> The type of the value to hold
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(of = { "value" })
public class Vertex<T> {
    private T value;
}