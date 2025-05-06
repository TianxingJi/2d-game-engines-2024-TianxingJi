package engine.gameobjects;

import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;


public class PhysicsComponent implements Component {
    private double mass;  // Mass of the object
    private Vec2d position;  // Position of the object
    private Vec2d velocity;  // Current velocity of the object
    private Vec2d force = new Vec2d(0, 0);  // Accumulated force applied to the object
    private Vec2d impulse = new Vec2d(0, 0);  // Accumulated impulse to be applied
    TransformComponent transform;

    public PhysicsComponent(double mass, TransformComponent transform) {
        this.mass = mass;
        this.transform = transform;
        this.position = transform.getPosition();
        this.velocity = new Vec2d(0, 0);  // Start with zero velocity
    }

    // Apply a continuous force to the object
    public void applyForce(Vec2d force) {
        this.force = this.force.plus(force);
    }

    // Apply an instantaneous impulse to the object
    public void applyImpulse(Vec2d impulse) {
        this.impulse = this.impulse.plus(impulse);
    }

    @Override
    public void onTick(long nanosSincePreviousTick) {
     // Update velocity with force and impulse
            // Convert nanoseconds to seconds for consistent calculations
        double deltaTime = nanosSincePreviousTick / 1_000_000_000.0;
            // Apply impulse directly to velocity
        velocity = velocity.plus(impulse.smult(1.0 / mass));

            // Calculate acceleration from the accumulated force
        Vec2d acceleration = force.smult(1.0 / mass);

            // Update velocity based on acceleration and time
        velocity = velocity.plus(acceleration.smult(deltaTime));

        // Update position based on velocity and time
        position = getPosition().plus(velocity.smult(deltaTime));
        setPosition(position);

        // Clear the force and impulse for the next frame
        force = new Vec2d(0, 0);
        impulse = new Vec2d(0, 0);
    }

    @Override
    public void onDraw(GraphicsContext g) {

    }

    // Getters and setters
    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public Vec2d getPosition() {
        return transform.getPosition();
    }

    public void setPosition(Vec2d position) {
        transform.setPosition(position);
    }

    public Vec2d getVelocity() {
        return velocity;
    }

    public void setVelocity(Vec2d velocity) {
        this.velocity = velocity;
    }

}