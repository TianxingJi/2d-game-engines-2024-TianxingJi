package engine;

import engine.UIkit.UIElement;
import engine.screens.EndGameScreen;
import engine.screens.InGameScreen;
import engine.screens.Screen;
import engine.screens.TitleScreen;
import engine.support.FXFrontEnd;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.lang.System;
import java.util.ArrayList;
import java.util.List;

/**
 * This is your main Application class that you will contain your
 * 'draws' and 'ticks'. This class is also used for controlling
 * user input.
 */
public class Application extends FXFrontEnd {

  protected Screen currentScreen;
  protected Vec2d originalStageSize = DEFAULT_STAGE_SIZE;
  // Declare the systems the application will use
  // List to hold all screens
  private List<Screen> allScreens;

  public Application(String title) {
    super(title);
  }
  public Application(String title, Vec2d windowSize, boolean debugMode, boolean fullscreen) {
    super(title, windowSize, debugMode, fullscreen);
  }

  /**
   * Called periodically and used to update the state of your game.
   * @param nanosSincePreviousTick	approximate number of nanoseconds since the previous call
   */
  @Override
  protected void onTick(long nanosSincePreviousTick) {
    if (currentScreen != null) {
      currentScreen.onTick(nanosSincePreviousTick);  // 更新当前屏幕逻辑
    }
  }

  /**
   * Called after onTick().
   */
  @Override
  protected void onLateTick() {
    // Don't worry about this method until you need it. (It'll be covered in class.)
  }

  /**
   *  Called periodically and meant to draw graphical components.
   * @param g		a {@link GraphicsContext} object used for drawing.
   */
  @Override
  protected void onDraw(GraphicsContext g) {
    if (currentScreen != null) {
      currentScreen.onDraw(g);
    }
  }

  /**
   * Called when a key is typed.
   * @param e		an FX {@link KeyEvent} representing the input event.
   */
  @Override
  protected void onKeyTyped(KeyEvent e) {

  }

  /**
   * Called when a key is pressed.
   * @param e		an FX {@link KeyEvent} representing the input event.
   */
  @Override
  public void onKeyPressed(KeyEvent e) {
    // Gradually increase the pan level when the key is held
    if (currentScreen != null) {
      currentScreen.onKeyPressed(e);  // call the current screen to handle key press events
    }
  }

  /**
   * Called when a key is released.
   * @param e		an FX {@link KeyEvent} representing the input event.
   */
  @Override
  protected void onKeyReleased(KeyEvent e) {
    if (currentScreen != null) {
      currentScreen.onKeyReleased(e);  // call the current screen to handle key press events
    }
  }

  /**
   * Called when the mouse is clicked.
   * @param e		an FX {@link MouseEvent} representing the input event.
   */
  @Override
  protected void onMouseClicked(MouseEvent e) {
    if (currentScreen != null) {
      currentScreen.onMouseClicked(e);  // call the current screen to solve all the mouse clicked events
    }
  }

  /**
   * Called when the mouse is pressed.
   * @param e		an FX {@link MouseEvent} representing the input event.
   */
  @Override
  protected void onMousePressed(MouseEvent e) {
    if (currentScreen != null) {
      currentScreen.onMousePressed(e);  // call the current screen to solve all the mouse pressed events
    }
  }

  /**
   * Called when the mouse is released.
   * @param e		an FX {@link MouseEvent} representing the input event.
   */
  @Override
  protected void onMouseReleased(MouseEvent e) {
    if (currentScreen != null) {
      currentScreen.onMouseReleased(e);  // call the current screen to solve all the mouse released events
    }
  }

  /**
   * Called when the mouse is dragged.
   * @param e		an FX {@link MouseEvent} representing the input event.
   */
  @Override
  protected void onMouseDragged(MouseEvent e) {
    if (currentScreen != null) {
      currentScreen.onMouseDragged(e);  // call the current screen to handle key press events
    }
  }

  /**
   * Called when the mouse is moved.
   * @param e		an FX {@link MouseEvent} representing the input event.
   */
  @Override
  protected void onMouseMoved(MouseEvent e) {
    if (currentScreen != null) {
      currentScreen.onMouseMoved(e);  // call the current screen to solve all the on mouse pressed events
    }
  }

  /**
   * Called when the mouse wheel is moved.
   * @param e		an FX {@link ScrollEvent} representing the input event.
   */
  @Override
  protected void onMouseWheelMoved(ScrollEvent e) {
    if (currentScreen != null) {
      currentScreen.onMouseWheelMoved(e);  // call the current screen to handle key press events
    }
  }

  /**
   * Called when the window's focus is changed.
   * @param newVal	a boolean representing the new focus state
   */
  @Override
  protected void onFocusChanged(boolean newVal) {

  }

  /**
   * Called when the window is resized.
   * @param newSize	the new size of the drawing area.
   */
  @Override
  protected void onResize(Vec2d newSize) {
    currentStageSize = newSize;
    for (Screen screen : allScreens) {
      screen.onResize(newSize, originalStageSize);
    }
    originalStageSize = currentStageSize;
  }

  /**
   * Called when the app is shutdown.
   */
  @Override
  protected void onShutdown() {

  }

  /**
   * Called when the app is starting up.s
   */
  @Override
  protected void onStartup() {
    // Initialize the list of all screens
    allScreens = new ArrayList<>();
  }

  public void changeScreen(Screen newScreen) {
    this.currentScreen = newScreen;  // change to new screen
  }

  public List<Screen> getAllScreens() {
    return allScreens;
  }

  public void addScreen(Screen screen) {
    allScreens.add(screen);
  }
}
