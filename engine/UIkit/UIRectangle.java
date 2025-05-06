package engine.UIkit;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class UIRectangle extends UIElement {
    private Color fillColor;   // Color to fill the rectangle
    private Color strokeColor; // Color for the rectangle's border
    private double strokeWidth; // Width of the border

    // Constructor for rectangle with no border
    public UIRectangle(double x, double y, double width, double height, Color fillColor) {
        super(x, y, width, height);
        this.fillColor = fillColor;
        this.strokeColor = null; // No border by default
        this.strokeWidth = 0;
    }

    // Constructor for rectangle with a border
    public UIRectangle(double x, double y, double width, double height, Color fillColor, Color strokeColor, double strokeWidth) {
        super(x, y, width, height);
        this.fillColor = fillColor;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
    }

    public UIRectangle(double x, double y, double width, double height) {
        super(x, y, width, height);  // Call UIElement constructor
    }

    // Overriding the onDraw method to draw a rectangle
    @Override
    public void onDraw(GraphicsContext g) {
        // Set the fill color and draw the filled rectangle
        g.setFill(fillColor);
        g.fillRect(x, y, width, height);

        // If strokeColor is set, draw the border
        if (strokeColor != null) {
            g.setStroke(strokeColor);
            g.setLineWidth(strokeWidth);
            g.strokeRect(x, y, width, height);
        }
    }

    // Optional: Setters for modifying the appearance dynamically
    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

    public void setStrokeWidth(double strokeWidth) {
        this.strokeWidth = strokeWidth;
    }
}
