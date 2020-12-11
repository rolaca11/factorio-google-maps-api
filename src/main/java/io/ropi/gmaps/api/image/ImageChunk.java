package io.ropi.gmaps.api.image;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.ropi.gmaps.api.map.Chunk;
import io.ropi.gmaps.api.parse.Position;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ImageChunk {

    private final ResourceLoader resourceLoader;
    private final Map<String, Image> imageCache = new ConcurrentHashMap<>();

    public ImageChunk(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public BufferedImage imageChunk(Chunk chunk, float width, float height) {
        BufferedImage bufferedImage = new BufferedImage(Math.round(width), Math.round(height), BufferedImage.TYPE_INT_ARGB);
        int chunkSize = (int)chunk.getTiles().keySet().parallelStream().map(Position::getX).distinct().count();
        float tileSize = (float)width / chunkSize;

        chunk.getTiles().entrySet().parallelStream()
                .map(entry -> Map.entry(entry.getKey(), getColorOfTile(entry.getValue())))
                .forEach(entry -> {
                    Graphics graphics = bufferedImage.getGraphics();
                    int x = Math.round(entry.getKey().getX() * tileSize);
                    int y = Math.round(entry.getKey().getY() * tileSize);
                    int size = (int) Math.ceil(tileSize);

                    graphics.drawImage(entry.getValue(), x, y, size, size, null);


                });

        if(chunk.getVisibleForces().isEmpty()) {
            Graphics graphics = bufferedImage.getGraphics();
            graphics.setColor(new Color(0, 0, 0, 0.5f));
            graphics.fillRect(0, 0, (int)width, (int)height);
        }

        return bufferedImage;
    }

    @NonNull
    public Image getColorOfTile(String name) {
        if(!imageCache.containsKey(name)) {
            synchronized (imageCache) {
                if (!imageCache.containsKey(name)) {
                    Resource resource = resourceLoader.getResource("classpath:assets/terrain/" + name + ".png");

                    Image image;
                    try {
                        image = ImageIO.read(resource.getInputStream()).getSubimage(0, 0, 32, 32);
                    } catch (IOException e) {
                        log.error("Could not find asset with name: {}", name);
                        image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
                    }

                    imageCache.put(name, image);
                    return image;
                } else {
                    return imageCache.get(name);
                }
            }
        }

        return imageCache.get(name);
    }
}
