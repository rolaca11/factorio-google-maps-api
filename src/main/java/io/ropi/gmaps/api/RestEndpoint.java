package io.ropi.gmaps.api;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
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

    public RestEndpoint(ResourceLoader resourceLoader, FactorioMapSupplier factorioMapSupplier) {
        this.resourceLoader = resourceLoader;
        this.factorioMapSupplier = factorioMapSupplier;
    }

    @GetMapping
    public ResponseEntity<byte[]> getMap(@RequestParam(value = "x", required = false) Integer lng,
                                         @RequestParam(value = "y", required = false) Integer lat,
                                         @RequestParam(value = "zoom", required = false) Integer zoom) throws IOException {
        FactorioMap factorioMap = factorioMapSupplier.get();

        int x = lng / zoom;
        int y = lat / zoom;

        int mapSize = Math.max(factorioMap.getHeight(), factorioMap.getWidth()); // how many chunks the map consists of
        int mapRowCount = (int) Math.pow(2, zoom); // how many pieces the map is put together from

        int chunkPixelSize = (int)Math.floor((float)mapRowCount * TILE_SIZE / mapSize); // the resolution of one chunk
        int chunkRowCount = mapSize / mapRowCount; // how many chunks fit in a row of the resulting image

        ChunkCoordinates tileCoordinates = new ChunkCoordinates();

        Position tileTopLeft = factorioMap.getTopLeftMostPosition();
        tileTopLeft.setY(tileTopLeft.getY() + (y * chunkRowCount));
        tileTopLeft.setX(tileTopLeft.getX() + (x * chunkRowCount));

        tileCoordinates.setTopLeft(tileTopLeft);
        Position tileBottomRight = new Position(tileTopLeft.getX() + chunkRowCount, tileTopLeft.getY() + chunkRowCount);
        tileCoordinates.setBottomRight(tileBottomRight);

        BufferedImage image = new BufferedImage(TILE_SIZE, TILE_SIZE, 2);

        factorioMap.getChunks().entrySet().parallelStream()
                .filter(entry -> entry.getKey().getX() >= tileTopLeft.getX())
                .filter(entry -> entry.getKey().getY() >= tileTopLeft.getY())
                .filter(entry -> entry.getKey().getY() <= tileBottomRight.getY() + chunkRowCount)
                .filter(entry -> entry.getKey().getX() <= tileBottomRight.getX() + chunkRowCount)
                .forEach(entry -> {
                    Graphics graphics = image.getGraphics();
                    int chunkX = entry.getKey().getX() - tileTopLeft.getX();
                    int chunkY = entry.getKey().getY() - tileTopLeft.getY();

                    graphics.setColor(Color.ORANGE);
                    graphics.fillRect(chunkX * chunkPixelSize,
                            chunkY * chunkPixelSize,
                            chunkPixelSize,
                            chunkPixelSize);

//                    graphics.drawImage(
//                            ImageChunk.imageChunk(entry.getValue(), chunkPixelSize, chunkPixelSize),
//                            chunkX * chunkPixelSize,
//                            chunkY * chunkPixelSize,
//                            chunkPixelSize,
//                            chunkPixelSize,
//                            null
//                    );
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
