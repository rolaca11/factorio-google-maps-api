package io.ropi.gmaps.api.map;

import java.util.List;
import java.util.Map;

import io.ropi.gmaps.api.parse.Position;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Chunk {
    private ChunkCoordinates coordinates;
    private List<String> chartedForces;
    private List<String> visibleForces;
    private Map<Position, Tile> tiles;
}
