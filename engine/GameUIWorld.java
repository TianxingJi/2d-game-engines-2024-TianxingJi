package engine;

import engine.UIkit.UIElement;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;
public class GameUIWorld {
    protected List<UIElement> inGameUIElements;  // UI Elements for in-game screen

    public GameUIWorld() {
        // Initialize UI element lists
        this.inGameUIElements = new ArrayList<>();
    }

    public List<UIElement> getInGameUIElements() {
        return inGameUIElements;
    }


    public void addUIElementToInGame(UIElement element) {inGameUIElements.add(element);}

    public void removeUIElementFromInGame(UIElement element) {
        inGameUIElements.remove(element);
    }

    // Method to update systems, called every frame
    public void onTick(long nanosSincePreviousTick) {

    }

    // Render all game objects and UI elements
    public void onDraw(GraphicsContext g) {
        for (UIElement uiElement : inGameUIElements) {
            uiElement.onDraw(g);
        }
    }
}
