package io.ropi.gmaps.api.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Map;

import io.ropi.gmaps.api.map.Chunk;
import io.ropi.gmaps.api.parse.Position;

import org.springframework.lang.NonNull;

public class ImageChunk {

    public static BufferedImage imageChunk(Chunk chunk, float width, float height) {
        BufferedImage bufferedImage = new BufferedImage(Math.round(width), Math.round(height), BufferedImage.TYPE_INT_ARGB);
        int chunkSize = (int)chunk.getTiles().keySet().parallelStream().map(Position::getX).distinct().count();
        float tileSize = (float)width / chunkSize;

        chunk.getTiles().entrySet().parallelStream()
                .map(entry -> Map.entry(entry.getKey(), getColorOfTile(entry.getValue())))
                .forEach(entry -> {
                    Graphics graphics = bufferedImage.getGraphics();
                    graphics.setColor(entry.getValue());
                    graphics.fillRect(Math.round(entry.getKey().getX() * tileSize),
                            Math.round(entry.getKey().getY() * tileSize), (int)Math.ceil(tileSize), (int)Math.ceil(tileSize));

//                    if(chunk.getVisibleForces().isEmpty()) {
//                        graphics.setColor(new Color(0, 0, 0, 0.2f));
//                        graphics.fillRect(0, 0, Math.round(width), Math.round(height));
//                    }
                });

        return bufferedImage;
    }

    @NonNull
    public static Color getColorOfTile(String name) {
        switch (name) {
            case "concrete":
            case "refined-concrete":
            case "refined-hazard-concrete-left":
            case "refined-hazard-concrete-right":
                return Color.GRAY;
            case "deepwater":
            case "water":
                return Color.BLUE;
            case "dirt-1":
            case "dirt-2":
            case "dirt-3":
            case "dirt-4":
            case "dirt-5":
            case "dirt-6":
            case "dirt-7":
            case "dry-dirt":
                return Color.getHSBColor(0.1f, 1.0f, 0.5f);
            case "grass-1":
            case "grass-2":
            case "grass-3":
            case "grass-4":
                return Color.GREEN;
            case "landfill":
                return Color.BLACK;
            case "red-desert-0":
            case "red-desert-1":
            case "red-desert-2":
            case "red-desert-3":
                return Color.RED;
            case "sand-1":
            case "sand-2":
            case "sand-3":
                return Color.ORANGE;
        }

        return Color.PINK;
    }
}
