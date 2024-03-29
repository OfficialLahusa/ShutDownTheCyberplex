package core;

import util.*;

/**
 * Geteilte Spielressourcen zwischen verschiedenen Szenen
 * 
 * @author Lasse Huber-Saffer
 * @version 24.12.2021
 */
public class GameState
{
    public Renderer renderer;
    public TimeManager timeManager;
    public InputHandler inputHandler;
    public TextRenderer textRenderer;
    public SoundEngine soundEngine;
    public ResourceManager resourceManager;
    public Scene scene;
    
    /**
     * Konstruiert einen neuen GameState.
     * Die Szenenkomponente muss anschließend extern gesetzt werden.
     */
    public GameState()
    {
        renderer = new Renderer();
        timeManager = new TimeManager();
        inputHandler = new InputHandler();
        textRenderer = new TextRenderer(renderer);
        soundEngine = new SoundEngine();
        resourceManager = new ResourceManager();
        scene = null;
    }
}
