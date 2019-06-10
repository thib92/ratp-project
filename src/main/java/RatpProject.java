import graph.Edge;
import graph.Graph;
import graph.Vertex;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import network.JsonNetworkParser;
import network.dto.Network;
import network.dto.Station;
import util.Pair;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import static java.util.Collections.reverseOrder;

public class RatpProject {

    private static Network network;

    public static void main(String[] args) throws IOException {
        JsonNetworkParser networkParser = new JsonNetworkParser();
        network = networkParser.parseNetwork("src/main/resources/reseau.json");
        Graph<Station> stationGraph = networkParser.buildGraph(network, false);
        Graph<Station> weightedStationGraph = networkParser.buildGraph(network, true);

        // Get the longest path
        Map.Entry<Pair, List<Vertex<Station>>> longestPath = getLongestPath(weightedStationGraph, true);
        Pair stationPair = longestPath.getKey();
        System.out.println(String.format("Longest path is between %s and %s", stationPair.getKey(), stationPair.getValue()));

        // Get the edges with the highest betweenness
        //LinkedHashMap<Edge<Station>, Integer> edgesBetweenCulsters = getEdgesBetweenCulsters(stationGraph);
        //for (Map.Entry<Edge<Station>, Integer> edgeIntegerEntry: edgesBetweenCulsters.entrySet()){
        //    Edge<Station> edge = edgeIntegerEntry.getKey();
        //    int betweenness = edgeIntegerEntry.getValue();
        //    System.out.println(String.format("Edege between %s and %s has a betweenness of %s", edge.getFrom().getValue(), edge.getTo().getValue(), betweenness));
        //}
    }

    /**
     * Find the shortest path for any directed pair of stations
     * @param stationGraph The directed graph with stations as nodes
     * @return A map of station pairs to the path from station A to station B
     */
    private static Map<Pair, List<Vertex<Station>>> shortestPaths(Graph<Station> stationGraph, boolean useDijkstra) {
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
                        List<Vertex<Station>> shortestPath;
                        if (useDijkstra) {
                            shortestPath = stationGraph.dijkstra(stationPair.getKey(), stationPair.getValue());
                        } else {
                            shortestPath = stationGraph.bfs(stationPair.getKey(), stationPair.getValue());
                        }
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
    private static Map.Entry<Pair, List<Vertex<Station>>> getLongestPath(Graph<Station> stationGraph, boolean useDijkstra) {
        Map<Pair, List<Vertex<Station>>> shortestPaths = shortestPaths(stationGraph, useDijkstra);

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


    private static Map<Edge<Station>, Integer> getEdgesBetweennesses(Graph<Station> stationGraph) {
        Map<Pair, List<Vertex<Station>>> shortestPaths = shortestPaths(stationGraph, true);

        Map<Edge<Station>, Integer> edgeBetweennesses = new HashMap<>();
        for (Edge<Station> edge: stationGraph.getEdges()) {
            edgeBetweennesses.put(edge, 0);
        }
        for (Map.Entry<Pair, List<Vertex<Station>>> pathPair: shortestPaths.entrySet()) {
            List<Vertex<Station>> path = pathPair.getValue();
            if (path == null) {
                continue;
            }
            for (int i = 0; i < path.size() - 1; i++) {
                Edge<Station> edge = stationGraph.getEdge(path.get(i), path.get(i+1));
                Integer betweenness  = edgeBetweennesses.get(edge);
                edgeBetweennesses.put(edge, betweenness + 1);
            }
        }

        return edgeBetweennesses;
    }

    private static LinkedHashMap<Edge<Station>, Integer> getEdgesBetweenCulsters(Graph<Station> stationGraph) {
        Map<Edge<Station>, Integer> edgesBetweennesses = getEdgesBetweennesses(stationGraph);
        return edgesBetweennesses
                .entrySet().parallelStream()
                .sorted(reverseOrder(Map.Entry.comparingByValue()))
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }
}
