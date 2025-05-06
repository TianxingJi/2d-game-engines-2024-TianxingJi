package engine.systems;

import engine.UIkit.ViewPort;
import engine.gameobjects.CollisionComponent;
import engine.components.DraggableComponent;
import engine.gameobjects.GameObject;
import engine.gameobjects.TransformComponent;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

import java.util.Optional;

public class DragAndDropSystem extends System {

    private GameObject draggingObject;
    private Vec2d originalPosition;
    private ViewPort viewPort;
    private CollisionSystem collisionSystem;
    private RenderSystem renderSystem;

    // create the drag and drop system with a render system, which contains all the items that need rendering
    public DragAndDropSystem(ViewPort viewPort, CollisionSystem collisionSystem, RenderSystem renderSystem) {
        this.viewPort = viewPort;
        this.collisionSystem = collisionSystem;
        this.renderSystem = renderSystem;
    }

    public void setCollisionSystem(CollisionSystem collisionSystem) {
        this.collisionSystem = collisionSystem;
    }

    // register draggable game object
    public void registerDraggable(GameObject gameObject) {
        if (gameObject.getComponent(DraggableComponent.class) == null) {
            // if not draggable component, add one
            gameObject.addComponent(new DraggableComponent(gameObject));
        }
    }

    @Override
    public void onTick(long nanosSincePreviousTick) {
    }

    @Override
    public void onDraw(GraphicsContext g) {
    }

    public void onMousePressed(MouseEvent e) {
        // change the screen position to the world position
        Vec2d worldPosition = screenToWorld(new Vec2d(e.getX(), e.getY()));
        Optional<GameObject> gameObject = findClickedObject(worldPosition.x, worldPosition.y);
        gameObject.ifPresent(g -> {
            draggingObject = g;
            originalPosition = g.getComponent(TransformComponent.class).getPosition();

            // Disable the collider while dragging
            CollisionComponent collider = g.getComponent(CollisionComponent.class);
            if (collider != null) {
                collider.setActive(false);  // Disable collision detection while dragging
            }

            // set the state of dragging
            DraggableComponent draggable = g.getComponent(DraggableComponent.class);
            if (draggable != null) {
                draggable.setDragging(true);
            }
        });
    }

    public void onMouseDragged(MouseEvent e) {
        if (draggingObject != null) {
            // update the position of dragged item
            Vec2d worldPosition = screenToWorld(new Vec2d(e.getX(), e.getY()));
            draggingObject.getComponent(TransformComponent.class)
                    .setPosition(new Vec2d(worldPosition.x - draggingObject.getComponent(TransformComponent.class).getSize().x / 2,
                            worldPosition.y - draggingObject.getComponent(TransformComponent.class).getSize().y / 2));
        }
    }

    public void onMouseReleased(MouseEvent e, Vec2d currentStageSize) {
        if (draggingObject != null) {
            // decide whether to put the item
            Vec2d worldPosition = screenToWorld(new Vec2d(e.getX(), e.getY()));
            Vec2d dropPosition = new Vec2d(worldPosition.x - draggingObject.getComponent(TransformComponent.class).getSize().x / 2,
                    worldPosition.y - draggingObject.getComponent(TransformComponent.class).getSize().y / 2);

            // If the object was deleted in handleDrop, avoid further operations
            if (handleDrop(e, draggingObject, dropPosition, currentStageSize)) {
                draggingObject = null;  // clear the dragging object
                return;  // Object was deleted, so stop further processing
            }

            // Re-enable the collider after the drop
            CollisionComponent collider = draggingObject.getComponent(CollisionComponent.class);
            if (collider != null) {
                collider.setPosition(dropPosition);  // Update the collider's position
                collider.setActive(true);  // Re-enable collision detection
            }

            // reset the state of dragged item
            DraggableComponent draggable = draggingObject.getComponent(DraggableComponent.class);
            if (draggable != null) {
                draggable.setDragging(false);
            }

            draggingObject = null;  // clear the dragging object
        }
    }

    private Optional<GameObject> findClickedObject(double mouseX, double mouseY) {
        // find the object within some range of mouse position
        return renderSystem.getGameObjects()
                .stream()
                .filter(g -> g.getComponent(TransformComponent.class)
                        .contains(mouseX, mouseY))
                .findFirst();
    }

    public boolean handleDrop(MouseEvent e, GameObject gameObject, Vec2d dropPosition, Vec2d stageSize) {
        // Element panel dimensions
        double panelX = stageSize.x - 120;

        // Check if drop is within the element panel area
        if (e.getX() >= panelX && e.getX() <= panelX + 120){
            // Drop is within the element panel range, so remove the game object
            removeGameObject(gameObject);
            renderSystem.removeGameObject(gameObject);
            collisionSystem.removeGameObject(gameObject);
            return true;  // Object was deleted
        } else {
            // Check if drop is outside the main screen, reset to the original position
            if (e.getX() < 0 || e.getX() > stageSize.x || e.getY() < 0 || e.getY() > stageSize.y) {
                gameObject.getComponent(TransformComponent.class).setPosition(originalPosition);
            } else {
                // Otherwise, set to the new position
                gameObject.getComponent(TransformComponent.class).setPosition(dropPosition);
            }
        }
        return false;  // Object was not deleted
    }


    // change the screen position to the world position
    private Vec2d screenToWorld(Vec2d screenPos) {
        // get the current pan and zoom parameters
        double zoom = viewPort.getZoom();
        Vec2d viewPortPos = viewPort.getPosition();

        // calculate the world position
        return new Vec2d(
                (screenPos.x / zoom) + viewPortPos.x,  // zoom plus pan
                (screenPos.y / zoom) + viewPortPos.y
        );
    }

    // Clear all the game objects
    public void clearGameObjects() {
        gameObjects.clear();
    }

}

