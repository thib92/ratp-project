package network;

import com.fasterxml.jackson.databind.ObjectMapper;
import network.dto.Network;
import network.dto.Station;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class JsonNetworkParser {
    public Network parseNetwork(String jsonFilePath) throws IOException {
        InputStream fileStream = new FileInputStream(jsonFilePath);
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(fileStream, Network.class);
    }
}
