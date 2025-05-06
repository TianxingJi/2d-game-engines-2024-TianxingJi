package engine.UIkit;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class UIButton extends UIElement {
    private String text;
    private Font font = new Font("Arial", 20);  // Default font
    private TextAlignment textAlignment = TextAlignment.CENTER;  // Default text alignment
    private Color color = Color.GRAY;  // Default button color
    private Color textColor = Color.BLACK; // Default text color
    private double alpha = 1.0;  // Default alpha for interaction effects

    public UIButton(double x, double y, double width, double height, String text) {
        super(x, y, width, height);
        this.text = text;
    }

    @Override
    public void onDraw(GraphicsContext g) {
        // Modify alpha based on button state (pressed or hovered)
        double currentAlpha = 1.0;
        if (pressed) {
            currentAlpha = 0.5;  // Reduce alpha when pressed
        } else if (hovered) {
            currentAlpha = 0.85;  // Slightly reduce alpha when hovered
        }

        // Set the fill color with modified alpha
        Color fillColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), currentAlpha);
        g.setFill(fillColor);
        g.fillRect(x, y, width, height);

        // Draw button border
        g.setStroke(Color.BLACK);
        g.setLineWidth(2);
        g.strokeRect(x, y, width, height);

        // Draw button text with the configured font and alignment
        g.setFill(textColor);  // Use the text color here
        g.setFont(font);
        g.setTextAlign(textAlignment);

        // Adjust text position based on alignment
        double textX = x + width / 2;
        double textY = y + height / 2 + font.getSize() / 3;  // Center the text vertically

        g.fillText(text, textX, textY);
    }

    public Font getFont() {
        return font;
    }

    // Set the font for the button text
    public void setFont(Font font) {
        this.font = font;
    }

    // Set the text alignment (CENTER, LEFT, RIGHT)
    public void setTextAlignment(TextAlignment alignment) {
        this.textAlignment = alignment;
    }

    // Set the color for the button
    public void setColor(Color color) {
        this.color = color;
    }

    // Set the button text
    public void setText(String text) {
        this.text = text;
    }

    // Set the color for the button text
    public void setTextColor(Color color) {
        this.textColor = color;
    }
}
