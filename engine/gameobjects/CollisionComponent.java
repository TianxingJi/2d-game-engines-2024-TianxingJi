package engine.gameobjects;

import engine.support.Vec2d;

public interface CollisionComponent extends Component {
    // Enum for collider types to improve clarity
    enum ColliderType {
        CIRCLE, BOX, POLYGON, RAY
    }
    // Synchronize the collider position with the TransformComponent
    void syncWithTransform(TransformComponent transform, double resizeScaleX, double resizeScaleY);

    // Check if this component collides with another component
    boolean collidesWith(CollisionComponent other);

    // Get the position of the collider
    Vec2d getPosition();

    // Set the position of the collider
    void setPosition(Vec2d position);

    // Get the type of the collider (useful for debugging or future extensions)
    ColliderType getType();

    // New method to disable or enable the collider
    boolean isActive();
    void setActive(boolean active);
}
