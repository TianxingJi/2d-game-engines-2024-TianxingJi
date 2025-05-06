package engine.gameobjects;

import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;

import java.util.List;

public class RayColliderComponent implements CollisionComponent{
    private Vec2d src;
    private Vec2d dir;
    private boolean active = true;
    private boolean isStatic = false;
    private double restitution;
    private TransformComponent transform;
    private PhysicsComponent physics;
    private double closestT = Double.MAX_VALUE; // Set initial value to a large number
    private CollisionComponent closestHitObjectCollisionComponent = null; // Stores the closest object hit
    private Vec2d impulseDirection; // Direction of impulse

    public RayColliderComponent(TransformComponent transform, Vec2d dir, boolean isStatic, double restitution, PhysicsComponent physics){
        this.src = transform.getPosition();
        this.dir = dir.normalize(); // Ensure direction is normalized
        this.isStatic = isStatic;
        this.restitution = restitution;
        this.transform = transform;
        this.physics = physics;
    }

    // Call this before checking collisions to reset the closest t-value
    public void resetClosestT() {
        this.closestT = Double.MAX_VALUE; // Reset to a large value
        this.closestHitObjectCollisionComponent = null;
    }

    // Return the closest t-value after all collisions are checked
    public double getClosestT() {
        // If closestT remains Double.MAX_VALUE, it means no collision was found
        return closestT == Double.MAX_VALUE ? -1 : closestT;
    }

    // Method to apply impulse to the closest hit object's PhysicsComponent
    public void applyImpulseToHitObject(double impulseStrength) {
        if (closestHitObjectCollisionComponent != null) {
            PhysicsComponent physics = null;

            if (closestHitObjectCollisionComponent instanceof BoxColliderComponent) {
                physics = ((BoxColliderComponent) closestHitObjectCollisionComponent).physics;
            } else if (closestHitObjectCollisionComponent instanceof CircleColliderComponent) {
                physics = ((CircleColliderComponent) closestHitObjectCollisionComponent).physics;
            } else if (closestHitObjectCollisionComponent instanceof PolygonColliderComponent) {
                physics = ((PolygonColliderComponent) closestHitObjectCollisionComponent).physics;
            }

            if (physics != null) {
                // Apply impulse in the direction of the ray
                Vec2d impulse = dir.normalize().smult(impulseStrength);
                physics.applyImpulse(impulse);
            }
        }
    }

    @Override
    public boolean collidesWith(CollisionComponent other) {
        double t = -1;

        if (other instanceof CircleColliderComponent) {
            t = this.collidesWithCircle((CircleColliderComponent) other);
        } else if (other instanceof PolygonColliderComponent) {
            t = this.collidesWithPolygon((PolygonColliderComponent) other);
        } else if (other instanceof BoxColliderComponent) {
            t = this.collidesWithBox((BoxColliderComponent) other);
        }

        // If t is valid and closer than the previous t, update closestT and closestHitObject
        if (t >= 0 && t <= closestT) {
            closestT = t;
            closestHitObjectCollisionComponent = other; // Assuming CollisionComponent has a method to get its owner GameObject
            return true;
        }

        // Return true if there is a collision (valid t value)
        return false;
    }

    // These methods should calculate t-value for intersection and return it.
    public double collidesWithCircle(CircleColliderComponent other) {
// 1. Calculate the vector from the ray source to the circle center
        Vec2d rayToCircleCenter = other.getPosition().minus(src);

        // 2. Project the vector onto the ray direction
        Vec2d projectionOntoRayDir = rayToCircleCenter.projectOnto(dir);

        // 3. Check if the projection is behind the ray origin
        if (projectionOntoRayDir.dot(dir) < 0) {
            return -1; // No collision if the circle is behind the ray
        }

        // 4. Calculate the closest point on the ray to the circle center
        Vec2d closestPointOnRay = src.plus(projectionOntoRayDir);

        // 5. Calculate the squared distance from the closest point to the circle center
        Vec2d closestPointToCenter = other.getPosition().minus(closestPointOnRay);
        double distanceToCenterSquared = closestPointToCenter.mag2();

        // 6. Compare with the radius squared to check if the distance is within the circle's radius
        double radiusSquared = other.getRadius() * other.getRadius();
        if (distanceToCenterSquared > radiusSquared) {
            return -1; // No intersection if outside the radius
        }

        // 7. Calculate the distance to the intersection point
        double distanceToIntersection = Math.sqrt(radiusSquared - distanceToCenterSquared);

        // 8. Determine the t value based on whether the ray origin is inside the circle
        double t;
        if (rayToCircleCenter.mag() < other.getRadius()) {
            // Ray origin is inside the circle, add distanceToIntersection
            t = projectionOntoRayDir.mag() + distanceToIntersection;
        } else {
            // Ray origin is outside the circle, subtract distanceToIntersection
            t = projectionOntoRayDir.mag() - distanceToIntersection;
        }

        // 9. If t is negative, return -1 since it's behind the ray's origin
        return t >= 0 ? t : -1;
    }

    public double collidesWithBox(BoxColliderComponent other) {
        // Step 1: Calculate the corners of the box
        Vec2d topLeft = other.getTransformPosition();
        Vec2d size = other.getSize();

        Vec2d[] corners = {
                topLeft,
                topLeft.plus(new Vec2d(size.x, 0)),        // Top-right
                topLeft.plus(size),                         // Bottom-right
                topLeft.plus(new Vec2d(0, size.y))          // Bottom-left
        };

        double minT = Double.MAX_VALUE;
        boolean intersectionFound = false;

        // Step 2: Iterate over each edge of the box (4 edges in total)
        for (int i = 0; i < 4; i++) {
            // Define the current edge with two vertices
            Vec2d a = corners[i];
            Vec2d b = corners[(i + 1) % 4]; // Wrap around to form a closed loop

            // Vectors from ray source to each endpoint of the edge
            Vec2d vecA = a.minus(src);
            Vec2d vecB = b.minus(src);

            // Cross product to check if segment straddles the ray
            double crossA = vecA.cross(dir);
            double crossB = vecB.cross(dir);

            // If cross products are both positive or both negative, skip this edge
            if (crossA * crossB > 0) {
                continue;
            }

            // Calculate the edge direction and its normal
            Vec2d edgeDir = b.minus(a).normalize();
            Vec2d edgeNormal = new Vec2d(-edgeDir.y, edgeDir.x); // Perpendicular to the edge

            // Calculate t using the formula t = ( (a - p) • n ) / ( d • n )
            double numerator = vecA.dot(edgeNormal);
            double denominator = dir.dot(edgeNormal);

            // If denominator is zero, the ray is parallel to the edge
            if (Math.abs(denominator) < 1e-6) {
                continue;
            }

            double t = numerator / denominator;

            // Only consider positive t values (in the direction of the ray)
            if (t >= 0) {
                intersectionFound = true;
                minT = Math.min(minT, t);
            }
        }

        // Return the smallest positive t value if any intersection is found
        return intersectionFound ? minT : -1;
    }

    public double collidesWithPolygon(PolygonColliderComponent other) {
        int numPoints = other.getNumPoints();
        double minT = Double.MAX_VALUE;
        boolean intersectionFound = false;

        // Iterate over each edge of the polygon
        for (int i = 0; i < numPoints; i++) {
            // Define the current edge with two vertices
            Vec2d a = other.getPoint(i);
            Vec2d b = other.getPoint((i + 1) % numPoints); // Wrap around to the first point

            // Vectors from ray source to each endpoint of the edge
            Vec2d vecA = a.minus(src);
            Vec2d vecB = b.minus(src);

            // Cross product to check if the edge straddles the ray
            double crossA = vecA.cross(dir);
            double crossB = vecB.cross(dir);

            // If cross products are both positive or both negative, skip this edge
            if (crossA * crossB > 0) {
                continue;
            }

            // Calculate the edge direction and its normal
            Vec2d edgeDir = b.minus(a).normalize();
            Vec2d edgeNormal = new Vec2d(-edgeDir.y, edgeDir.x); // Perpendicular to the edge

            // Calculate t using the formula t = ( (a - src) • n ) / ( dir • n )
            double numerator = vecA.dot(edgeNormal);
            double denominator = dir.dot(edgeNormal);

            // If denominator is zero, the ray is parallel to the edge
            if (Math.abs(denominator) < 1e-6) {
                continue;
            }

            double t = numerator / denominator;

            // Only consider positive t values (in the direction of the ray)
            if (t >= 0) {
                intersectionFound = true;
                minT = Math.min(minT, t);
            }
        }

        // Return the smallest positive t value if any intersection is found
        return intersectionFound ? minT : -1;
    }

    @Override
    public void syncWithTransform(TransformComponent transform, double resizeScaleX, double resizeScaleY) {

    }

    @Override
    public Vec2d getPosition() {
        return src;
    }

    @Override
    public void setPosition(Vec2d position) {

    }

    @Override
    public ColliderType getType() {
        return ColliderType.RAY;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {

    }

    @Override
    public void onTick(long nanosSincePreviousTick) {

    }

    @Override
    public void onDraw(GraphicsContext g) {

    }

    public Vec2d getDirection() {
        return dir;
    }
}
