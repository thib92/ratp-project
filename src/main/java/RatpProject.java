import graph.Graph;
import graph.Vertex;
import me.tongfei.progressbar.ProgressBar;
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

        try (ProgressBar pb = new ProgressBar("Calculating shortest distances", pairsCount)) {
            Map<StationPair, List<Vertex<Station>>> shortestPaths = new HashMap<>();
            ExecutorService executorService = Executors.newFixedThreadPool(46);

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
                    countDownLatch.countDown();
                    pb.stepBy(pairsSubList.size());
                });
            }
            countDownLatch.await();
            executorService.shutdown();
            return shortestPaths;
        }
    }

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
