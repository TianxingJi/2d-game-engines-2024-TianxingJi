package Nin;

import Nin.NinGameWorld;
import engine.UIkit.UIElement;
import engine.UIkit.UIImageElement;
import engine.UIkit.UIPanel;
import engine.UIkit.UIText;
import engine.gameobjects.*;
import Nin.gameCharacter;
import engine.support.Vec2d;
import engine.systems.CollisionSystem;
import engine.systems.DragAndDropSystem;
import engine.systems.PhysicsSystem;
import engine.systems.RenderSystem;
import javafx.scene.text.Font;

import javax.swing.text.html.HTML;
import java.util.ArrayList;
import java.util.List;

public class GameCollisionSystem extends CollisionSystem {
    private List<GameObject> collisionObjects;
    private RenderSystem renderSystem;  // Reference to the RenderSystem
    private PhysicsSystem physicsSystem;
    private Vec2d currentStageSize;
    private NinGameWorld gameWorld;
    private boolean knightWallCollided = false;
    private boolean boxWallCollided = false;
    private boolean bornWallCollided = false;

    public GameCollisionSystem(RenderSystem renderSystem, Vec2d currentStageSize) {
        this.renderSystem = renderSystem;  // Store the reference
        this.collisionObjects = new ArrayList<>();
        this.currentStageSize = currentStageSize;
        this.physicsSystem = null;
    }

    public GameCollisionSystem(RenderSystem renderSystem, PhysicsSystem physicsSystem, Vec2d currentStageSize, NinGameWorld gameWorld) {
        this.renderSystem = renderSystem;  // Store the reference
        this.physicsSystem = physicsSystem;
        this.collisionObjects = new ArrayList<>();
        this.currentStageSize = currentStageSize;
        this.gameWorld = gameWorld;
    }

    public void updateCurrentStageSize(Vec2d size) {
        this.currentStageSize = size;
    }

    @Override
    protected void handleCollisionResult(GameObject objA, GameObject objB) {
        TagComponent tagA = objA.getComponent(TagComponent.class);
        TagComponent tagB = objB.getComponent(TagComponent.class);

        // Check if objA is the wall and objB is the knight
        if (tagA.hasTag("wall") && tagB.hasTag("knight")) {
            PhysicsComponent knightPhysics = objB.getComponent(PhysicsComponent.class);
            gameCharacter knight = (gameCharacter) objB;
            if (knightPhysics != null && !knight.isBuffering()) {
                knightPhysics.setVelocity(new Vec2d(knightPhysics.getVelocity().x, 0));
                knight.setGrounded(true);
                knightWallCollided = true;
            }
        }
        // Check if objB is the wall and objA is the knight
        else if (tagB.hasTag("wall") && tagA.hasTag("knight")) {
            PhysicsComponent knightPhysics = objA.getComponent(PhysicsComponent.class);
            gameCharacter knight = (gameCharacter) objA;
            if (knightPhysics != null && !knight.isBuffering()) {
                knightPhysics.setVelocity(new Vec2d(knightPhysics.getVelocity().x, 0));
                knight.setGrounded(true);
                knightWallCollided = true;
            }
        }

        // Check if objA is the wall and objB is the knight
        if (!tagA.hasTag("wall") && tagB.hasTag("knight")) {
            PhysicsComponent knightPhysics = objB.getComponent(PhysicsComponent.class);
            gameCharacter knight = (gameCharacter) objB;
            if (knightPhysics != null) {
                knight.setGrounded(false);
            }
        }
        // Check if objB is the wall and objA is the knight
        else if (!tagB.hasTag("wall") && tagA.hasTag("knight")) {
            PhysicsComponent knightPhysics = objA.getComponent(PhysicsComponent.class);
            gameCharacter knight = (gameCharacter) objA;
            if (knightPhysics != null) {
                knight.setGrounded(false);
            }
        }

        // Check if objA is the wall and objB is the knight
        if (tagA.hasTag("wall") && tagB.hasTag("box")) {
            PhysicsComponent boxPhysics = objB.getComponent(PhysicsComponent.class);
            NinGameObject box = (NinGameObject) objB;
            if (boxPhysics != null) {
                boxPhysics.setVelocity(new Vec2d(boxPhysics.getVelocity().x, 0));
                box.setGrounded(true);
                boxWallCollided = true;
            }
        }
        // Check if objB is the wall and objA is the knight
        else if (tagB.hasTag("wall") && tagA.hasTag("box")) {
            PhysicsComponent boxPhysics = objA.getComponent(PhysicsComponent.class);
            NinGameObject box = (NinGameObject) objA;
            if (boxPhysics != null) {
                boxPhysics.setVelocity(new Vec2d(boxPhysics.getVelocity().x, 0));
                box.setGrounded(true);
                boxWallCollided = true;
            }
        }

        // Check if objA is the wall and objB is the knight
        if (!tagA.hasTag("wall") && tagB.hasTag("box")) {
            PhysicsComponent boxPhysics = objB.getComponent(PhysicsComponent.class);
            NinGameObject box = (NinGameObject) objB;
            if (boxPhysics != null) {
                box.setGrounded(false);
            }
        }
        // Check if objB is the wall and objA is the knight
        else if (!tagB.hasTag("wall") && tagA.hasTag("box")) {
            PhysicsComponent boxPhysics = objA.getComponent(PhysicsComponent.class);
            NinGameObject box = (NinGameObject) objA;
            if (boxPhysics != null) {
                box.setGrounded(false);
            }
        }

        // Check if objA is the wall and objB is the knight
        if (tagA.hasTag("wall") && tagB.hasTag("born")) {
            PhysicsComponent bornPhysics = objB.getComponent(PhysicsComponent.class);
            NinGameObject born = (NinGameObject) objB;
            if (bornPhysics != null) {
                bornPhysics.setVelocity(new Vec2d(bornPhysics.getVelocity().x, 0));
                born.setGrounded(true);
                bornWallCollided = true;
            }
        }
        // Check if objB is the wall and objA is the knight
        else if (tagB.hasTag("wall") && tagA.hasTag("born")) {
            PhysicsComponent bornPhysics = objA.getComponent(PhysicsComponent.class);
            NinGameObject born = (NinGameObject) objA;
            if (bornPhysics != null) {
                bornPhysics.setVelocity(new Vec2d(bornPhysics.getVelocity().x, 0));
                born.setGrounded(true);
                bornWallCollided = true;
            }
        }

        // Check if objA is the wall and objB is the knight
        if (!tagA.hasTag("wall") && tagB.hasTag("born")) {
            PhysicsComponent bornPhysics = objB.getComponent(PhysicsComponent.class);
            NinGameObject born = (NinGameObject) objB;
            if (bornPhysics != null) {
                born.setGrounded(false);
            }
        }
        // Check if objB is the wall and objA is the knight
        else if (!tagB.hasTag("wall") && tagA.hasTag("born")) {
            PhysicsComponent bornPhysics = objA.getComponent(PhysicsComponent.class);
            NinGameObject born = (NinGameObject) objA;
            if (bornPhysics != null) {
                born.setGrounded(false);
            }
        }

        // Check if objA is the wall and objB is the knight
        if (tagA.hasTag("destination") && tagB.hasTag("knight")) {
            gameWorld.setReachDestination(true);
        }
        // Check if objB is the wall and objA is the knight
        else if (tagB.hasTag("wall") && tagA.hasTag("knight")) {
            gameWorld.setReachDestination(true);
        }

        // Check if objA is the destructible block and objB is the knight
        if (tagA.hasTag("destructible") && tagB.hasTag("knight")) {
            TransformComponent transformA = objA.getComponent(TransformComponent.class);
            if (transformA != null) {
                transformA.setPosition(new Vec2d(0, 1000)); // Move the destructible block out of view
            }
        }
        // Check if objB is the destructible block and objA is the knight
        else if (tagB.hasTag("destructible") && tagA.hasTag("knight")) {
            TransformComponent transformB = objB.getComponent(TransformComponent.class);
            if (transformB != null) {
                transformB.setPosition(new Vec2d(0, 1000)); // Move the destructible block out of view
            }
        }


    }

    @Override
    public void onTick(long nanosSincePreviousTick) {
        super.onTick(nanosSincePreviousTick);

        // If no collision was detected for the knight, set it to be in the air
        if (!knightWallCollided) {
            gameWorld.getKnight().setGrounded(false);
        }

        // If no collision was detected for the box, set it to be in the air
        for (NinGameObject box : gameWorld.getGameObjectsWithTag("box")) {
            if (!boxWallCollided) {
                box.setGrounded(false);
            }
        }

        // If no collision was detected for the born object, set it to be in the air
        for (NinGameObject born : gameWorld.getGameObjectsWithTag("born")) {
            if (!bornWallCollided) {
                born.setGrounded(false);
            }
        }

        // Reset collision flags for the next tick
        knightWallCollided = false;
        boxWallCollided = false;
        bornWallCollided = false;
    }

}
