package engine.gameobjects;

import debugger.collisions.AABShape;
import debugger.collisions.CircleShape;
import debugger.collisions.PolygonShape;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.List;

public class PolygonColliderComponent implements CollisionComponent {
    private List<Vec2d> vertices;
    private boolean active = true;
    private boolean isStatic = false;
    private double restitution;
    private TransformComponent transform;
    public PhysicsComponent physics;

    public PolygonColliderComponent(TransformComponent transform, List<Vec2d> vertices, boolean isStatic, double restitution, PhysicsComponent physics) {
        this.transform = transform;
        this.vertices = new ArrayList<>(vertices);
        this.isStatic = isStatic;
        this.restitution = restitution;
        this.physics = physics;
    }


    @Override
    public void syncWithTransform(TransformComponent transform, double resizeScaleX, double resizeScaleY) {
        // Adjust the vertices using the provided scaling factors
        double scale = (resizeScaleX + resizeScaleY) / 2;
        for (int i = 0; i < vertices.size(); i++) {
            Vec2d scaledVertex = vertices.get(i).smult(scale);
            vertices.set(i, scaledVertex);
        }
    }


    @Override
    public void onTick(long nanosSincePreviousTick) {

    }

    @Override
    public void onDraw(GraphicsContext g) {

    }

    @Override
    public boolean collidesWith(CollisionComponent other) {
        if (other instanceof CircleColliderComponent) {
            return this.collidesWithCircle((CircleColliderComponent) other);
        } else if (other instanceof PolygonColliderComponent) {
            return this.collidesWithPolygon((PolygonColliderComponent) other);
        } else if (other instanceof BoxColliderComponent) {
            return this.collidesWithBox((BoxColliderComponent) other);
        } else if (other instanceof RayColliderComponent) {
            return this.collidesWithRay((RayColliderComponent) other);
        }
        return false;
    }

    private boolean collidesWithPolygon(PolygonColliderComponent other) {
        int numAxes = this.getNumPoints() + other.getNumPoints();
        Vec2d[] axes = new Vec2d[numAxes];
        int axisIndex = 0;

        // Convert this polygon's vertices to world coordinates
        List<Vec2d> thisWorldVertices = new ArrayList<>();
        Vec2d thisTopLeft = this.transform.getPosition();
        for (Vec2d vertex : this.vertices) {
            thisWorldVertices.add(vertex.plus(thisTopLeft));
        }

        // Get axes from this polygon's edges
        for (int i = 0; i < thisWorldVertices.size(); i++) {
            Vec2d p1 = thisWorldVertices.get(i);
            Vec2d p2 = thisWorldVertices.get((i + 1) % thisWorldVertices.size());
            Vec2d edge = p2.minus(p1);
            Vec2d normal = new Vec2d(-edge.y, edge.x).normalize();
            axes[axisIndex++] = normal;
        }

        // Convert other polygon's vertices to world coordinates
        List<Vec2d> otherWorldVertices = new ArrayList<>();
        Vec2d otherTopLeft = other.transform.getPosition();
        for (Vec2d vertex : other.vertices) {
            otherWorldVertices.add(vertex.plus(otherTopLeft));
        }

        // Get axes from the other polygon's edges
        for (int i = 0; i < otherWorldVertices.size(); i++) {
            Vec2d p1 = otherWorldVertices.get(i);
            Vec2d p2 = otherWorldVertices.get((i + 1) % otherWorldVertices.size());
            Vec2d edge = p2.minus(p1);
            Vec2d normal = new Vec2d(-edge.y, edge.x).normalize();
            axes[axisIndex++] = normal;
        }

        double minOverlap = Double.MAX_VALUE;
        Vec2d mtvAxis = null;

        // Project both polygons onto each axis and check overlap
        for (Vec2d axis : axes) {
            double[] thisProjection = projectPolygon(thisWorldVertices, axis);
            double[] otherProjection = projectPolygon(otherWorldVertices, axis);

            double overlap = Math.min(thisProjection[1], otherProjection[1]) - Math.max(thisProjection[0], otherProjection[0]);
            if (overlap <= 0) {
                return false;
            }

            if (overlap < minOverlap) {
                minOverlap = overlap;
                mtvAxis = axis;
            }
        }

        Vec2d direction = other.getPosition().minus(this.getPosition());
        if (mtvAxis.dot(direction) < 0) {
            mtvAxis = mtvAxis.smult(-1);
        }

        Vec2d mtv = mtvAxis.smult(minOverlap);

        // Apply MTV based on whether objects are static or dynamic
        if (!this.isStatic && !other.isStatic) {
            this.transform.setPosition(getTransformPosition().plus(mtv.smult(0.5)));
            other.transform.setPosition(other.getTransformPosition().minus(mtv.smult(0.5)));
        } else if (!this.isStatic) {
            this.transform.setPosition(getTransformPosition().plus(mtv));
        } else if (!other.isStatic) {
            other.transform.setPosition(other.getTransformPosition().minus(mtv));
        }

        Vec2d collisionNormal = mtv.normalize();

        Vec2d relativeVelocity = other.physics.getVelocity().minus(this.physics.getVelocity());
        double velocityAlongNormal = relativeVelocity.dot(collisionNormal);

        if (velocityAlongNormal > 0) {
            return false;
        }

        double restitution = Math.sqrt(this.getRestitution() * other.getRestitution());
        double impulseMagnitude;

        if (this.isStatic) {
            impulseMagnitude = -(1 + restitution) * velocityAlongNormal / (1 / other.physics.getMass());
        } else if (other.isStatic()) {
            impulseMagnitude = -(1 + restitution) * velocityAlongNormal / (1 / this.physics.getMass());
        } else {
            impulseMagnitude = -(1 + restitution) * velocityAlongNormal;
            impulseMagnitude /= (1 / this.physics.getMass() + 1 / other.physics.getMass());
        }

        Vec2d impulse = collisionNormal.smult(impulseMagnitude);
        if (!this.isStatic) {
            this.physics.applyImpulse(impulse.smult(-1));
        }
        if (!other.isStatic()) {
            other.physics.applyImpulse(impulse);
        }

        return true;
    }


    private boolean collidesWithCircle(CircleColliderComponent circle) {
        int numAxes = this.getNumPoints() + 1;
        Vec2d[] axes = new Vec2d[numAxes];
        int axisIndex = 0;

        // Convert this polygon's vertices to world coordinates
        List<Vec2d> thisWorldVertices = new ArrayList<>();
        Vec2d thisTopLeft = this.transform.getPosition();
        for (Vec2d vertex : this.vertices) {
            thisWorldVertices.add(vertex.plus(thisTopLeft));
        }

        // Get axes from this polygon's edges
        for (int i = 0; i < thisWorldVertices.size(); i++) {
            Vec2d p1 = thisWorldVertices.get(i);
            Vec2d p2 = thisWorldVertices.get((i + 1) % thisWorldVertices.size());
            Vec2d edge = p2.minus(p1);
            Vec2d normal = new Vec2d(-edge.y, edge.x).normalize();
            axes[axisIndex++] = normal;
        }

        Vec2d closestPoint = findClosestPoint(circle.getPosition(), thisWorldVertices);
        Vec2d axisToCircle = closestPoint.minus(circle.getPosition()).normalize();
        axes[axisIndex] = axisToCircle;

        double minOverlap = Double.MAX_VALUE;
        Vec2d mtvAxis = null;

        for (Vec2d axis : axes) {
            double[] polygonProjection = projectPolygon(thisWorldVertices, axis);
            double[] circleProjection = projectCircle(circle, axis);

            double overlap = Math.min(polygonProjection[1], circleProjection[1]) - Math.max(polygonProjection[0], circleProjection[0]);
            if (overlap <= 0) {
                return false;
            }

            if (overlap < minOverlap) {
                minOverlap = overlap;
                mtvAxis = axis;
            }
        }

        Vec2d direction = circle.getPosition().minus(this.getPosition());
        if (mtvAxis.dot(direction) < 0) {
            mtvAxis = mtvAxis.smult(-1);
        }

        Vec2d mtv = mtvAxis.smult(minOverlap);

        // Apply MTV based on whether objects are static or dynamic
        if (!this.isStatic && !circle.isStatic()) {
            this.transform.setPosition(getTransformPosition().plus(mtv.smult(0.5)));
            circle.transform.setPosition(circle.getTransformPosition().minus(mtv.smult(0.5)));
        } else if (!this.isStatic) {
            this.transform.setPosition(getTransformPosition().plus(mtv));
        } else if (!circle.isStatic()) {
            circle.transform.setPosition(circle.getTransformPosition().minus(mtv));
        }

        Vec2d collisionNormal = mtv.normalize();

        Vec2d relativeVelocity = circle.physics.getVelocity().minus(this.physics.getVelocity());
        double velocityAlongNormal = relativeVelocity.dot(collisionNormal);

        if (velocityAlongNormal > 0) {
            return false;
        }

        double restitution = Math.sqrt(this.getRestitution() * circle.getRestitution());
        double impulseMagnitude;

        if (this.isStatic) {
            impulseMagnitude = -(1 + restitution) * velocityAlongNormal / (1 / circle.physics.getMass());
        } else if (circle.isStatic()) {
            impulseMagnitude = -(1 + restitution) * velocityAlongNormal / (1 / this.physics.getMass());
        } else {
            impulseMagnitude = -(1 + restitution) * velocityAlongNormal;
            impulseMagnitude /= (1 / this.physics.getMass() + 1 / circle.physics.getMass());
        }

        Vec2d impulse = collisionNormal.smult(impulseMagnitude);
        if (!this.isStatic) {
            this.physics.applyImpulse(impulse.smult(-1));
        }
        if (!circle.isStatic()) {
            circle.physics.applyImpulse(impulse);
        }

        return true;
    }


    // Helper method to find the closest point on a polygon to a given point
    private Vec2d findClosestPoint(Vec2d point, List<Vec2d> vertices) {
        Vec2d closestPoint = null;
        double minDistance = Double.MAX_VALUE;

        // Iterate over each edge of the polygon
        for (int i = 0; i < vertices.size(); i++) {
            Vec2d v1 = vertices.get(i);
            Vec2d v2 = vertices.get((i + 1) % vertices.size());

            // Get the closest point on the line segment v1-v2 to the given point
            Vec2d closestOnEdge = closestPointOnLineSegment(v1, v2, point);
            double distance = closestOnEdge.dist2(point); // Use squared distance for efficiency

            // Update the closest point if this one is closer
            if (distance < minDistance) {
                minDistance = distance;
                closestPoint = closestOnEdge;
            }
        }

        return closestPoint;
    }

    // Helper method to find the closest point on a line segment to a given point
    private Vec2d closestPointOnLineSegment(Vec2d v1, Vec2d v2, Vec2d point) {
        Vec2d line = v2.minus(v1);
        double lengthSquared = line.dot(line);

        // Handle degenerate case where the segment is a point
        if (lengthSquared == 0) {
            return v1;
        }

        // Project the point onto the line, clamping t to the [0, 1] range
        double t = Math.max(0, Math.min(1, point.minus(v1).dot(line) / lengthSquared));
        return v1.plus(line.smult(t));
    }


    private boolean collidesWithBox(BoxColliderComponent box) {
        int numAxes = 2 + this.getNumPoints();
        Vec2d[] axes = new Vec2d[numAxes];
        int axisIndex = 0;

        axes[axisIndex++] = new Vec2d(1, 0);
        axes[axisIndex++] = new Vec2d(0, 1);

        // Convert this polygon's vertices to world coordinates
        List<Vec2d> thisWorldVertices = new ArrayList<>();
        Vec2d thisTopLeft = this.transform.getPosition();
        for (Vec2d vertex : this.vertices) {
            thisWorldVertices.add(vertex.plus(thisTopLeft));
        }

        // Get axes from this polygon's edges
        for (int i = 0; i < thisWorldVertices.size(); i++) {
            Vec2d p1 = thisWorldVertices.get(i);
            Vec2d p2 = thisWorldVertices.get((i + 1) % thisWorldVertices.size());
            Vec2d edge = p2.minus(p1);
            Vec2d normal = new Vec2d(-edge.y, edge.x).normalize();
            axes[axisIndex++] = normal;
        }

        double minOverlap = Double.MAX_VALUE;
        Vec2d mtvAxis = null;

        for (Vec2d axis : axes) {
            double[] polygonProjection = projectPolygon(thisWorldVertices, axis);
            double[] aabbProjection = projectAABB(box, axis);

            double overlap = Math.min(polygonProjection[1], aabbProjection[1]) - Math.max(polygonProjection[0], aabbProjection[0]);
            if (overlap <= 0) {
                return false;
            }

            if (overlap < minOverlap) {
                minOverlap = overlap;
                mtvAxis = axis;
            }
        }

        Vec2d direction = box.getPosition().minus(this.getPosition());
        if (mtvAxis.dot(direction) < 0) {
            mtvAxis = mtvAxis.smult(-1);
        }

        Vec2d mtv = mtvAxis.smult(minOverlap);

        // Apply MTV based on whether objects are static or dynamic
        if (!this.isStatic && !box.isStatic()) {
            this.transform.setPosition(getTransformPosition().plus(mtv.smult(-0.5)));
            box.transform.setPosition(box.getTransformPosition().minus(mtv.smult(-0.5)));
        } else if (!this.isStatic) {
            this.transform.setPosition(getTransformPosition().plus(mtv.smult(-1)));
        } else if (!box.isStatic()) {
            box.transform.setPosition(box.getTransformPosition().minus(mtv.smult(-1)));
        }

        Vec2d collisionNormal = mtv.normalize();

        Vec2d relativeVelocity = box.physics.getVelocity().minus(this.physics.getVelocity());
        double velocityAlongNormal = relativeVelocity.dot(collisionNormal);

        if (velocityAlongNormal > 0) {
            return false;
        }

        double restitution = Math.sqrt(this.getRestitution() * box.getRestitution());
        double impulseMagnitude;

        if (this.isStatic) {
            impulseMagnitude = -(1 + restitution) * velocityAlongNormal / (1 / box.physics.getMass());
        } else if (box.isStatic()) {
            impulseMagnitude = -(1 + restitution) * velocityAlongNormal / (1 / this.physics.getMass());
        } else {
            impulseMagnitude = -(1 + restitution) * velocityAlongNormal;
            impulseMagnitude /= (1 / this.physics.getMass() + 1 / box.physics.getMass());
        }

        Vec2d impulse = collisionNormal.smult(impulseMagnitude);
        if (!this.isStatic) {
            this.physics.applyImpulse(impulse.smult(-1));
        }
        if (!box.isStatic()) {
            box.physics.applyImpulse(impulse);
        }

        return true;
    }

    private boolean collidesWithRay(RayColliderComponent ray){
        return ray.collidesWith(this);
    }


    @Override
    public Vec2d getPosition() {
        // Get the top-left position of the polygon from the transform component
        Vec2d topLeft = transform.getPosition();

        // Calculate the average of all vertices relative to the top-left position to find the center offset
        Vec2d centerOffset = new Vec2d(0, 0);
        for (Vec2d vertex : vertices) {
            centerOffset = centerOffset.plus(vertex);
        }

        // Add the center offset to the top-left position to get the center position of the polygon
        return topLeft.plus(centerOffset.smult(1.0 / vertices.size()));
    }


    public Vec2d getTransformPosition(){
        return transform.getPosition();
    }

    @Override
    public void setPosition(Vec2d position) {
    }

    @Override
    public ColliderType getType() {
        return ColliderType.POLYGON;
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

    // Helper method to project a BoxColliderComponent onto an axis
    private double[] projectAABB(BoxColliderComponent box, Vec2d axis) {
        // Get the center of the AABB from its position
        Vec2d center = box.getPosition();
        Vec2d halfSize = box.getSize().smult(0.5);

        // Calculate the corners of the AABB based on the center
        Vec2d[] corners = new Vec2d[]{
                center.minus(halfSize), // Top-left
                center.plus(new Vec2d(halfSize.x, -halfSize.y)), // Top-right
                center.plus(halfSize), // Bottom-right
                center.plus(new Vec2d(-halfSize.x, halfSize.y)) // Bottom-left
        };

        // Project each corner onto the axis and find the min and max projections
        double min = corners[0].dot(axis);
        double max = min;
        for (int i = 1; i < corners.length; i++) {
            double projection = corners[i].dot(axis);
            min = Math.min(min, projection);
            max = Math.max(max, projection);
        }
        return new double[]{min, max};
    }


    // Helper method to project a PolygonColliderComponent onto an axis
    private double[] projectPolygon(List<Vec2d> vertices, Vec2d axis) {
        // Project each vertex of the polygon onto the axis
        double min = vertices.get(0).dot(axis);
        double max = min;
        for (int i = 1; i < vertices.size(); i++) {
            double projection = vertices.get(i).dot(axis);
            min = Math.min(min, projection);
            max = Math.max(max, projection);
        }
        return new double[]{min, max};
    }

    // Helper method to project a CircleColliderComponent onto an axis
    private double[] projectCircle(CircleColliderComponent circle, Vec2d axis) {
        Vec2d center = circle.getPosition();
        double radius = circle.getRadius();
        double centerProjection = center.dot(axis);

        // Return the range of projections by adding and subtracting the radius
        return new double[]{centerProjection - radius, centerProjection + radius};
    }

    public int getNumPoints() {
        return vertices.size();
    }

    public Vec2d getPoint(int i) {
        return vertices.get(i).plus(transform.getPosition());
    }

}