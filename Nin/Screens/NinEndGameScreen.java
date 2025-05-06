package Nin.Screens;

import engine.Application;
import engine.UIkit.UIText;
import engine.screens.EndGameScreen;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class NinEndGameScreen extends EndGameScreen {

    private UIText successText;
    private UIText instructionText;
    private double alpha = 1.0;  // Initial alpha value
    private boolean increasingAlpha = false;  // Direction of alpha change


    public NinEndGameScreen(Application app) {
        super(app);
    }

    @Override
    public void onStartUp(Vec2d currentStageSize) {
        super.onStartUp(currentStageSize);

        // Create success title
        successText = new UIText(currentStageSize.x / 2, currentStageSize.y / 2 - 100, "Success!");
        successText.setFont(new Font("Arial", 50));
        successText.setColor(Color.GREEN);
        successText.setTextAlignment(TextAlignment.CENTER);

        // Create instruction text
        instructionText = new UIText(currentStageSize.x / 2, currentStageSize.y / 2, "Press Any Key to Start a New Game");
        instructionText.setFont(new Font("Arial", 30));
        instructionText.setColor(Color.BLACK);
        instructionText.setTextAlignment(TextAlignment.CENTER);

        // Add texts to the EndGameScreen UI elements
        gameUIWorld.addUIElementToInGame(successText);
        gameUIWorld.addUIElementToInGame(instructionText);
    }

    @Override
    public void onTick(long nanosSincePreviousTick) {
        super.onTick(nanosSincePreviousTick);
        // Convert nanoseconds to seconds
        double deltaTime = nanosSincePreviousTick / 1_000_000_000.0;

        // Adjust the alpha value over time
        if (increasingAlpha) {
            alpha += deltaTime * 0.7;  // Increase alpha over time (fades in)
            if (alpha >= 1.0) {
                alpha = 1.0;
                increasingAlpha = false;  // Start decreasing next
            }
        } else {
            alpha -= deltaTime * 0.7;  // Decrease alpha over time (fades out)
            if (alpha <= 0.0) {
                alpha = 0.0;
                increasingAlpha = true;  // Start increasing next
            }
        }

        // Update the alpha of the instruction text
        instructionText.setAlpha(alpha);
    }


    @Override
    public void onDraw(GraphicsContext g) {
        g.setFill(Color.LIGHTBLUE);  // set the background color
        g.fillRect(0, 0, currentStageSize.x, currentStageSize.y);
        super.onDraw(g);
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        super.onKeyPressed(e);
        app.changeScreen(app.getAllScreens().get(0));
    }

}
