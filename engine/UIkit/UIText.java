package engine.UIkit;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class UIText extends UIElement {

    private String text;
    private Font font;
    private Color color;
    private TextAlignment textAlignment;

    public UIText(double x, double y, String text) {
        super(x, y, 0, 0);
        this.text = text;
        this.font = new Font("Arial", 20);
        this.color = Color.BLACK;
        this.textAlignment = TextAlignment.CENTER;
    }

    @Override
    public void onDraw(GraphicsContext g) {
        g.setFont(font);
        g.setFill(color);
        g.setTextAlign(textAlignment);
        g.fillText(text, x, y);
    }

    public Font getFont() {return font;}

    public void setFont(Font font) {
        this.font = font;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setTextAlignment(TextAlignment alignment) {
        this.textAlignment = alignment;
    }

    public String getText(){return text;}

    public void setText(String text) {
        this.text = text;
    }

    // Method to change the alpha value of the text color
    public void setAlpha(double alpha) {
        if (alpha < 0.0) alpha = 0.0;  // Ensure alpha is between 0.0 and 1.0
        if (alpha > 1.0) alpha = 1.0;

        // Create a new color with the same RGB values but with the new alpha
        this.color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
}
