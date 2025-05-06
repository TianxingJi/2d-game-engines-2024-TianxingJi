package engine.gameobjects;

import engine.support.Vec2d;
import engine.UIkit.ViewPort;  // Assuming there's a ViewPort class in your engine
import javafx.scene.canvas.GraphicsContext;

public class CenterViewComponent implements Component {

    private TransformComponent target;  // The target to follow (knight)
    private ViewPort viewPort;  // Reference to the viewport

    // Constructor to bind the component to a specific target and the viewport
    public CenterViewComponent(TransformComponent target, ViewPort viewPort) {
        this.target = target;
        this.viewPort = viewPort;  // Sync the viewport in the constructor
    }

    // Called every game tick to update the viewport's position
    @Override
    public void onTick(long nanosSincePreviousTick) {
        Vec2d targetPosition = target.getPosition();  // Get the knight's position
        Vec2d viewPortSize = viewPort.getSize();  // Get the viewport size dynamically

        // Adjust for zoom: The size of the viewport changes according to the zoom level
        Vec2d adjustedViewPortSize = viewPortSize.smult(1.0 / viewPort.getZoom());

        // Subtract half the size of the character to correctly center the knight in the viewport
        Vec2d characterOffset = target.getSize().smult(0.5);

        // Calculate the new position to center the knight in the viewport considering the zoom level and offset
        Vec2d newViewPos = targetPosition.minus(adjustedViewPortSize.smult(0.5)).plus(characterOffset);

        // Update the viewport's position to center on the knight
        viewPort.setPosition(newViewPos);
    }

    @Override
    public void onDraw(GraphicsContext g) {
        // Nothing to draw for this component
    }
}
