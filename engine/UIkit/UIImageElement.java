package engine.UIkit;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class UIImageElement extends UIElement {
    private Image image;
    private String imagePath;

    public UIImageElement(double x, double y, double width, double height, String imagePath) {
        super(x, y, width, height);
        this.image = new Image(getClass().getResource(imagePath).toString());
        this.imagePath = imagePath;
    }


    @Override
    public void onDraw(GraphicsContext g) {
        g.drawImage(image, x, y, width, height);
    }

    public String getImagePath() {
        return imagePath;
    }

    public Image getImage() {
        return image;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // if the same object
        if (obj == null || getClass() != obj.getClass()) return false; // check the class
        UIImageElement other = (UIImageElement) obj; // convert type
        return this.imagePath.equals(other.imagePath); // compare image path
    }

    // hash the image path
    @Override
    public int hashCode() {
        return imagePath.hashCode();
    }
}
