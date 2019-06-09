import graph.Graph;
import graph.Vertex;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import network.JsonNetworkParser;
import network.dto.Network;
import network.dto.Station;
import util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RatpProject {

    private static Network network;

    public static void main(String[] args) throws IOException {
        JsonNetworkParser networkParser = new JsonNetworkParser();
        network = networkParser.parseNetwork("src/main/resources/reseau.json");
        Graph<Station> stationGraph = networkParser.buildGraph(network, false);
        Graph<Station> weightedStationGraph = networkParser.buildGraph(network, true);

        Map.Entry<Pair, List<Vertex<Station>>> longestPath = getLongestPath(weightedStationGraph);
        Pair stationPair = longestPath.getKey();
        System.out.println(String.format("Longest path is between %s and %s", stationPair.getKey(), stationPair.getValue()));
    }

    /**
     * Find the shortest path for any directed pair of stations
     * @param stationGraph The directed graph with stations as nodes
     * @return A map of station pairs to the path from station A to station B
     */
    private static Map<Pair, List<Vertex<Station>>> shortestPaths(Graph<Station> stationGraph) {
        // Build a list of all stations pairs
        List<Pair<Station, Station>> pairs = new ArrayList<>();
        for (Station station: network.getStations().values()) {
            for (Station station2: network.getStations().values()) {
                pairs.add(new Pair<>(station, station2));
            }
        }

        try (
                ProgressBar pb = (new ProgressBarBuilder())
                        .setTaskName("Calculating shortest distances")
                        .setInitialMax(pairs.size())
                        .setUpdateIntervalMillis(500)
                        .showSpeed()
                        .build()
        ) {
            return pairs.parallelStream()
                    .map(stationPair -> {
                        List<Vertex<Station>> shortestPath = stationGraph.dijkstra(stationPair.getKey(), stationPair.getValue());
                        return new Pair<>(stationPair, shortestPath);
                    })
                    .peek((stationPair -> pb.step()))
                    .collect(HashMap::new, (m, pair) -> m.put(pair.getKey(), pair.getValue()), HashMap::putAll);

        }
    }

    /**
     * Find the diameter of a graph by computing all the shortest paths and finding the longest one
     * @param stationGraph The directed graph of which to find the diameter
     * @return A map from the Map Entry of the pair of stations with the longest shortest path to the path from station A to station B
     */
    private static Map.Entry<Pair, List<Vertex<Station>>> getLongestPath(Graph<Station> stationGraph) {
        Map<Pair, List<Vertex<Station>>> shortestPaths = shortestPaths(stationGraph);

        Map.Entry<Pair, List<Vertex<Station>>> longestPath = null;
        for (Map.Entry<Pair, List<Vertex<Station>>> entry: shortestPaths.entrySet()) {
            // List will be null if stations cannot be connected
            if (entry.getValue() == null) {
                continue;
            }
            if (longestPath == null || longestPath.getValue().size() < entry.getValue().size()) {
                longestPath = entry;
            }
        }

        return longestPath;
    }
}
