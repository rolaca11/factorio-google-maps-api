package io.ropi.gmaps.api.image;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.ropi.gmaps.api.map.Chunk;
import io.ropi.gmaps.api.map.ChunkCoordinates;
import io.ropi.gmaps.api.parse.Position;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.ResourceLoader;

class ImageChunkTest {

    private static final List<String> TILE_TYPES = List.of("concrete",
            "refined-concrete",
            "refined-hazard-concrete-left",
            "refined-hazard-concrete-right",
            "deepwater",
            "water",
            "dirt-1",
            "dirt-2",
            "dirt-3",
            "dirt-4",
            "dirt-5",
            "dirt-6",
            "dirt-7",
            "dry-dirt",
            "grass-1",
            "grass-2",
            "grass-3",
            "grass-4",
            "landfill",
            "red-desert-0",
            "red-desert-1",
            "red-desert-2",
            "red-desert-3",
            "sand-1",
            "sand-2",
            "sand-3");

    @Test
    void imageChunk() throws IOException {
        Chunk chunk = Chunk.builder().build();
        ChunkCoordinates coordinates = new ChunkCoordinates();
        chunk.setCoordinates(coordinates);

        coordinates.setTopLeft(new Position(-200, 400));
        coordinates.setBottomRight(new Position(-196, 404));

        HashMap<Position, String> tiles = new HashMap<>();

        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                tiles.put(new Position(i, j), getRandomTile());
            }
        }

//        chunk.setTiles(tiles);

        ResourceLoader resourceLoader = mock(ResourceLoader.class);
        when(resourceLoader.getResource(any())).thenReturn(new FileUrlResource(""));
        ImageChunk imageChunk = new ImageChunk(resourceLoader);
        ImageIO.write(imageChunk.imageChunk(chunk, 256, 256), "png", new File("test.png"));
    }

    private String getRandomTile() {
        return TILE_TYPES.get(new Random().nextInt(TILE_TYPES.size()));
    }
}
