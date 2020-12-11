package io.ropi.gmaps.api.parse;

import java.util.Map;

import lombok.Data;

@Data
public class Chunk {
    private ChunkCoordinates chunk;
    private Map<String, Boolean> charted;
    private Map<String, Boolean> visible;
    private Map<Integer, Map<Integer, String>> tiles;
}
