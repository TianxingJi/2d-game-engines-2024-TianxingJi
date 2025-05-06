package engine.gameobjects;

import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;

public class CircleColliderComponent implements CollisionComponent {
    private double radius;
    private boolean active = true;  // Collider is active by default
    private boolean isStatic = false;  // New field to track whether the object is static
    TransformComponent transform;  // Reference to the transform component
    double restitution;
    public PhysicsComponent physics;

    public CircleColliderComponent(TransformComponent transform, double radius, boolean isStatic, double restitution, PhysicsComponent physics) {
        this.transform = transform;  // Reference to the transform, which will handle position
        this.radius = radius;
        this.isStatic = isStatic;
        this.restitution = restitution;
        this.physics = physics;
    }

    @Override
    public void syncWithTransform(TransformComponent transform, double resizeScaleX, double resizeScaleY) {
        this.radius *= (resizeScaleX + resizeScaleY) / 2.0;  // Scale radius using the average of resize scales
    }

    @Override
    public boolean collidesWith(CollisionComponent other) {
        if (other instanceof CircleColliderComponent) {
            return this.collidesWithCircle((CircleColliderComponent) other);
        } else if (other instanceof BoxColliderComponent) {
            return this.collidesWithBox((BoxColliderComponent) other);
        } else if (other instanceof PolygonColliderComponent) {
            return this.collidesWithPolygon((PolygonColliderComponent) other);
        } else if (other instanceof RayColliderComponent) {
            return this.collidesWithRay((RayColliderComponent) other);
        }
        return false;
    }

    private boolean collidesWithCircle(CircleColliderComponent other) {
        Vec2d position = getPosition();  // Get the position from the transform component
        Vec2d difference = position.minus(other.getPosition());
        double radiiSum = this.radius + other.getRadius();

        // Check for collision
        if (squaredMagnitude(difference) <= radiiSum * radiiSum) {
            // Collision occurred, now calculate MTV
            double distance = difference.mag();
            double overlap = radiiSum - distance;

            // Normalize the direction to get the collision normal
            Vec2d collisionNormal = difference.normalize();
            Vec2d mtv = collisionNormal.smult(overlap);

            // Apply MTV based on whether objects are static or dynamic
            if (!this.isStatic && !other.isStatic()) {
                this.transform.setPosition(getTransformPosition().plus(mtv.smult(0.5)));
                other.transform.setPosition(other.getTransformPosition().minus(mtv.smult(0.5)));
            } else if (!this.isStatic) {
                this.transform.setPosition(getTransformPosition().plus(mtv));
            } else if (!other.isStatic()) {
                other.transform.setPosition(other.getTransformPosition().minus(mtv));
            }

            // Calculate relative velocity in the direction of the collision normal
            Vec2d relativeVelocity = other.physics.getVelocity().minus(this.physics.getVelocity());
            double velocityAlongNormal = relativeVelocity.dot(collisionNormal);

            // Do not resolve if velocities are separating
            if (velocityAlongNormal <= 0) {
                return false;
            }

            // Calculate restitution using the geometric mean of both colliders' restitution
            double restitution = Math.sqrt(this.getRestitution() * other.getRestitution());

            // Calculate impulse scalar
            double impulseMagnitude;
            if (this.isStatic) {
                impulseMagnitude = -(1 + restitution) * velocityAlongNormal / (1 / other.physics.getMass());
            } else if (other.isStatic()) {
                impulseMagnitude = -(1 + restitution) * velocityAlongNormal / (1 / this.physics.getMass());
            } else {
                impulseMagnitude = -(1 + restitution) * velocityAlongNormal;
                impulseMagnitude /= (1 / this.physics.getMass() + 1 / other.physics.getMass());
            }

            // Calculate the impulse vector
            Vec2d impulse = collisionNormal.smult(impulseMagnitude);

            // Apply the impulse to both objects
            if (!this.isStatic) {
                this.physics.applyImpulse(impulse.smult(-1));  // Apply opposite impulse to this object
            }
            if (!other.isStatic()) {
                other.physics.applyImpulse(impulse);  // Apply impulse to the other object
            }

            return true;  // Return true, meaning collision detected and resolved
        }

        return false;  // No collision
    }

    private boolean collidesWithBox(BoxColliderComponent box) {
        return box.collidesWith(this);  // Delegate to box's collision detection logic
    }

    private boolean collidesWithPolygon(PolygonColliderComponent polygon) {
        return polygon.collidesWith(this);  // Delegate to box's collision detection logic
    }

    private boolean collidesWithRay(RayColliderComponent ray) {
        return ray.collidesWith(this);
    }

    private double squaredMagnitude(Vec2d vec) {
        return vec.x * vec.x + vec.y * vec.y;
    }

    @Override
    public Vec2d getPosition() {
        // Return the center of the circle by adding the radius to the transform's top-left position
        return transform.getPosition().plus(new Vec2d(radius, radius));
    }


    public Vec2d getTransformPosition(){
        return transform.getPosition();
    }

    @Override
    public void setPosition(Vec2d position) {
        // Position is now controlled by the TransformComponent, so no need to set it manually
    }

    @Override
    public ColliderType getType() {
        return ColliderType.CIRCLE;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public void onTick(long nanosSincePreviousTick) {
        // Sync the position with the transform (if needed), but it's automatically handled now
    }

    @Override
    public void onDraw(GraphicsContext g) {

    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    public double getRestitution() {
        return restitution;
    }
}
