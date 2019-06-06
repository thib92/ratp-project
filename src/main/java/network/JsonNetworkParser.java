package network;

import com.fasterxml.jackson.databind.ObjectMapper;
import graph.Graph;
import graph.Vertex;
import network.dto.Network;
import network.dto.Route;
import network.dto.Station;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class JsonNetworkParser {

    public Network parseNetwork(String jsonFilePath) throws IOException {
        InputStream fileStream = new FileInputStream(jsonFilePath);
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(fileStream, Network.class);
    }

    public Graph<Station> buildGraph(Network network, boolean weighted) {
        Graph<Station> graph = new Graph<>();
        for (Station station: network.getStations().values()) {
            graph.addVertex(station);
        }

        for (Route route: network.getRoutes()) {
            List<String> arrets = route.getArrets();
            // Iterate on each stop of the route, except the last one
            for (int i = 0; i < arrets.size() - 1; i++) {
                Station fromStation = network.getStations().get(arrets.get(i));
                Station toStation = network.getStations().get(arrets.get(i+1));

                Vertex<Station> fromVertex = graph.getVertex(fromStation);
                Vertex<Station> toVertex = graph.getVertex(toStation);

                // If there is already an edge between these two vertices, don't add a new one
                if (graph.getEdge(fromVertex, toVertex) != null) {
                    continue;
                }

                int weight = 1;

                if (weighted) {
                    double distance = Math.sqrt(
                            Math.pow((Double.valueOf(fromStation.getLat()) - Double.valueOf(toStation.getLat())), 2) +
                            Math.pow((Double.valueOf(fromStation.getLng()) - Double.valueOf(toStation.getLng())), 2)
                    );
                    weight = (int) Math.round(distance);
                }

                graph.addEdge(fromVertex, toVertex, weight);
            }
        }

        return graph;
    }
}
