package util;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Utility class to hold an ordered pair of Stations
 */
@Data
@AllArgsConstructor
public class Pair<K, V> {
    private K key;
    private V value;
}
