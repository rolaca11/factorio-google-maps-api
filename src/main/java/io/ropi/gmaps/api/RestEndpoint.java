package io.ropi.gmaps.api;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestEndpoint {

    @GetMapping
    public ResponseEntity<byte[]> getMap(@RequestParam(value = "x", required = false) Integer lat,
                                         @RequestParam(value = "y", required = false) Integer lng,
                                         @RequestParam(value = "zoom", required = false) Integer zoom) throws IOException {
        String from = String.format("{%d, %d}", lat, lng);

        BufferedImage image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);

        Font font = new Font("Arial Black", Font.PLAIN, 30);

        Graphics graphics = image.getGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, 256, 256);
        graphics.setColor(Color.LIGHT_GRAY);
        graphics.fillRect(1, 1, 254, 254);
        graphics.setColor(Color.BLACK);
        graphics.drawLine(0, 0, 255, 255);
        graphics.drawLine(255, 0, 0, 255);
        graphics.setColor(Color.LIGHT_GRAY);

        FontMetrics fontMetrics = image.getGraphics().getFontMetrics();
        graphics.setFont(font);
        int xPos = 128 - (int) (fontMetrics.stringWidth(from) / 2.0f);

        graphics.fillRect(xPos, (int) (128 - (fontMetrics.getHeight() / 2.0f)), 2 * (128 - xPos), fontMetrics.getHeight());

        graphics.setColor(Color.BLACK);

        graphics.drawString(from, xPos, 128 + fontMetrics.getHeight() / 4);

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
