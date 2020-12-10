package io.ropi.gmaps.api;

public class Rectangle {
    private Float x;
    private Float y;
    private Float height;
    private Float width;

    public Rectangle() {
    }

    public Rectangle(Float x, Float y, Float height, Float width) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }

    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public Float getWidth() {
        return width;
    }

    public void setWidth(Float width) {
        this.width = width;
    }
}
