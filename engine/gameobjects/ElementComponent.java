package engine.gameobjects;

import javafx.scene.canvas.GraphicsContext;

public class ElementComponent implements Component {
    private Element element;

    // Constructor to initialize the element type
    public ElementComponent(Element element) {
        this.element = element;
    }

    // Getter for the element
    public Element getElement() {
        return element;
    }

    // Setter for the element
    public void setElement(Element element) {
        this.element = element;
    }

    @Override
    public void onTick(long nanosSincePreviousTick) {
        // No need to update anything for this component in onTick
    }

    @Override
    public void onDraw(GraphicsContext g) {
        // This component does not need to draw anything
    }
}