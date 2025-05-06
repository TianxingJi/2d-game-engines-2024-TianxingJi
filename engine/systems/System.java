package engine.systems;

import engine.gameobjects.*;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;

public abstract class System {
    protected List<GameObject> gameObjects = new ArrayList<>();

    // Add a GameObject to this system
    public void addGameObject(GameObject obj) {
        gameObjects.add(obj);
    }

    // Remove a GameObject from this system
    public void removeGameObject(GameObject obj) {
        gameObjects.remove(obj);
    }

    // get all the game objects in the system
    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    // Abstract methods for updating and drawing objects
    public abstract void onTick(long nanosSincePreviousTick);
    public abstract void onDraw(GraphicsContext g);
    // Clear all the game objects
    public void clearGameObjects() {}
}