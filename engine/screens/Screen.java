package engine.screens;

import engine.Application;
import engine.GameUIWorld;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public abstract class Screen {
    protected Application app;

    protected Vec2d currentStageSize;
    protected Vec2d originalStageSize;

    protected GameUIWorld gameUIWorld;

    public Screen(Application app) {
        this.app = app;
    }

    public void onStartUp(Vec2d currentStageSize){
        this.currentStageSize = currentStageSize;
    };

    public abstract void onDraw(GraphicsContext g);

    public abstract void onTick(long nanosSincePreviousTick);

    public abstract void onKeyReleased(KeyEvent e);

    public abstract void onKeyPressed(KeyEvent e);

    public abstract void onMouseClicked(MouseEvent e);

    public abstract void onMouseMoved(MouseEvent e);

    public abstract void onMousePressed(MouseEvent e);

    public abstract void onMouseDragged(MouseEvent e);

    public abstract void onMouseReleased(MouseEvent e);

    public abstract void onMouseWheelMoved(ScrollEvent e);

    public abstract void onResize(Vec2d newSize, Vec2d oldSize);
}
