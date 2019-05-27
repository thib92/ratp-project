package network.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Network {
    private List<List<String>> corresp;
    private Map<String, Station> stations;
    private List<Route> routes;
    private Map<String, Ligne> lignes;
}
