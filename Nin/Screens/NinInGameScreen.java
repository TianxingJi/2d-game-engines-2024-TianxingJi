package Nin.Screens;

import Nin.NinGameWorld;
import Nin.UIElementStorageClasses.NinInGameUIWorld;
import engine.Application;
import engine.UIkit.UIButton;
import engine.UIkit.UIText;
import engine.UIkit.ViewPort;
import engine.screens.InGameScreen;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class NinInGameScreen extends InGameScreen {

    private NinGameWorld ninGameWorld;
    private UIButton backButton;
    private UIButton menuButton;
    private UIText controlInstruction;

    public NinInGameScreen(Application app) {
        super(app);
    }

    @Override
    public void onStartUp(Vec2d currentStageSize) {
        super.onStartUp(currentStageSize);
        gameUIWorld = new NinInGameUIWorld();

        menuButton = new UIButton(50, 50, 80, 50, "Menu");
        menuButton.setFont(new Font("Verdana", 25));
        menuButton.setTextAlignment(TextAlignment.CENTER);
        menuButton.setColor(Color.GREENYELLOW);
        menuButton.setTextColor(Color.WHITE);

        // Initialize the control instruction text
        controlInstruction = new UIText(currentStageSize.x / 2, currentStageSize.y - 50, "Controls: Jump - W, Attack - SPACE");
        controlInstruction.setColor(Color.DARKBLUE);

        gameUIWorld.addUIElementToInGame(menuButton);
        gameUIWorld.addUIElementToInGame(controlInstruction);

        viewPort = new ViewPort(new Vec2d(0, 0), new Vec2d(0, 0));
        // Initialize the original view port
        viewPort.setPosition(new Vec2d(0, 0));
        viewPort.setSize(currentStageSize);

        ninGameWorld = new NinGameWorld(viewPort);
        ninGameWorld.onStartUp(currentStageSize);

        updateControlInstructions();
    }

    @Override
    public void onDraw(GraphicsContext g) {
        g.setFill(Color.LIGHTBLUE);  // set the background color
        g.fillRect(0, 0, currentStageSize.x, currentStageSize.y);
        g.save();  // Save the current state of the graphics context

        // Clip the drawing area to the fixed size of the viewport
        g.beginPath();
        g.rect(0, 0, viewPort.getSize().x, viewPort.getSize().y);  // Ensure clipping is fixed to the viewport size
        g.clip();  // Only render within the viewport bounds

        // Apply the ViewPort's translation and zoom to the content (not the viewport itself)
        transform.setToIdentity();  // Reset any previous transforms
        transform.appendScale(viewPort.getZoom(), viewPort.getZoom());  // Apply zoom to content
        transform.appendTranslation(-viewPort.getPosition().x, -viewPort.getPosition().y);  // Apply panning to content

        // Apply the affine transformation before drawing objects
        g.setTransform(transform);

        ninGameWorld.onDraw(g);

        g.restore();  // Restore the original state
        super.onDraw(g);
    }

    @Override
    public void onTick(long nanosSincePreviousTick) {
        super.onTick(nanosSincePreviousTick);
        ninGameWorld.onTick(nanosSincePreviousTick);

        if(ninGameWorld.reachDestination()){
            app.changeScreen(app.getAllScreens().get(2));
            ninGameWorld.setReachDestination(false);
        }
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        super.onMouseClicked(e);
        if(menuButton.handleClicked(e.getX(), e.getY())) {
            app.changeScreen(app.getAllScreens().get(3));
        }
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        super.onKeyPressed(e);
        ninGameWorld.onKeyPressed(e);
    }

    @Override
    public void onKeyReleased(KeyEvent e) {
        super.onKeyReleased(e);
        ninGameWorld.onKeyReleased(e);
    }

    @Override
    public void onMousePressed(MouseEvent e) {
        super.onMousePressed(e);
        ninGameWorld.onMousePressed(e);
    }

    @Override
    public void onMouseReleased(MouseEvent e) {
        super.onMouseReleased(e);
        ninGameWorld.onMouseReleased(e);
    }

    @Override
    public void onResize(Vec2d newSize, Vec2d oldSize) {
        super.onResize(newSize, oldSize);
        ninGameWorld.onResize(newSize, oldSize);
    }

    public void saveGame(){
        ninGameWorld.saveGame("Nin/save.xml");
    }

    public void loadGame(){
        ninGameWorld.loadGame("Nin/save.xml");
    }

    public void startGame(){
        ninGameWorld.loadGame("Nin/start.xml");
    }

    public void controlChange(){
        ninGameWorld.showKeyMappingOptions();
        updateControlInstructions();
    }

    private void updateControlInstructions() {
        String jumpKey = ninGameWorld.getKeyMapping("Jump").getName();
        String attackKey = ninGameWorld.getKeyMapping("Attack").getName();
        controlInstruction.setText("Controls: Jump - " + jumpKey + ", Attack - " + attackKey);
    }

}
