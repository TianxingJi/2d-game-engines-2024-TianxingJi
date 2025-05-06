package engine.gameobjects;

import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class SpriteComponent implements Component {
    protected Image sprite;  // The image representing the game object
    protected TransformComponent transform;  // The associated transform component

    // Constructor to initialize the sprite and transform
    public SpriteComponent(Image sprite, TransformComponent transform) {
        this.sprite = sprite;
        this.transform = transform;
    }

    public SpriteComponent(String imagePath, TransformComponent transform) {
        this.sprite = new Image(getClass().getResource(imagePath).toString());
        this.transform = transform;
    }

    // No logic to update in onTick for the sprite
    @Override
    public void onTick(long nanosSincePreviousTick) {}

    // Draws the sprite at the position and size specified by the transform component
    @Override
    public void onDraw(GraphicsContext g) {
        Vec2d position = transform.getPosition();
        Vec2d size = transform.getSize();
        g.drawImage(sprite, position.x, position.y, size.x, size.y);
    }
}
