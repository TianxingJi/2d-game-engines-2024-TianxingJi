package engine.gameobjects;

import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;

public class BoxColliderComponent implements CollisionComponent {
    private Vec2d size;  // Width and height
    private boolean active = true;  // Collider is active by default
    private boolean isStatic = false;  // New field to track whether the object is static
    TransformComponent transform;  // Reference to the transform component
    private double restitution;  // Restitution of the object
    public PhysicsComponent physics;

    public BoxColliderComponent(TransformComponent transform, Vec2d size, boolean isStatic, double restitution, PhysicsComponent physics) {
        this.transform = transform;  // Reference to the transform, which will handle position
        this.size = size;
        this.isStatic = isStatic;
        this.restitution = restitution;
        this.physics = physics;
    }

    @Override
    public void syncWithTransform(TransformComponent transform, double resizeScaleX, double resizeScaleY) {
        this.size = new Vec2d(size.x * resizeScaleX, size.y * resizeScaleY);  // Adjust the size based on scaling
    }

    @Override
    public boolean collidesWith(CollisionComponent other) {
        if (other instanceof CircleColliderComponent) {
            return this.collidesWithCircle((CircleColliderComponent) other);
        } else if (other instanceof BoxColliderComponent) {
            return this.collidesWithBox((BoxColliderComponent) other);
        } else if (other instanceof  PolygonColliderComponent) {
            return this.collidesWithPolygon((PolygonColliderComponent) other);
        } else if (other instanceof RayColliderComponent) {
            return this.collidesWithRay((RayColliderComponent) other);
        }
        return false;
    }

    // AABB collision detection method for BoxColliderComponent
    private boolean collidesWithBox(BoxColliderComponent other) {
        // Get the center positions of this box and the other box
        Vec2d thisCenter = getPosition();
        Vec2d otherCenter = other.getPosition();

        // Get the half sizes for easier calculations
        Vec2d thisHalfSize = this.getSize().smult(0.5);
        Vec2d otherHalfSize = other.getSize().smult(0.5);

        // Calculate the min and max corners based on the center position and half size
        Vec2d thisMin = thisCenter.minus(thisHalfSize);  // Top-left corner of this AABB
        Vec2d thisMax = thisCenter.plus(thisHalfSize);   // Bottom-right corner of this AABB
        Vec2d otherMin = otherCenter.minus(otherHalfSize);  // Top-left corner of the other AABB
        Vec2d otherMax = otherCenter.plus(otherHalfSize);   // Bottom-right corner of the other AABB

        // Debug info
//        System.out.println("ThisMin: " + thisMin + ", ThisMax: " + thisMax);
//        System.out.println("OtherMin: " + otherMin + ", OtherMax: " + otherMax);

        // Check for overlap on both x and y axes
        if (thisMax.x > otherMin.x && thisMin.x < otherMax.x &&
                thisMax.y > otherMin.y && thisMin.y < otherMax.y) {

            // Collision detected, now calculate MTV (Minimum Translation Vector)
            double overlapX1 = thisMax.x - otherMin.x;
            double overlapX2 = otherMax.x - thisMin.x;
            double overlapY1 = thisMax.y - otherMin.y;
            double overlapY2 = otherMax.y - thisMin.y;

            double mtvX = Math.min(overlapX1, overlapX2);  // Smallest x-axis overlap
            double mtvY = Math.min(overlapY1, overlapY2);  // Smallest y-axis overlap

            Vec2d mtv;
            if (mtvX < mtvY) {
                // MTV along x-axis
                mtv = (thisMin.x < otherMin.x) ? new Vec2d(-mtvX, 0) : new Vec2d(mtvX, 0);
            } else {
                // MTV along y-axis
                mtv = (thisMin.y < otherMin.y) ? new Vec2d(0, -mtvY) : new Vec2d(0, mtvY);
            }

            // Apply MTV based on whether objects are static or dynamic
            if (!this.isStatic && !other.isStatic) {
                // Both dynamic, split the MTV
                this.transform.setPosition(getTransformPosition().plus(mtv.smult(0.5)));
                other.transform.setPosition(other.getTransformPosition().minus(mtv.smult(0.5)));
            } else if (!this.isStatic) {
                this.transform.setPosition(getTransformPosition().plus(mtv));
            } else if (!other.isStatic) {
                other.transform.setPosition(other.getTransformPosition().minus(mtv));
            }

            // Impulse Here
            // Calculate the collision normal as the normalized MTV
            Vec2d collisionNormal = mtv.normalize();

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
                this.physics.applyImpulse(impulse.smult(-1));
            }
            if (!other.isStatic()) {
                other.physics.applyImpulse(impulse);
            }

            return true;  // Return true, meaning collision detected and resolved
        }

        return false;  // No collision
    }



    private boolean collidesWithCircle(CircleColliderComponent circle) {
        // Get the center of the AABB from the transform
        Vec2d thisCenter = getPosition();
        Vec2d halfSize = this.getSize().smult(0.5); // Half size for easy calculations

        // Get the circle's center position
        Vec2d circleCenter = circle.getPosition();

        // Calculate the min and max corners of this AABB
        Vec2d thisMin = thisCenter.minus(halfSize);  // Top-left corner of this AABB
        Vec2d thisMax = thisCenter.plus(halfSize);   // Bottom-right corner of this AABB

        // Find the closest point on the AABB to the circle's center
        Vec2d closestPoint = new Vec2d(
                Math.max(thisMin.x, Math.min(circleCenter.x, thisMax.x)),
                Math.max(thisMin.y, Math.min(circleCenter.y, thisMax.y))
        );

        // Calculate the vector from the closest point on the AABB to the circle's center
        Vec2d difference = circleCenter.minus(closestPoint);
        double distance = difference.mag();

        // Case 1: Circle's center is INSIDE the AABB
        if (thisMin.x <= circleCenter.x && circleCenter.x <= thisMax.x &&
                thisMin.y <= circleCenter.y && circleCenter.y <= thisMax.y) {

            // Find the closest edge of the AABB
            double distanceToTop = Math.abs(circleCenter.y - thisMin.y);
            double distanceToBottom = Math.abs(circleCenter.y - thisMax.y);
            double distanceToLeft = Math.abs(circleCenter.x - thisMin.x);
            double distanceToRight = Math.abs(circleCenter.x - thisMax.x);

            // Determine the closest edge and adjust the closest point to that edge
            if (Math.min(distanceToLeft, distanceToRight) < Math.min(distanceToTop, distanceToBottom)) {
                if (distanceToLeft < distanceToRight) {
                    closestPoint = new Vec2d(thisMin.x, circleCenter.y);  // Left edge
                } else {
                    closestPoint = new Vec2d(thisMax.x, circleCenter.y);  // Right edge
                }
            } else {
                if (distanceToTop < distanceToBottom) {
                    closestPoint = new Vec2d(circleCenter.x, thisMin.y);  // Top edge
                } else {
                    closestPoint = new Vec2d(circleCenter.x, thisMax.y);  // Bottom edge
                }
            }

            // Recalculate the MTV based on the closest edge
            difference = circleCenter.minus(closestPoint);
            distance = difference.mag();
            double overlap = circle.getRadius() - distance;  // Circle needs to be pushed out of the AABB
            Vec2d mtv = difference.normalize().smult(overlap);

            // Apply MTV based on whether objects are static or dynamic
            if (!this.isStatic && !circle.isStatic()) {
                this.transform.setPosition(getTransformPosition().plus(mtv.smult(0.5)));
                circle.setPosition(circle.getTransformPosition().minus(mtv.smult(0.5)));
            } else if (!this.isStatic) {
                this.transform.setPosition(getTransformPosition().plus(mtv));
            } else if (!circle.isStatic()) {
                circle.setPosition(circle.getTransformPosition().minus(mtv));
            }

            // Impulse calculation
            Vec2d collisionNormal = difference.normalize();
            Vec2d relativeVelocity = circle.physics.getVelocity().minus(this.physics.getVelocity());
            double velocityAlongNormal = relativeVelocity.dot(collisionNormal);

            // Do not resolve if velocities are separating
            if (velocityAlongNormal > 0) {
                return false;
            }

            double restitution = Math.sqrt(this.getRestitution() * circle.getRestitution());
            double impulseMagnitude = calculateImpulseMagnitude(this, circle, restitution, velocityAlongNormal);

            Vec2d impulse = collisionNormal.smult(impulseMagnitude);

            // Apply the impulse to both objects
            applyImpulses(this, circle, impulse);

            return true;
        }

        // Case 2: Circle's center is OUTSIDE the AABB
        if (distance < circle.getRadius()) {
            double overlap = circle.getRadius() - distance;
            Vec2d mtv = difference.normalize().smult(overlap);

            // Apply MTV based on whether objects are static or dynamic
            if (!this.isStatic && !circle.isStatic()) {
                this.transform.setPosition(getTransformPosition().plus(mtv.smult(0.5)));
                circle.setPosition(circle.getTransformPosition().minus(mtv.smult(0.5)));
            } else if (!this.isStatic) {
                this.transform.setPosition(getTransformPosition().plus(mtv));
            } else if (!circle.isStatic()) {
                circle.setPosition(circle.getTransformPosition().minus(mtv));
            }

            Vec2d collisionNormal = difference.normalize();
            Vec2d relativeVelocity = circle.physics.getVelocity().minus(this.physics.getVelocity());
            double velocityAlongNormal = relativeVelocity.dot(collisionNormal);

            // Do not resolve if velocities are separating
            if (velocityAlongNormal <= 0) {
                return false;
            }

            double restitution = Math.sqrt(this.getRestitution() * circle.getRestitution());
            double impulseMagnitude = calculateImpulseMagnitude(this, circle, restitution, velocityAlongNormal);

            Vec2d impulse = collisionNormal.smult(impulseMagnitude);

            // Apply the impulse to both objects
            applyImpulses(this, circle, impulse);

            return true;
        }

        return false;
    }

    // Calculate the impulse magnitude based on restitution and velocity
    private double calculateImpulseMagnitude(BoxColliderComponent box, CircleColliderComponent circle, double restitution, double velocityAlongNormal) {
        double impulseMagnitude;
        if (box.isStatic) {
            impulseMagnitude = -(1 + restitution) * velocityAlongNormal / (1 / circle.physics.getMass());
        } else if (circle.isStatic()) {
            impulseMagnitude = -(1 + restitution) * velocityAlongNormal / (1 / box.physics.getMass());
        } else {
            impulseMagnitude = -(1 + restitution) * velocityAlongNormal;
            impulseMagnitude /= (1 / box.physics.getMass() + 1 / circle.physics.getMass());
        }
        return impulseMagnitude;
    }

    // Apply impulses to both objects
    private void applyImpulses(BoxColliderComponent box, CircleColliderComponent circle, Vec2d impulse) {
        if (!box.isStatic) {
            box.physics.applyImpulse(impulse.smult(-1));
        }
        if (!circle.isStatic()) {
            circle.physics.applyImpulse(impulse);
        }
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
        // Return the center of the box by adding half the size to the transform's top-left position
        return transform.getPosition().plus(size.smult(0.5));
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
        return ColliderType.BOX;
    }

    public Vec2d getSize() {
        return size;
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

    public double getRestitution(){
        return restitution;
    }
}
