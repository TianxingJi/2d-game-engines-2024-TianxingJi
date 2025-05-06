package engine.UIkit;

import engine.support.Vec2d;

public class ViewPort {
    private Vec2d position;  // The top-left corner of the ViewPort
    private Vec2d size;      // The size of the ViewPort
    private double zoom;     // The zoom level (1.0 means no zoom, 2.0 means zoomed in 2x)
    private double panLevel;

    public ViewPort(Vec2d position, Vec2d size) {
        this.position = position;
        this.size = size;
        this.zoom = 2.0;  // Default zoom level
        this.panLevel = 1.0; // Default pan level
    }

    // Getter and Setter for position
    public Vec2d getPosition() {
        return position;
    }

    public void setPosition(Vec2d position) {
        this.position = position;
    }

    // Getter and Setter for size
    public Vec2d getSize() {
        return size;
    }

    public void setSize(Vec2d size) {
        this.size = size;
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    public double getPanLevel() {
        return panLevel;
    }

    public void setPanLevel(double pan) {
        this.panLevel = pan;
    }

    public void pan(Vec2d delta) {
        this.position = new Vec2d(position.x + delta.x * panLevel, position.y + delta.y * panLevel);
    }

    public void adjustZoom(double deltaZoom) {
        zoom += deltaZoom;  // Adjust zoom level
        if (zoom < 1.0) zoom = 1.0;  // Minimum zoom level
        if (zoom > 4.0) zoom = 4.0;  // Maximum zoom level
    }
}