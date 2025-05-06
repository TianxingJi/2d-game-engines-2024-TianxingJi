package engine.systems;

import engine.UIkit.ViewPort;
import engine.gameobjects.*;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;

import java.util.Comparator;

public class RenderSystem extends System {

    private double resizeScaleX = 1.0;  // Scale factor for resize (initially 1)
    private double resizeScaleY = 1.0;  // Scale factor for resize (initially 1)
    private double resizeScaleAverage;

    public RenderSystem() {
    }

    @Override
    public void onTick(long nanosSincePreviousTick) {
        // No need to update anything in RenderSystem
    }

    @Override
    public void onDraw(GraphicsContext g) {
        // Sort gameObjects by their z-index before drawing
        gameObjects.sort(Comparator.comparingInt(GameObject::getZIndex));

        // Now draw the game objects in order
        for (GameObject gameObject : gameObjects) {
            gameObject.onDraw(g);  // Call the draw method of each GameObject
        }

    }

    // Handle window resize and adjust scale for rendering
    public void onResize(Vec2d newSize, Vec2d originalSize) {
        // Compute scale factors for resizing, based on new size compared to original size
        resizeScaleX = newSize.x / originalSize.x;
        resizeScaleY = newSize.y / originalSize.y;
        resizeScaleAverage = (resizeScaleX + resizeScaleY) / 2;

        // Adjust the position of each game object according to the resize scale, but keep the original size
        for (GameObject gameObject : gameObjects) {
            TransformComponent transform = gameObject.getComponent(TransformComponent.class);
            if (transform != null) {
                // Only adjust the position, not the size
                Vec2d currentPos = transform.getPosition();
                Vec2d currentSize = transform.getSize();
                Vec2d newGameObjectPos = new Vec2d(currentPos.x * resizeScaleAverage, currentPos.y * resizeScaleAverage);
                Vec2d newGameObjectSize = new Vec2d(currentSize.x * resizeScaleAverage, currentSize.y * resizeScaleAverage);
                transform.setPosition(newGameObjectPos);
                transform.setSize(newGameObjectSize);
                // Update collider position and size/radius if the game object has a collider
                CollisionComponent collider = gameObject.getComponent(CollisionComponent.class);
                if (collider != null) {
                    collider.syncWithTransform(transform, resizeScaleAverage, resizeScaleAverage);  // Sync collider position and size
                }
            }
        }
    }



    // Clear all the game objects
    public void clearGameObjects() {
        gameObjects.clear();
    }

}
