package engine;

import engine.UIkit.ViewPort;
import engine.gameobjects.GameObject;
import engine.support.Vec2d;
import engine.systems.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class GameWorld {

    protected RenderSystem renderSystem;  // System for rendering
    protected PhysicsSystem physicsSystem;  // System for physics
    protected DragAndDropSystem dragAndDropSystem;
    protected CollisionSystem collisionSystem;

    protected ViewPort viewPort;
    protected Vec2d currentStageSize;

    public GameWorld(ViewPort viewPort) {
        // Initialize systems
        this.renderSystem = new RenderSystem();
        this.physicsSystem = new PhysicsSystem();
        this.collisionSystem = new CollisionSystem();
        this.viewPort = viewPort;
        this.dragAndDropSystem = new DragAndDropSystem(viewPort, collisionSystem, renderSystem);
    }

    public void onStartUp(Vec2d currentStageSize) {
        this.currentStageSize = currentStageSize;
    }

    // Getters for systems and UI elements
    public RenderSystem getRenderSystem() {
        return renderSystem;
    }

    public PhysicsSystem getPhysicsSystem() {
        return physicsSystem;
    }

    public DragAndDropSystem getDragAndDropSystem() {
        return dragAndDropSystem;
    }

    public CollisionSystem getCollisionSystem() {
        return collisionSystem;
    }

    // Add GameObject to RenderSystem
    public void addToRenderSystem(GameObject obj) {
        renderSystem.addGameObject(obj);
    }

    // Remove GameObject from RenderSystem
    public void removeFromRenderSystem(GameObject obj) {
        renderSystem.removeGameObject(obj);
    }

    // Add GameObject to PhysicsSystem
    public void addToPhysicsSystem(GameObject obj) {
        physicsSystem.addGameObject(obj);
    }

    // Remove GameObject from RenderSystem
    public void removeFromPhysicsSystem(GameObject obj) {
        physicsSystem.removeGameObject(obj);
    }

    public void addToDragAndDropSystem(GameObject obj) {
        dragAndDropSystem.registerDraggable(obj);
    }

    public void removeFromDragAndDropSystem(GameObject obj) {
        dragAndDropSystem.removeGameObject(obj);
    }

    public void addToCollisionSystem(GameObject obj) {
        collisionSystem.addGameObject(obj);
    }

    public void removeFromCollisionSystem(GameObject obj) {
        collisionSystem.removeGameObject(obj);
    }

    // Remove GameObject from both RenderSystem and PhysicsSystem
    public void addToSystems(GameObject obj) {
        renderSystem.addGameObject(obj);
        physicsSystem.addGameObject(obj);
        collisionSystem.addGameObject(obj);
    }

    // Remove GameObject from both RenderSystem and PhysicsSystem
    public void removeFromSystems(GameObject obj) {
        renderSystem.removeGameObject(obj);
        physicsSystem.removeGameObject(obj);
        collisionSystem.removeGameObject(obj);
    }

    public void cleanSystems() {
        renderSystem.clearGameObjects();
        physicsSystem.clearGameObjects();
        collisionSystem.clearGameObjects();
        dragAndDropSystem.clearGameObjects();
    }

    // Method to update systems, called every frame
    public void onTick(long nanosSincePreviousTick) {
        // Update physics system
        collisionSystem.onTick(nanosSincePreviousTick);
        physicsSystem.onTick(nanosSincePreviousTick);
    }

    // Render all game objects and UI elements
    public void onDraw(GraphicsContext g) {
        // Render all game objects in the render system
        renderSystem.onDraw(g);
    }

    public void onResize(Vec2d newSize, Vec2d oldSize) {
        renderSystem.onResize(newSize, oldSize);
    }

    public void onMouseClicked(MouseEvent e) {}

    public void onMousePressed(MouseEvent e) {}

    public void onMouseDragged(MouseEvent e) {}

    public void onMouseReleased(MouseEvent e) {}

    public void onKeyPressed(KeyEvent e) {}
    public void onKeyReleased(KeyEvent e) {}

}
