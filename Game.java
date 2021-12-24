import java.util.*;
import javafx.util.*;

/**
 * Diese Klasse enthält die Kernlogik des Spiels und den Gameloop
 * 
 * @author Lasse Huber-Saffer, Sven Schreiber, Nico Hädicke
 * @version 01.12.2021
 */
public class Game
{
    private GameState _state;

    
    // FPS-Berechnung
    private double _fps;
    private double fpsTimer = 0.0; 
    private TimeManager _frameCapTimeManager;
    public static final double STATIC_FPS_CAP = 60.0;
    // Wenn aktiv: FPS werden auf den Wert von STATIC_FPS_CAP begrenzt.
    public static final boolean CAP_FRAMERATE = true;
    // Wenn aktiv: Framezeit wird um DYNAMIC_FPS_FACTOR * frametime erhöht, um den fertigen Frame länger anzuzeigen
    public static final boolean DYNAMIC_FPS_CAPPING = false;
    public static final double DYNAMIC_FPS_FACTOR = 1.0;

    /**
     * Konstruktor für Objekte der Klasse Game
     */
    public Game()
    {
        _state = new GameState();
        _frameCapTimeManager = new TimeManager();
    }
    
    /**
     * Startmethode des Spiels
     */
    public void start()
    {
        _state.scene = new TitleScene(_state);
        
        _state.inputHandler.loadJFrame();
        runGameLoop();
    }
    
    private void runGameLoop()
    {
        double runTime = 0.0, deltaTime = 0.0;

        // Gameloop
        while(true)
        {
            // Zeiten berechnen
            runTime = _state.timeManager.getRunTime();
            deltaTime = _state.timeManager.getDeltaTime();
            
            // FrameCap-Timer zurücksetzen
            _frameCapTimeManager.getDeltaTime();
            
            // Input-Handling
            _state.scene.handleInput(deltaTime, runTime);
            
            // Update
            _state.scene.update(deltaTime, runTime);
            
            // Render
            _state.scene.draw(deltaTime, runTime);
                               
            // Zeichnet den FPS-Zähler
            fpsTimer += deltaTime;
            if(fpsTimer > 1.0)
            {
                fpsTimer = 0;
                _fps = 1.0 / deltaTime;
            }
            _state.textRenderer.write(new Vector2(10,10), 5, "fps: " + (int)Math.round(_fps), "rot");
            
            // Bildrate auf maximal FPS_CAP (Konstante) begrenzen
            double currentFrameTime = _frameCapTimeManager.getDeltaTime();
            if(CAP_FRAMERATE)
            {
                try
                {
                    if(DYNAMIC_FPS_CAPPING)
                    {
                        Thread.sleep((long)(1000.0 * DYNAMIC_FPS_FACTOR * currentFrameTime));
                    }
                    else
                    {
                        double diff = (1.0 / STATIC_FPS_CAP) - currentFrameTime;
                        if(diff > 0.0)
                        {
                            Thread.sleep((long)(1000.0 * diff));
                        }
                    }

                }
                catch (InterruptedException ie)
                {
                    throw new RuntimeException("unhandled interrupt");
                }
            }
        }
    }
}
