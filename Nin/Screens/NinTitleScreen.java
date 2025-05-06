package Nin.Screens;

import Nin.UIElementStorageClasses.NinInGameUIWorld;
import engine.Application;
import engine.UIkit.UIButton;
import engine.UIkit.UIText;
import engine.screens.TitleScreen;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class NinTitleScreen extends TitleScreen {

    private UIText titleText;
    private UIButton playButton;
    private UIButton quitButton;
    private UIButton continueButton;
    private NinInGameScreen ninInGameScreen;

    public NinTitleScreen(Application app, NinInGameScreen screen) {
        super(app);
        ninInGameScreen = screen;
    }

    @Override
    public void onStartUp(Vec2d currentStageSize) {
        super.onStartUp(currentStageSize);
        gameUIWorld = new NinInGameUIWorld();

        double buttonX = (currentStageSize.x - 200) / 2;// in the center of the screen

        // Create Play Button
        playButton = new UIButton(buttonX, currentStageSize.y / 2 + 50, 200, 50, "Play");
        playButton.setFont(new Font("Verdana", 25));
        playButton.setTextAlignment(TextAlignment.CENTER);
        playButton.setColor(Color.LIMEGREEN);
        playButton.setTextColor(Color.WHITE);

        // Create Continue Button
        continueButton = new UIButton(buttonX, currentStageSize.y / 2 + 100, 200, 50, "Continue");
        continueButton.setFont(new Font("Verdana", 25));
        continueButton.setTextAlignment(TextAlignment.CENTER);
        continueButton.setColor(Color.DODGERBLUE);
        continueButton.setTextColor(Color.WHITE);

        // Create Quit Button
        quitButton = new UIButton(buttonX, currentStageSize.y / 2 + 150, 200, 50, "Quit");
        quitButton.setFont(new Font("Verdana", 25));
        quitButton.setTextAlignment(TextAlignment.CENTER);
        quitButton.setColor(Color.ORANGE);
        quitButton.setTextColor(Color.WHITE);

        // Create Title Text
        titleText = new UIText(currentStageSize.x / 2, currentStageSize.y / 2 - 100, "Nin");
        titleText.setFont(new Font("Arial", 50));
        titleText.setColor(Color.BLACK);
        titleText.setTextAlignment(TextAlignment.CENTER);

        // Add elements to the UI
        gameUIWorld.addUIElementToInGame(titleText);
        gameUIWorld.addUIElementToInGame(playButton);
        gameUIWorld.addUIElementToInGame(continueButton);
        gameUIWorld.addUIElementToInGame(quitButton);
    }

    @Override
    public void onDraw(GraphicsContext g) {
        g.setFill(Color.LIGHTBLUE);  // set the background color
        g.fillRect(0, 0, currentStageSize.x, currentStageSize.y);
        super.onDraw(g);
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        super.onMouseClicked(e);
        if (playButton.handleClicked(e.getX(), e.getY())) { // if the play button is clicked and is title screen
            // change to the in game screen
            app.changeScreen(app.getAllScreens().get(1));
            ninInGameScreen.startGame();
        }

        if (continueButton.handleClicked(e.getX(), e.getY())) {
            // Load the saved game state before transitioning to the game screen
            app.changeScreen(app.getAllScreens().get(1));
            ninInGameScreen.loadGame();
        }

        if (quitButton.handleClicked(e.getX(), e.getY())) { // if the quit button is clicked and is title screen
            app.shutdown();  // quit game
        }

    }

}
