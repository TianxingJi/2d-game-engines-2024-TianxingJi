package engine.gameobjects;

import javafx.scene.canvas.GraphicsContext;
import engine.support.Vec2d;

public class TransformComponent implements Component {
    private Vec2d originalPosition;
    private Vec2d originalSize;
    private Vec2d position;  // Position of the game object
    private Vec2d size;      // Size of the game object

    // Constructor to initialize position and size
    public TransformComponent(Vec2d position, Vec2d size) {
        this.position = position;
        this.originalPosition = position;
        this.originalSize = size;
        this.size = size;
    }

    // Gets the position of the object
    public Vec2d getPosition() {
        return position;
    }

    // Sets the position of the object
    public void setPosition(Vec2d position) {
        this.position = position;
    }

    public Vec2d getOriginalPosition() {
        return originalPosition;
    }

    public Vec2d getOriginalSize() {
        return originalSize;
    }

    // Gets the size of the object
    public Vec2d getSize() {
        return size;
    }

    // Sets the size of the object
    public void setSize(Vec2d size) {
        this.size = size;
    }

    // Add the contains method to determine if a point is within the scope of the TransformComponent
    public boolean contains(double mouseX, double mouseY) {
        return mouseX >= position.x && mouseX <= position.x + size.x &&
                mouseY >= position.y && mouseY <= position.y + size.y;
    }

    // No need to update anything in onTick for the transform
    @Override
    public void onTick(long nanosSincePreviousTick) {}

    // No need to render anything for the transform itself
    @Override
    public void onDraw(GraphicsContext g) {}
}