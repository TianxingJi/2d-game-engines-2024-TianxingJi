package engine.systems;

import engine.gameobjects.GameObject;
import engine.gameobjects.CollisionComponent;
import engine.gameobjects.RayColliderComponent;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;

public class CollisionSystem extends System {

    private List<GameObject> collisionObjects;

    // This is the method the game will override to handle collisions
    protected void handleCollisionResult(GameObject objA, GameObject objB) {
        // Empty in the engine, to be implemented by the game code
    }

    public CollisionSystem() {
        this.collisionObjects = new ArrayList<>();
    }

    public void addGameObject(GameObject gameObject) {
        collisionObjects.add(gameObject);
    }

    public void removeGameObject(GameObject gameObject) {
        collisionObjects.remove(gameObject);
    }

    @Override
    public void onTick(long nanosSincePreviousTick) {
        handleCollisions();
    }

    protected void onLateTick() {}

    @Override
    public void onDraw(GraphicsContext g) {

    }

    private void handleCollisions() {
        for (int i = 0; i < collisionObjects.size(); i++) {
            GameObject objA = collisionObjects.get(i);
            CollisionComponent colliderA = objA.getComponent(CollisionComponent.class);

            if (colliderA == null || !colliderA.isActive()) {
                continue;  // Skip if collider is inactive
            }

            for (int j = i + 1; j < collisionObjects.size(); j++) {
                GameObject objB = collisionObjects.get(j);
                CollisionComponent colliderB = objB.getComponent(CollisionComponent.class);

                if (colliderB == null || !colliderB.isActive()) {
                    continue;  // Skip if collider is inactive
                }

                if (colliderA.collidesWith(colliderB)) {
                    // Handle collision logic
                    handleCollisionResult(objA, objB);
                }
            }
        }
    }

    public GameObject rayHitClosestObject(RayColliderComponent colliderA) {
        GameObject closestObject = null;
        double closestT = Double.MAX_VALUE;

        for (GameObject obj : collisionObjects) {
            CollisionComponent colliderB = obj.getComponent(CollisionComponent.class);
            if (colliderB != null && colliderA.collidesWith(colliderB)) {
                double currentT = colliderA.getClosestT();
                if (currentT != -1 && currentT < closestT) {
                    closestT = currentT;
                    closestObject = obj;
                }
            }
        }

        return closestObject; // Return the closest object hit
    }

    // Clear all the game objects
    public void clearGameObjects() {
        gameObjects.clear();
    }

    public List<GameObject> getCollisionObjects() {
        return collisionObjects;
    }

}
