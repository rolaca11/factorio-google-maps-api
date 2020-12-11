package io.ropi.gmaps.api.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.Map;

import io.ropi.gmaps.api.map.Chunk;
import io.ropi.gmaps.api.parse.Position;

import org.springframework.lang.NonNull;

public class ImageChunk {

    public static BufferedImage imageChunk(Chunk chunk, int width, int height) {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int chunkSize = (int)chunk.getTiles().keySet().parallelStream().map(Position::getX).distinct().count();
        int tileSize = width / chunkSize;

        Graphics graphics1 = bufferedImage.getGraphics();
        graphics1.setColor(Color.LIGHT_GRAY);
        graphics1.fillRect(0, 0, width, height);

        chunk.getTiles().entrySet().parallelStream()
                .map(entry -> Map.entry(entry.getKey(), getColorOfTile(entry.getValue())))
                .forEach(entry -> {
                    Graphics graphics = bufferedImage.getGraphics();
                    graphics.setColor(entry.getValue());
                    graphics.fillRect(entry.getKey().getX() * tileSize, entry.getKey().getY() * tileSize, tileSize, tileSize);
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