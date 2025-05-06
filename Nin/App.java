package Nin;

import Nin.Screens.NinEndGameScreen;
import Nin.Screens.NinInGameScreen;
import Nin.Screens.NinMenuGameScreen;
import Nin.Screens.NinTitleScreen;
import engine.UIkit.ViewPort;
import engine.gameobjects.*;
import engine.Application;
import Nin.gameCharacter;
import engine.mapgeneration.DungeonLoader;
import engine.mapgeneration.Tile;
import engine.screens.Screen;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import javax.swing.text.html.HTML;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/**
 * This is your Tic-Tac-Toe top-level, App class.
 * This class will contain every other object in your game.
 */
public class App extends Application {

  private NinInGameScreen ninInGameScreen = new NinInGameScreen(this);
  private NinTitleScreen ninTitleScreen = new NinTitleScreen(this, ninInGameScreen);
  private NinEndGameScreen ninEndGameScreen = new NinEndGameScreen(this);
  private NinMenuGameScreen ninMenuGameScreen = new NinMenuGameScreen(this, ninInGameScreen);

  public App(String title) {
    super(title);
  }

  public App(String title, Vec2d windowSize, boolean debugMode, boolean fullscreen) {
    super(title, windowSize, debugMode, fullscreen);
  }

  @Override
  protected void onStartup() {
    super.onStartup();
    ninInGameScreen.onStartUp(currentStageSize);
    ninTitleScreen.onStartUp(currentStageSize);
    ninEndGameScreen.onStartUp(currentStageSize);
    ninMenuGameScreen.onStartUp(currentStageSize);
    addScreen(ninTitleScreen);
    addScreen(ninInGameScreen);
    addScreen(ninEndGameScreen);
    addScreen(ninMenuGameScreen);
    currentScreen = ninTitleScreen;
  }

  @Override
  protected void onTick(long nanosSincePreviousTick) {
    super.onTick(nanosSincePreviousTick);
  }

  @Override
  protected void onDraw(GraphicsContext g) {
    super.onDraw(g);
  }

  @Override
  public void onKeyPressed(KeyEvent e) {
    super.onKeyPressed(e);
  }

  @Override
  protected void onKeyReleased(KeyEvent e) {
    super.onKeyReleased(e);
  }

  @Override
  protected void onMousePressed(MouseEvent e) {
    super.onMousePressed(e);
  }

  @Override
  protected void onMouseReleased(MouseEvent e) {
    super.onMouseReleased(e);
  }


}
