package engine.UIkit;

import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;

public abstract class UIElement {
    protected double x, y, width, height;
    protected boolean clicked;
    protected boolean hovered;
    protected boolean pressed;

    public UIElement(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Abstract method for drawing UI elements
    public abstract void onDraw(GraphicsContext g);

    // Common functionality for mouse handling
    public boolean handleClicked(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public void handleHover(double mouseX, double mouseY) {
        hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public void handlePressed(double mouseX, double mouseY) {
        pressed = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public void handleReleased(double mouseX, double mouseY) {
        pressed = false;
    }

    public Vec2d getPosition() {
        return new Vec2d(x, y);
    }

    // Setters for position and size
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }



}