package engine.gameobjects;

import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameObject {
    private int id;
    private final List<Component> components = new ArrayList<>();  // Stores components
    private int zIndex = 0; // z-index to control render order, default is 0

    public GameObject(int id) {
        this.id = id;
    }

    // Adds a component to the game object
    public void addComponent(Component component) {
        components.add(component);
    }

    // Removes a component from the game object
    public void removeComponent(Component component) {
        components.remove(component);
    }

    // 泛型方法来获取特定类型的组件
    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component component : components) {
            if (componentClass.isInstance(component)) {
                return componentClass.cast(component);  // 类型转换
            }
        }
        return null;
    }

    // Gets the z-index
    public int getZIndex() {
        return zIndex;
    }

    // Sets the z-index
    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    // Calls onTick for each component to update their state
    public void onTick(long nanosSincePreviousTick) {
        for (Component component : components) {
            component.onTick(nanosSincePreviousTick);
        }
    }

    // Calls onDraw for each component to render them
    public void onDraw(GraphicsContext g) {
        for (Component component : components) {
            component.onDraw(g);
        }
    }

    public List<Component> getComponents() {
        return components;
    }

    public String getId() {
        return Integer.toString(id);
    }

}
