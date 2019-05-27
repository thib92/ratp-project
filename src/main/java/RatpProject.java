import network.JsonNetworkParser;
import network.dto.Network;
import network.dto.Station;

import java.io.IOException;

public class RatpProject {
    public static void main(String[] args) {
        JsonNetworkParser networkParser = new JsonNetworkParser();
        try {
            Network network = networkParser.parseNetwork("src/main/resources/reseau.json");
            for (Station station: network.getStations().values()) {
                System.out.println(station.getNom());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
