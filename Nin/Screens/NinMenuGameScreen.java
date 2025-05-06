package Nin.Screens;

import Nin.UIElementStorageClasses.NinInGameUIWorld;
import engine.Application;
import engine.UIkit.UIButton;
import engine.UIkit.UIText;
import engine.screens.EndGameScreen;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class NinMenuGameScreen extends EndGameScreen {

    private NinInGameScreen ninInGameScreen;

    private UIButton controlButton;
    private UIButton volumeButton;
    private UIButton restartButton;
    private UIButton saveButton;
    private UIButton loadButton;
    private UIButton backButton;
    private UIButton quitButton;
    private UIText instructions;

    public NinMenuGameScreen(Application app, NinInGameScreen screen) {
        super(app);
        this.ninInGameScreen = screen;
    }

    @Override
    public void onStartUp(Vec2d currentStageSize) {
        super.onStartUp(currentStageSize);

        gameUIWorld = new NinInGameUIWorld();

        double buttonX = (currentStageSize.x - 200) / 2;// in the center of the screen

        instructions = new UIText(buttonX + 100, currentStageSize.y / 4 - 50, "Volume control feature is not implemented yet");
        instructions.setFont(new Font("Verdana", 20));
        instructions.setColor(Color.BLACK);
        instructions.setTextAlignment(TextAlignment.CENTER);

        controlButton = new UIButton(buttonX, currentStageSize.y / 4, 200, 50, "Controls");
        controlButton.setFont(new Font("Verdana", 25));
        controlButton.setColor(Color.CYAN);
        controlButton.setTextColor(Color.WHITE);
        controlButton.setTextAlignment(TextAlignment.CENTER);

        volumeButton = new UIButton(buttonX, currentStageSize.y / 4 + 50, 200, 50, "Volume");
        volumeButton.setFont(new Font("Verdana", 25));
        volumeButton.setColor(Color.LIGHTGREEN);
        volumeButton.setTextColor(Color.WHITE);
        volumeButton.setTextAlignment(TextAlignment.CENTER);

        restartButton = new UIButton(buttonX, currentStageSize.y / 4 + 100, 200, 50, "Restart");
        restartButton.setFont(new Font("Verdana", 25));
        restartButton.setTextAlignment(TextAlignment.CENTER);
        restartButton.setColor(Color.LIMEGREEN);
        restartButton.setTextColor(Color.WHITE);

        saveButton = new UIButton(buttonX, currentStageSize.y / 4 + 150, 200, 50, "Save");
        saveButton.setFont(new Font("Verdana", 25));
        saveButton.setTextAlignment(TextAlignment.CENTER);
        saveButton.setColor(Color.DODGERBLUE);
        saveButton.setTextColor(Color.WHITE);

        loadButton = new UIButton(buttonX, currentStageSize.y / 4 + 200, 200, 50, "Load");
        loadButton.setFont(new Font("Verdana", 25));
        loadButton.setTextAlignment(TextAlignment.CENTER);
        loadButton.setColor(Color.ORANGE);
        loadButton.setTextColor(Color.WHITE);

        backButton = new UIButton(buttonX, currentStageSize.y / 4 + 250, 200, 50, "Title Screen");
        backButton.setFont(new Font("Verdana", 25));
        backButton.setTextAlignment(TextAlignment.CENTER);
        backButton.setColor(Color.LIGHTGOLDENRODYELLOW);
        backButton.setTextColor(Color.WHITE);

        quitButton = new UIButton(buttonX, currentStageSize.y / 4 + 300, 200, 50, "Quit");
        quitButton.setFont(new Font("Verdana", 25));
        quitButton.setTextAlignment(TextAlignment.CENTER);
        quitButton.setColor(Color.CORNFLOWERBLUE);
        quitButton.setTextColor(Color.WHITE);

        // 将按钮添加到UI中
        gameUIWorld.addUIElementToInGame(instructions);
        gameUIWorld.addUIElementToInGame(controlButton);
        gameUIWorld.addUIElementToInGame(volumeButton);
        gameUIWorld.addUIElementToInGame(restartButton);
        gameUIWorld.addUIElementToInGame(saveButton);
        gameUIWorld.addUIElementToInGame(loadButton);
        gameUIWorld.addUIElementToInGame(backButton);
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

        if (controlButton.handleClicked(e.getX(), e.getY())) { // if the play button is clicked and is title screen
            // change to the in game screen
            app.changeScreen(app.getAllScreens().get(1));
            ninInGameScreen.controlChange();
        }

        if (volumeButton.handleClicked(e.getX(), e.getY())) {
            // Future logic for volume control goes here.
        }

        if (restartButton.handleClicked(e.getX(), e.getY())) { // if the play button is clicked and is title screen
            // change to the in game screen
            app.changeScreen(app.getAllScreens().get(1));
            ninInGameScreen.startGame();
        }

        if (saveButton.handleClicked(e.getX(), e.getY())) {
            // Load the saved game state before transitioning to the game screen
            app.changeScreen(app.getAllScreens().get(1));
            ninInGameScreen.saveGame();
        }

        if (loadButton.handleClicked(e.getX(), e.getY())) { // if the quit button is clicked and is title screen
            app.changeScreen(app.getAllScreens().get(1));
            ninInGameScreen.loadGame();
        }

        if (backButton.handleClicked(e.getX(), e.getY())) { // if the play button is clicked and is title screen
            // change to the in game screen
            app.changeScreen(app.getAllScreens().get(0));
        }

        if (quitButton.handleClicked(e.getX(), e.getY())) { // if the play button is clicked and is title screen
            // change to the in game screen
            app.shutdown();
        }

    }

}
