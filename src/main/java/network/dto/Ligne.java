package network.dto;

import lombok.Data;

import java.util.List;

@Data
public class Ligne {
    private String name;
    private String num;
    private String color;
    private String type;
    private Boolean show;
    private List<List<Float>> labels;
    private List<List<String>> arrets;
}
