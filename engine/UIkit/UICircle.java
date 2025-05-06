package engine.UIkit;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class UICircle extends UIElement {
    public UICircle(double x, double y, double radius) {
        super(x, y, radius * 2, radius * 2);  // Treat radius as width and height
    }

    @Override
    public void onDraw(GraphicsContext g) {
        // Set fill color based on hover or press state
        if (pressed) {
            g.setFill(Color.DARKGRAY);
        } else if (hovered) {
            g.setFill(Color.LIGHTGRAY);
        } else {
            g.setFill(Color.GRAY);
        }

        // Draw the circle
        g.fillOval(x, y, width, height);

        // Draw the border
        g.setStroke(Color.BLACK);
        g.setLineWidth(2);
        g.strokeOval(x, y, width, height);
    }

    @Override
    public boolean handleClicked(double mouseX, double mouseY) {
        // Special case for circles: check if the click is within the circle's radius
        double centerX = x + width / 2;
        double centerY = y + height / 2;
        double distance = Math.sqrt(Math.pow(mouseX - centerX, 2) + Math.pow(mouseY - centerY, 2));
        return distance <= width / 2;
    }
}
