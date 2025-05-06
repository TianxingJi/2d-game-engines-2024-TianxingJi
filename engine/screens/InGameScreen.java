package engine.screens;

import engine.Application;
import engine.GameUIWorld;
import engine.UIkit.*;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;

public class InGameScreen extends Screen {

    protected GameUIWorld gameUIWorld = new GameUIWorld();
    protected ViewPort viewPort;
    protected final Affine transform = new Affine();  // Store the transformation

    public InGameScreen(Application app) {
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
        viewPort.setPanLevel(1.0);
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        // Gradually increase the pan level when the key is held
        viewPort.setPanLevel(viewPort.getPanLevel() + 0.2);  // Increase pan level by 0.2 every time the key is pressed

        // Define the pan factor for smooth panning based on the zoom and pan level
        double panFactor = 10 * viewPort.getZoom();  // Multiply by zoom to make panning feel smoother at different zoom levels
        Vec2d panDelta = new Vec2d(0, 0);  // Initialize pan delta

        // Handle panning via arrow keys
        switch (e.getCode()) {
            case UP:
                panDelta = new Vec2d(0, -panFactor);  // Pan up
                break;
            case DOWN:
                panDelta = new Vec2d(0, panFactor);  // Pan down
                break;
            case LEFT:
                panDelta = new Vec2d(-panFactor, 0);  // Pan left
                break;
            case RIGHT:
                panDelta = new Vec2d(panFactor, 0);  // Pan right
                break;
        }

        // Call the pan method to update the viewport position
        viewPort.pan(panDelta);

        // Ensure the viewport doesn't pan outside of the current stage size
        Vec2d newPosition = clampViewPortPosition(viewPort.getPosition());
        viewPort.setPosition(newPosition);
        // press 'ESCAPE' to shut down the game
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
// Set the zoom limits
        double minZoom = 1.0;  // Minimum zoom level (100%)
        double maxZoom = 4.0;  // Maximum zoom level (400%)

        // Get the mouse position
        double mouseX = e.getX();
        double mouseY = e.getY();

        // Calculate the world position before zooming
        double worldXBeforeZoom = (mouseX / viewPort.getZoom()) + viewPort.getPosition().x;
        double worldYBeforeZoom = (mouseY / viewPort.getZoom()) + viewPort.getPosition().y;

        // Adjust zoom using the new method
        double deltaZoom = e.getDeltaY() > 0 ? 0.05 : -0.05;
        viewPort.adjustZoom(deltaZoom);

        // Clamp the zoom within the allowed limits
        if (viewPort.getZoom() < minZoom) {
            viewPort.setZoom(minZoom);
        } else if (viewPort.getZoom() > maxZoom) {
            viewPort.setZoom(maxZoom);
        }

        // Calculate the world position after zooming
        double worldXAfterZoom = (mouseX / viewPort.getZoom()) + viewPort.getPosition().x;
        double worldYAfterZoom = (mouseY / viewPort.getZoom()) + viewPort.getPosition().y;

        // Adjust the viewport position based on the difference in world position
        viewPort.setPosition(new Vec2d(
                viewPort.getPosition().x + (worldXBeforeZoom - worldXAfterZoom),
                viewPort.getPosition().y + (worldYBeforeZoom - worldYAfterZoom)
        ));

        // Ensure the viewport position stays within bounds after zooming
        viewPort.setPosition(clampViewPortPosition(viewPort.getPosition()));
    }

    @Override
    public void onResize(Vec2d newSize, Vec2d oldSize) {
        // Update the viewport size
        viewPort.setSize(new Vec2d(newSize.x, newSize.y));
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
            } else if (uiElement instanceof UIPanel) {
                UIPanel uiPanel = (UIPanel) uiElement;

                // Recursively adjust all the elements within the UIPanel
                for (UIElement panelElement : uiPanel.getElements()) {
                    // Adjust the position of the panel element
                    Vec2d panelElementPos = panelElement.getPosition();
                    Vec2d newPanelElementPos = new Vec2d(panelElementPos.x * resizeScaleX, panelElementPos.y * resizeScaleY);
                    panelElement.setPosition(newPanelElementPos.x, newPanelElementPos.y);

                    // If the panel element is a text or button, adjust its font size
                    if (panelElement instanceof UIText) {
                        UIText panelText = (UIText) panelElement;
                        double newFontSize = panelText.getFont().getSize() * resizeScaleAverage;
                        panelText.setFont(new Font(panelText.getFont().getName(), newFontSize));
                    } else if (panelElement instanceof UIButton) {
                        UIButton panelButton = (UIButton) panelElement;
                        double newFontSize = panelButton.getFont().getSize() * resizeScaleAverage;
                        panelButton.setFont(new Font(panelButton.getFont().getName(), newFontSize));
                    } else if (panelElement instanceof UIImageElement) {
                        UIImageElement panelImage = (UIImageElement) panelElement;
                        // Resize the image element according to the scaling factor
                        panelImage.setSize(panelImage.getWidth() * resizeScaleAverage, panelImage.getHeight() * resizeScaleAverage);
                    } else {
                        // Scale the size of the panel element
                        panelElement.setSize(panelElement.getWidth() * resizeScaleAverage, panelElement.getHeight() * resizeScaleAverage);
                    }
                }
            } else if (uiElement instanceof UIMap){
                UIMap uiMap = (UIMap) uiElement;
                uiMap.setTileSize(uiMap.getTileSize() * resizeScaleAverage);
            }
        }
    }

    // Utility method to clamp the viewport position within the stage size when zoom is 1.0
    private Vec2d clampViewPortPosition(Vec2d position) {
        // The world boundaries should be defined as if zoom is 1.0
        double worldWidth = currentStageSize.x;  // Width of the world at zoom = 1.0
        double worldHeight = currentStageSize.y; // Height of the world at zoom = 1.0

        // The visible portion of the world changes with zoom
        double visibleWidth = viewPort.getSize().x / viewPort.getZoom();
        double visibleHeight = viewPort.getSize().y / viewPort.getZoom();

        // Calculate the maximum panning positions
        double maxX = worldWidth - visibleWidth;
        double maxY = worldHeight - visibleHeight;

        // Ensure that maxX and maxY are non-negative to avoid over-restricting panning
        maxX = Math.max(0, maxX);
        maxY = Math.max(0, maxY);

        // Clamp the viewport's position so it doesn't go beyond the allowed range
        double clampedX = Math.max(0, Math.min(position.x, maxX));
        double clampedY = Math.max(0, Math.min(position.y, maxY));

        return new Vec2d(clampedX, clampedY);
    }


}
