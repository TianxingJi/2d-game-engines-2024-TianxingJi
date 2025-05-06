package engine.gameobjects;

import javafx.scene.canvas.GraphicsContext;
import java.util.HashSet;
import java.util.Set;

public class TagComponent implements Component {
    private Set<String> tags = new HashSet<>();

    // Adds a tag to the object
    public void addTag(String tag) {
        tags.add(tag);
    }

    // Removes a tag from the object
    public void removeTag(String tag) {
        tags.remove(tag);
    }

    // Checks if the object has a specific tag
    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    public Set<String> getTags() {
        return tags;
    }

    @Override
    public void onTick(long nanosSincePreviousTick) {
        // No need to update anything for tags
    }

    @Override
    public void onDraw(GraphicsContext g) {
        // No need to render anything for tags
    }
}