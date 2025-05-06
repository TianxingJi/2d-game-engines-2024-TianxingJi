package engine.UIkit;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public class UIPanel extends UIElement {
    private List<UIElement> elements = new ArrayList<>();
    private Color backgroundColor = Color.TRANSPARENT;  // default background color
    private static final double PADDING = 10.0;  // Default padding between elements
    private static double TEXT_OFFSET = 15.0;  // Additional x offset for UIText elements

    public UIPanel(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    // 设置背景颜色的方法
    public void setColor(Color color) {
        this.backgroundColor = color;
    }

    public void addElement(UIElement element) {
        elements.add(element);
    }

    public void addElementToFront(UIElement element) {
        elements.addFirst(element);  // 将元素插入到列表的第一个位置
    }

    public void removeElement(UIElement element) {
        elements.remove(element);
    }

    public boolean containsElement(UIElement element) {
        return elements.contains(element);
    }

    // Expose the list of elements in the panel
    public List<UIElement> getElements() {
        return elements;
    }

    @Override
    public void onDraw(GraphicsContext g) {
        // Draw the background
        g.setFill(backgroundColor);
        g.fillRect(x, y, width, height);
        TEXT_OFFSET = 0.32 * width;
        // Calculate positions for elements inside the panel
        double currentY = this.y + PADDING;  // Start just below the top with some padding

        // Draw each element with new calculated positions
        for (UIElement element : elements) {
            // Set the element's new position inside the panel
            double newElementX = this.x + PADDING;  // Start with some padding from the left
            double newElementY = currentY;

            // Ensure the element fits within the panel
            double elementWidth = this.width -  2 * PADDING;
            double elementHeight = (this.height -  2 * PADDING) / 10;

            // If the element is UIText, adjust its position.x further to the right
            if (element instanceof UIText) {
                newElementX += TEXT_OFFSET;  // Offset UIText further to the right
                elementHeight = TEXT_OFFSET / 3;
                elementWidth = TEXT_OFFSET / 2;
                element = (UIText) element;
                ((UIText) element).setFont(new Font("Arial", elementWidth));
            }

            // Update the element's position and size
            element.setPosition(newElementX, newElementY);
            element.setSize(elementWidth, elementHeight);

            // Draw the element at the new position
            element.onDraw(g);

            // Update the Y position for the next element
            currentY += elementHeight + PADDING;

            // Stop if there is no more space in the panel
            if (currentY + elementHeight + PADDING / 2 > this.y + this.height) {
                break;
            }
        }
    }
}