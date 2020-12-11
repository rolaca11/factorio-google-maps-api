package io.ropi.gmaps.api;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import io.ropi.gmaps.api.image.FactorioMapSupplier;
import io.ropi.gmaps.api.image.ImageChunk;
import io.ropi.gmaps.api.map.ChunkCoordinates;
import io.ropi.gmaps.api.map.FactorioMap;
import io.ropi.gmaps.api.parse.Position;

import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestEndpoint {
    private static final String IMAGE_PATH = "classpath:map/zoom%d/image_%d_%d.png";
    private static final int TILE_SIZE = 256;

    private final ResourceLoader resourceLoader;
    private final FactorioMapSupplier factorioMapSupplier;
    private final ImageChunk imageChunk;

    public RestEndpoint(ResourceLoader resourceLoader, FactorioMapSupplier factorioMapSupplier, ImageChunk imageChunk) {
        this.resourceLoader = resourceLoader;
        this.factorioMapSupplier = factorioMapSupplier;
        this.imageChunk = imageChunk;
    }

    static int roundUpToNextPowerOfTwo(int num) {
        int mask = 1;
        for(int i = 0; i < bitCount(num - 1); i++) {
            mask = mask << 1;
        }

        return mask;
    }

    static int bitCount(int num) {
        int count = 0;

        while(num != 0) {
            num = num >> 1;
            count++;
        }

        return count;
    }

    @GetMapping
    public ResponseEntity<byte[]> getMap(@RequestParam(value = "x", required = false) Integer lng,
                                         @RequestParam(value = "y", required = false) Integer lat,
                                         @RequestParam(value = "zoom", required = false) Integer zoom) throws IOException {
        FactorioMap factorioMap = factorioMapSupplier.get();

        int x = lng / zoom;
        int y = lat / zoom;

        int mapSize = roundUpToNextPowerOfTwo(Math.max(factorioMap.getHeight(), factorioMap.getWidth())); // how many chunks the map consists of
        int mapRowCount = (int) Math.pow(2, zoom); // how many pieces the map is put together from

        float chunkPixelSize = (float) mapRowCount * TILE_SIZE / mapSize; // the resolution of one chunk
        float chunkRowCount = (float)mapSize / mapRowCount; // how many chunks fit in a row of the resulting image

        ChunkCoordinates tileCoordinates = new ChunkCoordinates();

        Position tileTopLeft = factorioMap.getTopLeftMostPosition();
        float tileTopLeftY = tileTopLeft.getY() + (y * chunkRowCount);
        float tileTopLeftX = tileTopLeft.getX() + (x * chunkRowCount);

        tileCoordinates.setTopLeft(tileTopLeft);

        BufferedImage image = new BufferedImage(TILE_SIZE, TILE_SIZE, 2);

        Graphics graphics = image.getGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, TILE_SIZE, TILE_SIZE);

        factorioMap.getChunks().entrySet().parallelStream()
                .map(entry -> Map.entry(new FloatPosition(entry.getKey().getX() - tileTopLeftX, entry.getKey().getY() - tileTopLeftY),
                        entry.getValue()))
                .filter(entry -> entry.getKey().getX() >= -1)
                .filter(entry -> entry.getKey().getY() >= -1)
                .filter(entry -> entry.getKey().getX() <= chunkRowCount)
                .filter(entry -> entry.getKey().getY() <= chunkRowCount)
                .forEach(entry -> {
                    float chunkX = entry.getKey().getX();
                    float chunkY = entry.getKey().getY();

                    graphics.drawImage(
                            imageChunk.imageChunk(entry.getValue(), chunkPixelSize, chunkPixelSize),
                            Math.round(chunkX * chunkPixelSize),
                            Math.round(chunkY * chunkPixelSize),
                            Math.round(chunkPixelSize),
                            Math.round(chunkPixelSize),
                            null
                    );
                });

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);

        return ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .body(outputStream.toByteArray());
    }

    @GetMapping("/size")
    public ResponseEntity<Map<String, Integer>> size() {
        return ResponseEntity.ok()
                .body(Map.of("width", 1000, "height", 1000));
    }
}
