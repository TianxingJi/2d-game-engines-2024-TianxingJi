package engine.gameobjects;

import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.List;

public class AnimationComponent extends SpriteComponent {
    private List<Vec2d> framePositions;  // List of positions of frames in the sprite sheet
    private int totalFrames;  // Total number of frames in the animation
    private int currentFrame = 0;  // The current frame being displayed
    private double frameDuration;  // Duration for each frame in seconds
    private double elapsedTime = 0;  // Elapsed time since last frame change
    private Vec2d frameSize;  // Size of each frame in the sprite sheet
    private Vec2d croppedFrameSize;  // Stores the cropped frame size

    // Constructor to initialize animation component with image and frame details
    public AnimationComponent(String imagePath, TransformComponent transform, int totalFrames, double frameDuration, Vec2d frameSize) {
        super(imagePath, transform);  // Call SpriteComponent constructor
        this.totalFrames = totalFrames;
        this.frameDuration = frameDuration;
        this.frameSize = frameSize;
    }

    // Constructor to initialize animation component with image and frame details
    public AnimationComponent(Image sprite, TransformComponent transform, int totalFrames, double frameDuration, Vec2d frameSize) {
        super(sprite, transform);  // Call SpriteComponent constructor
        this.totalFrames = totalFrames;
        this.frameDuration = frameDuration;
        this.frameSize = frameSize;
    }

    @Override
    public void onTick(long nanosSincePreviousTick) {
        // Convert time to seconds
        double deltaTime = nanosSincePreviousTick / 1_000_000_000.0;
        elapsedTime += deltaTime;

        // Update frame if enough time has passed
        if (elapsedTime >= frameDuration) {
            currentFrame = (currentFrame + 1) % totalFrames;  // Loop back to the first frame using modulo
            elapsedTime = 0;  // Reset the time for the next frame
        }
    }

    @Override
    public void onDraw(GraphicsContext g) {
        Vec2d position = transform.getPosition();
        Vec2d size = transform.getSize();

        // Calculate the x position of the current frame in the sprite sheet
        double frameX = currentFrame * frameSize.x;
        double frameY = 0;  // Assuming all frames are in a single row

        // Draw the current frame
        g.drawImage(
                sprite,  // The full sprite sheet
                frameX, frameY, frameSize.x, frameSize.y,  // Source: the specific frame in the sheet
                position.x, position.y, size.x, size.y  // Destination: where to draw it on the screen
        );
    }
}
