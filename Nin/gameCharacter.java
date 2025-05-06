package Nin;

import engine.gameobjects.*;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class gameCharacter extends GameObject {
    private AnimationComponent currentAnimation;  // The current animation (can be switched)
    private double health;  // Health points for the character
    private double maxHealth;  // Maximum health points
    private double movementSpeed;  // Movement speed for the character
    private double rushSpeed;
    private boolean isDead = false;  // Character death state
    protected boolean facingLeft = false; // Flag for direction
    private TransformComponent transform;  // Transform component to handle position and size
    private BoxColliderComponent boxCollider;  // Box collider for collision detection
    private PhysicsComponent physics;
    private boolean isGrounded = false;
    protected double rescaleAverage = 1.0;
    Vec2d movementForce = new Vec2d(500, 0);  // Adjust the value for desired movement speed
    Vec2d jumpForce = new Vec2d(0, -3200);  // Adjust jump force as needed
    private long jumpBufferTime = 1_000_000; // Buffer time in nanoseconds
    private long bufferStartTime = 0;
    private boolean isBuffering = false;

    private boolean isColldingWithWall = false;

    // Constructor to initialize the character with health, movement speed, animation, and collision component
    public gameCharacter(int id, TransformComponent transform, double maxHealth, double movementSpeed, AnimationComponent initialAnimation, Vec2d colliderSize, PhysicsComponent physics) {
        super(id);
        this.maxHealth = maxHealth;
        this.health = maxHealth;  // Start with full health
        this.movementSpeed = movementSpeed;
        this.rushSpeed = movementSpeed * 3;
        this.currentAnimation = initialAnimation;
        this.transform = transform;  // Use TransformComponent to manage position and size
        this.physics = physics;
        this.boxCollider = new BoxColliderComponent(transform, colliderSize, false, 0.5, physics);  // Create the box collider
        addComponent(initialAnimation);  // Add the initial animation as a component
        addComponent(transform);  // Add the transform as a component
        addComponent(boxCollider);  // Add the box collider as a component
        setZIndex(2);
    }

    // Set the current animation (this allows switching between different animations)
    public void setCurrentAnimation(AnimationComponent animation) {
        removeComponent(currentAnimation);  // Remove the old animation
        this.currentAnimation = animation;
        addComponent(currentAnimation);  // Add the new animation
    }

    // Updates the character state every game tick
    @Override
    public void onTick(long nanosSincePreviousTick) {
        super.onTick(nanosSincePreviousTick);  // Call parent method to tick all components
        // Add additional logic here, e.g., movement or AI behavior for enemies
        if (health <= 0) {
            isDead = true;
        }
    }

    // Draws the character and its components, including health bar
    @Override
    public void onDraw(GraphicsContext g) {
        if (facingLeft) {
            // Flip the image horizontally by applying a scale transformation
            g.save();
            g.scale(-1, 1); // Flip along the x-axis
            g.translate(-transform.getPosition().x * 2 - transform.getSize().x, 0); // Adjust translation to the flipped position
            super.onDraw(g);  // Draw the animation flipped
            g.restore();
        } else {
            super.onDraw(g);  // Normal draw when facing right
        }

        drawHealthBar(g);  // Draw health bar above the character
    }

    // Draws the health bar above the character
    private void drawHealthBar(GraphicsContext g) {
        double barWidth = transform.getSize().x;
        double barHeight = 5;
        double healthPercentage = health / maxHealth;
        double barX = transform.getPosition().x ;  // Use TransformComponent's position
        double barY = transform.getPosition().y - 10;  // Offset a bit above the character's sprite

        // Draw the health bar background (red)
        g.setFill(Color.RED);
        g.fillRect(barX, barY, barWidth, barHeight);

        // Draw the actual health portion (green)
        g.setFill(Color.GREEN);
        g.fillRect(barX, barY, barWidth * healthPercentage, barHeight);
    }

    // Reduces the character's health
    public void takeDamage(double damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            isDead = true;
        }
    }

    // Increases the character's health
    public void heal(double amount) {
        health += amount;
        if (health > maxHealth) {
            health = maxHealth;
        }
    }

    // Sets the movement speed
    public void setMovementSpeed(double speed) {
        this.movementSpeed = speed;
    }

    // Gets the current movement speed
    public double getMovementSpeed() {
        return movementSpeed;
    }

    public double getRushSpeed() {
        return rushSpeed;
    }
    // Sets the position using the TransformComponent
    public void setPosition(Vec2d position) {
        transform.setPosition(position);
    }

    // Gets the current position using the TransformComponent
    public Vec2d getPosition() {
        return transform.getPosition();
    }

    // Checks if the character is dead
    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    // Gets the BoxColliderComponent (for external use if needed)
    public BoxColliderComponent getBoxCollider() {
        return boxCollider;
    }

    // Changes the facing direction
    public void setFacingLeft(boolean facingLeft) {
        this.facingLeft = facingLeft;
    }

    public boolean isFacingLeft() {
        return facingLeft;
    }

    public void onResize(double rescaleAverage){
        this.rescaleAverage = rescaleAverage;
    }

    public double getRescaleAverage() {
        return rescaleAverage;
    }

    public boolean isGrounded() {
        return isGrounded;
    }

    public void setGrounded(boolean grounded) {
        isGrounded = grounded;
    }

    public PhysicsComponent getPhysics() {
        return physics;
    }

    public Vec2d getMovementForce() {
        return movementForce;
    }

    public Vec2d getJumpForce() {
        return jumpForce;
    }

    public void startBuffering() {
        isBuffering = true;
        bufferStartTime = System.nanoTime();
    }

    public boolean isBuffering() {
        if (isBuffering) {
            long elapsedTime = System.nanoTime() - bufferStartTime;
            if (elapsedTime >= jumpBufferTime) {
                isBuffering = false;
            }
        }
        return isBuffering;
    }

}
