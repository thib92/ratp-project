package network.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Route {
    private List<String> arrets;
    private String direction;
    private String type;
    private String ligne;
    private Map<String, List<String>> intersections;
}
