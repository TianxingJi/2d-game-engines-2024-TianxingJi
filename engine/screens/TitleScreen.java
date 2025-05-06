package engine.screens;

import engine.Application;
import engine.GameUIWorld;
import engine.UIkit.UIButton;
import engine.UIkit.UIElement;
import engine.UIkit.UIText;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.text.Font;

public class TitleScreen extends Screen {

    protected GameUIWorld gameUIWorld = new GameUIWorld();

    public TitleScreen(Application app) {
        super(app);
    }

    @Override
    public void onStartUp(Vec2d currentStageSize) {
        super.onStartUp(currentStageSize);
    }

    @Override
    public void onDraw(GraphicsContext g) {
        gameUIWorld.onDraw(g);
    }

    @Override
    public void onTick(long nanosSincePreviousTick) {
    }

    @Override
    public void onKeyReleased(KeyEvent e) {

    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        // shut down the game
        if (e.getCode().toString().equals("ESCAPE")) {
            app.shutdown();
        }
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
    }

    @Override
    public void onMouseMoved(MouseEvent e) {
        for (UIElement uiElement : gameUIWorld.getInGameUIElements()) {
            uiElement.handleHover(e.getX(), e.getY());
        }
    }

    public void onMousePressed(MouseEvent e) {
        for (UIElement uiElement : gameUIWorld.getInGameUIElements()) {
            uiElement.handlePressed(e.getX(), e.getY());
        }
    }

    @Override
    public void onMouseDragged(MouseEvent e) {

    }

    public void onMouseReleased(MouseEvent e) {
        for (UIElement uiElement : gameUIWorld.getInGameUIElements()) {
            uiElement.handleReleased(e.getX(), e.getY());
        }
    }

    @Override
    public void onMouseWheelMoved(ScrollEvent e) {

    }

    @Override
    public void onResize(Vec2d newSize, Vec2d oldSize) {
        // Compute scale factors for resizing, based on new size compared to original size
        double resizeScaleX = newSize.x / oldSize.x;
        double resizeScaleY = newSize.y / oldSize.y;
        double resizeScaleAverage = (resizeScaleX + resizeScaleY) / 2;
        this.currentStageSize = newSize;
        this.originalStageSize = oldSize;

        // Adjust the position, size, and font size of each UIElement
        for (UIElement uiElement : gameUIWorld.getInGameUIElements()) {
            // Adjust the position of the UI element
            Vec2d currentPos = uiElement.getPosition();
            Vec2d newPos = new Vec2d(currentPos.x * resizeScaleX, currentPos.y * resizeScaleY);
            uiElement.setPosition(newPos.x, newPos.y);

            // Scale the size of UI elements
            uiElement.setSize(uiElement.getWidth() * resizeScaleAverage, uiElement.getHeight() * resizeScaleAverage);

            // Adjust font size if the UIElement is a UIText or UIButton
            if (uiElement instanceof UIText) {
                UIText uiText = (UIText) uiElement;
                double newFontSize = uiText.getFont().getSize() * resizeScaleAverage;
                uiText.setFont(new Font(uiText.getFont().getName(), newFontSize)); // Set new scaled font size
            } else if (uiElement instanceof UIButton) {
                UIButton uiButton = (UIButton) uiElement;
                double newFontSize = uiButton.getFont().getSize() * resizeScaleAverage;
                uiButton.setFont(new Font(uiButton.getFont().getName(), newFontSize)); // Set new scaled font size
            }
        }
    }
}
