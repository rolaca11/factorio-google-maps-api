package io.ropi.gmaps.api.map;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import io.ropi.gmaps.api.parse.Position;

import org.junit.jupiter.api.Test;

class FactorioMapTest {

    private static final Map<Position, Chunk> CHUNKS = Map.ofEntries(
            Map.entry(new Position(0, 3), new Chunk()),
            Map.entry(new Position(-1, 2), new Chunk()),
            Map.entry(new Position(5, -2), new Chunk()),
            Map.entry(new Position(3, 1), new Chunk())
    );

    @Test
    void parse() {

    }

    @Test
    void getTopLeftMostPosition() {
        FactorioMap subject = new FactorioMap(CHUNKS, true);

        assertThat(subject.getTopLeftMostPosition())
                .satisfies(position -> assertThat(position.getX()).isEqualTo(-1))
                .satisfies(position -> assertThat(position.getY()).isEqualTo(-2));
    }

    @Test
    void getBottomRightMostPosition() {
        FactorioMap subject = new FactorioMap(CHUNKS, true);

        assertThat(subject.getBottomRightMostPosition())
                .satisfies(position -> assertThat(position.getX()).isEqualTo(5))
                .satisfies(position -> assertThat(position.getY()).isEqualTo(3));
    }

    @Test
    void getWidth() {
        FactorioMap subject = new FactorioMap(CHUNKS, true);

        assertThat(subject.getWidth()).isEqualTo(6);
    }

    @Test
    void getHeight() {
        FactorioMap subject = new FactorioMap(CHUNKS, true);

        assertThat(subject.getHeight()).isEqualTo(5);
    }
}
