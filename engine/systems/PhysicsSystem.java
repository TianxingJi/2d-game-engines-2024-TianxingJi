package engine.systems;

import engine.gameobjects.*;
import javafx.scene.canvas.GraphicsContext;

public class PhysicsSystem extends System {

    @Override
    public void onTick(long nanosSincePreviousTick) {
        // Update each GameObject's physics
        for (GameObject gameObject : gameObjects) {
            // Update physics logic (e.g., velocity, gravity)
            gameObject.onTick(nanosSincePreviousTick);
        }
    }

    @Override
    public void onDraw(GraphicsContext g) {
        // No need to render anything in the PhysicsSystem
    }

    // Clear all the game objects
    public void clearGameObjects() {
        gameObjects.clear();
    }
}