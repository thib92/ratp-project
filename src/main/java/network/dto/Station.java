package network.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(of = {"num", "nom"})
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

    @Override
    public String toString() {
        return nom;
    }
}
