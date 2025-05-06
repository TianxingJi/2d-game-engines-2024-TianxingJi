package engine.gameobjects;

import javafx.scene.canvas.GraphicsContext;

public interface Component {
    void onTick(long nanosSincePreviousTick);
    void onDraw(GraphicsContext g);
}
