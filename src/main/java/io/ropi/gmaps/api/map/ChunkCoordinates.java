package io.ropi.gmaps.api.map;

import io.ropi.gmaps.api.parse.Position;

import lombok.Data;

@Data
public class ChunkCoordinates {
    private Position position;
    private Position topLeft;
    private Position bottomRight;
}
