package network.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Station {
    private String commune;
    private String lat;
    private String lng;
    private String nom;
    private String num;
    private String type;
    private String cp;
    private Boolean show;
    private Boolean isHub;
    private Boolean isAmbiguous;
    private List<List<Integer>> routes;
    private Map<String, List<String>> lignes;
}
