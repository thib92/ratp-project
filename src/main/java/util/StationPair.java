package util;

import lombok.AllArgsConstructor;
import lombok.Data;
import network.dto.Station;

@Data
@AllArgsConstructor
public class StationPair{
    private Station first;
    private Station second;
}
