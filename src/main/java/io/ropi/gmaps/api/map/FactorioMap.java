package io.ropi.gmaps.api.map;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.ropi.gmaps.api.parse.Position;

public class FactorioMap {
    private final Map<Position, Chunk> chunks;

    public FactorioMap(List<io.ropi.gmaps.api.parse.Chunk> chunks) {
        this.chunks = chunks.parallelStream()
                .map(chunk -> {
                    Chunk result = new Chunk();
                    result.setChartedForces(chunk.getCharted().entrySet().stream()
                            .filter(Map.Entry::getValue)
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toList()));
                    result.setVisibleForces(chunk.getVisible().entrySet().stream()
                            .filter(Map.Entry::getValue)
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toList()));

                    ChunkCoordinates coordinates = new ChunkCoordinates();
                    coordinates.setPosition(new Position(chunk.getChunk().getX(), chunk.getChunk().getY()));
                    coordinates.setTopLeft(chunk.getChunk().getArea().getLeft_top());
                    coordinates.setBottomRight(chunk.getChunk().getArea().getRight_bottom());

                    result.setTiles(chunk.getTiles().entrySet().parallelStream()
                            .flatMap(xEntry -> xEntry.getValue().entrySet().parallelStream()
                                    .map(yEntry -> Map.entry(new Position(
                                                    xEntry.getKey() - coordinates.getTopLeft().getX(),
                                                    yEntry.getKey() - coordinates.getTopLeft().getY()),
                                            yEntry.getValue())))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

                    result.setCoordinates(coordinates);

                    return result;
                })
                .map(chunk -> {
                    Position key = new Position();
                    key.setX(chunk.getCoordinates().getPosition().getX());
                    key.setY(chunk.getCoordinates().getPosition().getY());

                    return Map.entry(key, chunk);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    FactorioMap(Map<Position, Chunk> chunks, boolean test) {
        this.chunks = chunks;
    }

    public Position getTopLeftMostPosition() {
        return getAnyMostPosition(Comparator.naturalOrder());
    }

    private Position getAnyMostPosition(Comparator<Integer> comparator) {
        return chunks.keySet().parallelStream()
                .reduce((left, right) -> {
                    Position result = new Position(left.getX(), left.getY());
                    if (comparator.compare(left.getX(), right.getX()) > 0) {
                        result.setX(right.getX());
                    }
                    if (comparator.compare(left.getY(), right.getY()) > 0) {
                        result.setY(right.getY());
                    }
                    return result;
                }).orElse(null);
    }

    public Position getBottomRightMostPosition() {
        return getAnyMostPosition(Comparator.<Integer>naturalOrder().reversed());
    }

    public Integer getWidth() {
        return getBottomRightMostPosition().getX() - getTopLeftMostPosition().getX();
    }

    public Integer getHeight() {
        return getBottomRightMostPosition().getY() - getTopLeftMostPosition().getY();
    }

    public Map<Position, Chunk> getChunks() {
        return chunks;
    }
}
