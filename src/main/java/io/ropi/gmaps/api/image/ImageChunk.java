package io.ropi.gmaps.api.image;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.ropi.gmaps.api.map.Chunk;
import io.ropi.gmaps.api.parse.Position;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ImageChunk {

    private final ResourceLoader resourceLoader;
    private final Map<CacheKey, Image> imageCache = new ConcurrentHashMap<>();

    public ImageChunk(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public BufferedImage imageChunk(Chunk chunk, float width, float height) {
        BufferedImage bufferedImage = new BufferedImage(Math.round(width), Math.round(height), BufferedImage.TYPE_INT_ARGB);

        int chunkSize = (int)chunk.getTiles().keySet().parallelStream().map(Position::getX).distinct().count();
        float tileSize = (float)width / chunkSize;
        int size = (int) Math.ceil(tileSize);

        Graphics graphics = bufferedImage.getGraphics();
        chunk.getTiles().entrySet().parallelStream()
                .map(entry -> Map.entry(entry.getKey(), getColorOfTile(CacheKey.builder().name(entry.getValue()).size(size).build())))
                .forEach(entry -> {
                    int x = Math.round(entry.getKey().getX() * tileSize);
                    int y = Math.round(entry.getKey().getY() * tileSize);

                    graphics.drawImage(entry.getValue(), x, y, size, size, null);
                });

        if(chunk.getVisibleForces().isEmpty()) {
            graphics.setColor(new Color(0, 0, 0, 0.5f));
            graphics.fillRect(0, 0, (int)width, (int)height);
        }

        return bufferedImage;
    }

    @NonNull
    public Image getColorOfTile(CacheKey key) {
        if(!imageCache.containsKey(key)) {
            synchronized (imageCache) {
                if (!imageCache.containsKey(key)) {
                    Resource resource = resourceLoader.getResource("classpath:assets/terrain/" + key.getName() + ".png");

                    Image image;
                    try {
                        image = ImageIO.read(resource.getInputStream()).getSubimage(0, 0, 32, 32)
                                .getScaledInstance(key.getSize(), key.getSize(), Image.SCALE_FAST);
                    } catch (IOException e) {
                        log.error("Could not find asset with name: {}", key.getName());
                        image = new BufferedImage(key.getSize(), key.getSize(), BufferedImage.TYPE_INT_RGB);
                    }

                    imageCache.put(key, image);
                    return image;
                } else {
                    return imageCache.get(key);
                }
            }
        }

        return imageCache.get(key);
    }

    @Data
    @Builder
    static class CacheKey {
        private String name;
        private int size;
    }
}
