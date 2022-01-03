package game;

import maths.*;
import core.*;

/**
 * Diese Klasse modelliert den Pausebildschirm
 * 
 * @author Sven Schreiber
 * @version 03.01.2022
 */
public class PauseScene extends Scene
{
    private GameScene _gameScene;
    private Camera _camera;
    
    public PauseScene(GameState state, GameScene gameScene)
    {
        super(state);
        
        _gameScene = gameScene;
        
        _camera = new Camera();
        _camera.setPosition(new Vector3(0.0, 2, 5.0));
        _camera.setFov(30.0);
    }
    
    /**
     * @see Scene#handleInput()
     */
    public void handleInput(double deltaTime, double runTime)
    {
        return;
    }
    
    /**
     * @see Scene#update()
     */
    public void update(double deltaTime, double runTime)
    { 
        return;
    }
    
    /**
     * @see Scene#draw()
     */
    public void draw(double deltaTime, double runTime)
    {
        _state.renderer.clear(10, 10, 10);
        
        _state.textRenderer.write(new Vector2(183, 100), 12, "PAUSE", TurtleColor.CYAN);
        
        // Zeichne den "Start" Button
        if (drawButton("Resume", new Vector2(213, 230), TurtleColor.WHITE)) {
            _state.scene = _gameScene;
            _state.inputHandler.setKeepMouseInPlace(true);
        }
        
        // Zeichne den "Exit" Button
        if (drawButton("Menu", new Vector2(225, 270), TurtleColor.WHITE)) {
            _state.soundEngine.clear();
            _state.scene = new TitleScene(_state);
        }
    }
    
    /**
     * Zeichnet einen Knopf an gegebener Position und gibt zurück ob gedrückt wurde.
     */
    private boolean drawButton(String text, Vector2 pos, TurtleColor color)
    {
        Vector2 mousePos = _state.inputHandler.getLocalMousePos();
        
        // Das offset von der Box beim drüberhovern
        int p = (int) pos.getY() - 372;
        
        final int textSize = 6;
        
        // Knopftext zeichnen
        _state.textRenderer.write(new Vector2(pos), textSize, text, color); // BUG?: write() verändert den position vektor
        
        // Box um den Text zeichnen
        if(mousePos.getX() > (pos.getX() - 10) && mousePos.getX() < (pos.getX() + text.length() * 14) && mousePos.getY() > (pos.getY() - 10) && mousePos.getY() < (pos.getY() + 25))
        {
            _state.renderer.drawLine(new Vector2(231, 364+p), new Vector2(269, 364+p), TurtleColor.MAGENTA);
            _state.renderer.drawLine(new Vector2(269, 364+p), new Vector2(304, 370+p), TurtleColor.MAGENTA);
            _state.renderer.drawLine(new Vector2(304, 370+p), new Vector2(304, 390+p), TurtleColor.MAGENTA);
            _state.renderer.drawLine(new Vector2(304, 390+p), new Vector2(269, 396+p), TurtleColor.MAGENTA);
            _state.renderer.drawLine(new Vector2(269, 396+p), new Vector2(231, 396+p), TurtleColor.MAGENTA);
            _state.renderer.drawLine(new Vector2(231, 396+p), new Vector2(196, 390+p), TurtleColor.MAGENTA);
            _state.renderer.drawLine(new Vector2(196, 390+p), new Vector2(196, 370+p), TurtleColor.MAGENTA);
            _state.renderer.drawLine(new Vector2(196, 370+p), new Vector2(231, 364+p), TurtleColor.MAGENTA);
            
            if (_state.inputHandler.isKeyPressed(KeyCode.MOUSE_BUTTON_LEFT))
            {
                return true;
            }
        }
        
        return false;
    }
}
