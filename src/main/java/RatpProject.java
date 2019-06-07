import graph.Graph;
import graph.Vertex;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import network.JsonNetworkParser;
import network.dto.Network;
import network.dto.Station;
import util.StationPair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RatpProject {

    private static Network network;

    public static void main(String[] args) throws IOException, InterruptedException {
        JsonNetworkParser networkParser = new JsonNetworkParser();
        network = networkParser.parseNetwork("src/main/resources/reseau.json");
        Graph<Station> stationGraph = networkParser.buildGraph(network, false);
        Graph<Station> weightedStationGraph = networkParser.buildGraph(network, true);

        Map.Entry<StationPair, List<Vertex<Station>>> longestPath = getLongestPath(weightedStationGraph);
        StationPair stationPair = longestPath.getKey();
        System.out.println(String.format("Longest path is between %s and %s", stationPair.getFirst(), stationPair.getSecond()));
    }

    /**
     * Find the shortest path for any directed pair of stations
     * @param stationGraph The directed graph with stations as nodes
     * @return A map of station pairs to the path from station A to station B
     * @throws InterruptedException Thrown if the computation takes more than 10 minutes
     */
    private static Map<StationPair, List<Vertex<Station>>> shortestPaths(Graph<Station> stationGraph) throws InterruptedException {
        // Build a list of all stations pairs
        List<StationPair> pairs = new ArrayList<>();
        for (Station station: network.getStations().values()) {
            for (Station station2: network.getStations().values()) {
                pairs.add(new StationPair(station, station2));
            }
        }

        int pairsCount = pairs.size();
        int tasksCount = 1000;
        int pairsByThread = pairsCount / tasksCount;

        CountDownLatch countDownLatch = new CountDownLatch(tasksCount);
        ExecutorService executorService = Executors.newFixedThreadPool(46);

        try (
                ProgressBar pb = (new ProgressBarBuilder())
                        .setTaskName("Calculating shortest distances")
                        .setInitialMax(pairsCount)
                        .setUpdateIntervalMillis(500)
                        .showSpeed()
                        .build()
        ) {
            Map<StationPair, List<Vertex<Station>>> shortestPaths = new HashMap<>();

            for (int j = 0; j < tasksCount; j++) {
                int start = j * pairsByThread;
                int end = start + pairsByThread;
                if (end > pairsCount) {
                    end = pairsCount;
                }
                List<StationPair> pairsSubList = pairs.subList(start, end);
                executorService.execute(() -> {
                    for (StationPair stationPair: pairsSubList) {
                        List<Vertex<Station>> shortestPath = stationGraph.dijkstra(stationPair.getFirst(), stationPair.getSecond());
                        shortestPaths.put(stationPair, shortestPath);
                    }
                    pb.stepBy(pairsSubList.size());
                    countDownLatch.countDown();
                });
            }

            countDownLatch.await(10, TimeUnit.MINUTES);
            return shortestPaths;
        } finally {
            executorService.shutdown();
        }
    }

    /**
     * Find the diameter of a graph by computing all the shortest paths and finding the longest one
     * @param stationGraph The directed graph of which to find the diameter
     * @return A map from the Map Entry of the pair of stations with the longest shortest path to the path from station A to station B
     * @throws InterruptedException Thrown if the computation takes more than 10 minutes
     */
    private static Map.Entry<StationPair, List<Vertex<Station>>> getLongestPath(Graph<Station> stationGraph) throws InterruptedException {
        Map<StationPair, List<Vertex<Station>>> shortestPaths = shortestPaths(stationGraph);

        Map.Entry<StationPair, List<Vertex<Station>>> longestPath = null;
        for (Map.Entry<StationPair, List<Vertex<Station>>> entry: shortestPaths.entrySet()) {
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
