package util;

import lombok.AllArgsConstructor;
import lombok.Data;
import network.dto.Station;

/**
 * Utility class to hold an ordered pair of Stations
 */
@Data
@AllArgsConstructor
public class StationPair{
    private Station first;
    private Station second;
}
