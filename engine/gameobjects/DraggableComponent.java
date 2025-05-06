package engine.components;

import engine.gameobjects.Component;
import engine.gameobjects.GameObject;
import javafx.scene.canvas.GraphicsContext;

public class DraggableComponent implements Component {

    private GameObject gameObject;
    private boolean isDragging = false;

    public DraggableComponent(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    // 设置是否正在拖拽
    public void setDragging(boolean isDragging) {
        this.isDragging = isDragging;
    }

    public boolean isDragging() {
        return isDragging;
    }

    // 获取 GameObject 对象
    public GameObject getGameObject() {
        return gameObject;
    }

    @Override
    public void onTick(long nanosSincePreviousTick) {

    }

    @Override
    public void onDraw(GraphicsContext g) {

    }
}
